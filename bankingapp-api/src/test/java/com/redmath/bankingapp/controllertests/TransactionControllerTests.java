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
public class TransactionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Order(1)
    public void testDepositAmount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/deposit/1")
                        .contentType("application/json")
                        .content("{\"date\":\"2023-09-01\",\"description\":\"deposit\",\"amount\":\"520\",\"debitCreditIndicator\":\"CR\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/deposit/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"date\":\"2023-09-01\",\"description\":\"deposit\",\"amount\":\"520\",\"debitCreditIndicator\":\"CR\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/deposit/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"date\":\"2023-09-01\",\"description\":\"deposit\",\"amount\":\"520\",\"debitCreditIndicator\":\"CR\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }
    @Test
    @Order(2)
    public void testWithdrawAmount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/withdraw/1")
                        .contentType("application/json")
                        .content("{\"date\":\"2023-09-01\",\"description\":\"deposit\",\"amount\":\"520\",\"debitCreditIndicator\":\"CR\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/withdraw/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"date\":\"2023-09-01\",\"description\":\"deposit\",\"amount\":\"520\",\"debitCreditIndicator\":\"CR\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/withdraw/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("{\"date\":\"2023-09-01\",\"description\":\"deposit\",\"amount\":\"1000\",\"debitCreditIndicator\":\"CR\"}"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }
    @Test
    @Order(3)
    public void testTransferAmount() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/transfer/1/2")
                        .contentType("application/json")
                        .content("300"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/transfer/1/2")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("300"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/transfer/1/2")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("300"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        /*mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/transaction/transfer/1/3")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType("application/json")
                        .content("300"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));*/
    }
    @Test
    @Order(4)
    public void testGetAllTransactionsByAccountId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/allTransactions/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/allTransactions/1")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/allTransactions/3")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());*/

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/allTransactions/1")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

        /*mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction/allTransactions/1")
                        .with(testUser("kamal2","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());*/

    }
    @Test
    @Order(5)
    public void testGetAllTransactions() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction")
                        .with(testUser("sara1","USER"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError())
                .andExpect(MockMvcResultMatchers.content().string(
                        Matchers.equalTo("")
                ));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/transaction")
                        .with(testUser("admin","ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }

    private RequestPostProcessor testUser(String uname, String authority) {
        return SecurityMockMvcRequestPostProcessors.user(uname).authorities(new SimpleGrantedAuthority(authority));
    }
}
