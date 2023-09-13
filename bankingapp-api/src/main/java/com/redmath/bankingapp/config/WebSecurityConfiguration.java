package com.redmath.bankingapp.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.redmath.bankingapp.user.UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.util.WebUtils;


import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration{
    @Value("${spring.web.security.ignored:/error,/ui/**,/favicon.ico,/swagger-ui/**,/v3/api-docs,/v3/api-docs/**}")
    private String[] ignored = { "/error", "/ui/**", "/favicon.ico", "/swagger-ui/**", "/v3/api-docs",
            "/v3/api-docs/**" };

    //---new---
    @Value("${spring.web.security.session.cookie.name:JWTOKEN}")
    private String jwToken = "JWTOKEN";

    @Value("${spring.web.security.jwt.secret.key:fBnKDJkuDDBeejkgYCK+zz4pcyc+bfrYeTTkOqyj7Uo}")
    private String secretKey = "fBnKDJkuDDBeejkgYCK+zz4pcyc+bfrYeTTkOqyj7Uo";

    @Value("${spring.web.security.session.expiry.seconds:28800}")
    private int sessionExpirySeconds = 28800;

    private final UserService userService;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;



    public WebSecurityConfiguration(UserService userService) {
        this.userService = userService;
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(secretKey), "RSA");
        this.jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(secretKeySpec));
        this.jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
    }
    //---new---

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> {
            for (String ignore : ignored) {
                web.ignoring().requestMatchers(AntPathRequestMatcher.antMatcher(ignore));
            }
            web.ignoring().requestMatchers(
                    new AntPathRequestMatcher("/h2-console/**", "GET"),
                    new AntPathRequestMatcher("/h2-console/**", "POST"),
                    new AntPathRequestMatcher("/swagger-ui/**", "GET"),
                    new AntPathRequestMatcher("/swagger-ui/**", "POST"),
                    new AntPathRequestMatcher("/swagger-ui/**", "DELETE")
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin(config -> config.successHandler(authenticationSuccessHandler()));

        http.logout(config -> config.deleteCookies("JWTOKEN").logoutSuccessHandler((request, response, auth) -> {}));


        CookieCsrfTokenRepository csrfRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        csrfRepository.setCookiePath("/");
        http.csrf(config -> config.csrfTokenRepository(csrfRepository)
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .sessionAuthenticationStrategy(new NullAuthenticatedSessionStrategy())
        );

        http.authorizeHttpRequests(config -> config
                .requestMatchers(AntPathRequestMatcher.antMatcher("/actuator/**")).hasAuthority("ADMIN")
                .anyRequest().authenticated());

        http.sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS));    //---new---
        http.oauth2ResourceServer(config -> config.opaqueToken(Customizer.withDefaults()));     //---new---

        return http.build();
    }

    private AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, auth) -> response.addCookie(createSessionCookie(encode(auth)));
    }

    private Cookie createSessionCookie(String token) {
        Cookie cookie = new Cookie(jwToken, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        //cookie.setPath("/");                      //don't enable this (as we have already set cookie path above), otherwise cookie will not delete
        return cookie;
    }

    private String encode(Authentication auth) {
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(auth.getName())
                .id(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(sessionExpirySeconds).toInstant(ZoneOffset.UTC))
                .build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
        return jwt.getTokenValue();
    }

    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return request -> resolveBearerToken(WebUtils.getCookie(request, jwToken));
    }

    private String resolveBearerToken(Cookie cookie) {
        String token = null;
        if (cookie != null) {
            token = cookie.getValue();
        }
        return token;
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return token -> introspectorToken(token);
    }

    private OAuth2AuthenticatedPrincipal introspectorToken(String token) {
        try {
            Jwt jwt = jwtDecoder.decode(token);
            UserDetails userDetails = userService.loadUserByUsername(jwt.getId(), jwt.getSubject());
            return new DefaultOAuth2User(userDetails.getAuthorities(), Map.of("sub", userDetails.getUsername()), "sub");
        } catch (Exception e) {
            throw new CredentialsExpiredException(e.getMessage(), e);
        }
    }

}
