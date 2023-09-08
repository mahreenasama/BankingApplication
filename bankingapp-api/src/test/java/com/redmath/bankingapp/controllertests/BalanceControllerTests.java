package com.redmath.bankingapp.controllertests;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BalanceControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testGetLatestBalanceByAccountId() throws Exception {
        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":{\"id\":3,\"date\":\"2023-09-01\",\"amount\":300,\"debitCreditIndicator\":\"DB\"}}")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":{\"id\":3,\"date\":\"2023-09-01\",\"amount\":300,\"debitCreditIndicator\":\"DB\"}}")
                ));*/

        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/1")
                        .with(testUser("kamal2","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));*/
    }
    @Test
    @Order(2)
    public void testGetBalanceHistoryByAccountId() throws Exception {
        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/balanceHistory/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/balanceHistory/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":[{\"id\":1,\"date\":\"2023-08-01\",\"amount\":500,\"debitCreditIndicator\":\"CR\"},{\"id\":3,\"date\":\"2023-09-01\",\"amount\":300,\"debitCreditIndicator\":\"DB\"}]}")
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/balanceHistory/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("{\"content\":[{\"id\":1,\"date\":\"2023-08-01\",\"amount\":500,\"debitCreditIndicator\":\"CR\"},{\"id\":3,\"date\":\"2023-09-01\",\"amount\":300,\"debitCreditIndicator\":\"DB\"}]}")
                ));*/

        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/balance/balanceHistory/1")
                        .with(testUser("kamal2","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));*/
    }
    private RequestPostProcessor testUser(String uname, String authority) {
        return SecurityMockMvcRequestPostProcessors.user(uname).authorities(new SimpleGrantedAuthority(authority));
    }
}
