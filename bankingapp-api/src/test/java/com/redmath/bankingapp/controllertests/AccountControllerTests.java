package com.redmath.bankingapp.controllertests;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
public class AccountControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testGetAllAccounts() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo(
                                "{\"content\":[{\"id\":1,\"name\":\"sara\",\"email\":\"sara@gmail.com\",\"address\":\"lahore\"}," +
                                        "{\"id\":2,\"name\":\"kamal\",\"email\":\"kamal@gmail.com\",\"address\":\"lahore\"}]}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        /*this.testDeleteAccount();       //deleting both accounts

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));*/
    }

    @Test
    @Order(2)
    public void testGetAccountById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":{\"id\":1,\"name\":\"sara\",\"email\":\"sara@gmail.com\",\"address\":\"lahore\"}}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/3")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":{\"id\":1,\"name\":\"sara\",\"email\":\"sara@gmail.com\",\"address\":\"lahore\"}}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/2")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
    }

    @Test
    @Order(3)
    public void getAccountsByNameLike() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/search")
                        .param("name","a"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/search")
                        .param("name","a")
                        .with(testUser("kamal2","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/search")
                        .param("name","sara")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":[{\"id\":1,\"name\":\"sara\",\"email\":\"sara@gmail.com\",\"address\":\"lahore\"}]}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/search")
                        .param("name","a")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo(
                                "{\"content\":[{\"id\":1,\"name\":\"sara\",\"email\":\"sara@gmail.com\",\"address\":\"lahore\"}," +
                                        "{\"id\":2,\"name\":\"kamal\",\"email\":\"kamal@gmail.com\",\"address\":\"lahore\"}]}")
                ));

        /*this.testDeleteAccount();       //deleting both accounts

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/search")
                        .param("name","sara")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));*/
    }

    @Test
    @Order(5)
    public void testCreateAccount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                        .contentType("application/json")
                        .content("{\"name\":\"ali\",\"email\":\"ali@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"ali\",\"email\":\"ali@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"ali\",\"email\":\"ali@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":{\"id\":3,\"name\":\"ali\",\"email\":\"ali@gmail.com\",\"address\":\"sialkot\"}}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"id\":3,\"name\":\"ali\",\"email\":\"ali@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
    }

    @Test
    @Order(6)
    public void testUpdateAccount() throws Exception {
       mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/2")
                        .contentType("application/json")
                        .content("{\"name\":\"saleem\",\"email\":\"saleem@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/2")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"saleem\",\"email\":\"saleem@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/2")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"saleem\",\"email\":\"saleem@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":{\"id\":2,\"name\":\"saleem\",\"email\":\"saleem@gmail.com\",\"address\":\"sialkot\"}}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/4")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"name\":\"saleem\",\"email\":\"saleem@gmail.com\",\"address\":\"sialkot\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
    }

    @Test
    @Order(7)
    public void testDeleteAccount() throws Exception {
        /*mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/2")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));*/
    }

    private RequestPostProcessor testUser(String uname, String authority) {
        return SecurityMockMvcRequestPostProcessors.user(uname).authorities(new SimpleGrantedAuthority(authority));
    }
}
