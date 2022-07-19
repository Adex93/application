package com.example.application.controllers;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.services.ApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MainControllerTest {

   @Autowired
    ObjectMapper objectMapper;

   @Autowired
    MockMvc mockMvc;

    @InjectMocks
    MainController mainController;


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(mainController).build();
    }

    @Mock
    ApplicationService applicationService;
    @Test
    void addNewApplication() throws Exception {

        LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();
        loanApplicationRequestDTO.setTerm(24);
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(2000000));
        loanApplicationRequestDTO.setFirstName("Aleksandr");
        loanApplicationRequestDTO.setLastName("Dmitriev");
        loanApplicationRequestDTO.setMiddleName("Sergeevich");
        loanApplicationRequestDTO.setEmail("dmitriev_alexandr93@mail.ru");
        loanApplicationRequestDTO.setBirthdate(LocalDate.of(1993, 7, 28));
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setPassportNumber("123456");
        when(applicationService.getResponseListLoanDTO(any())).thenReturn(new ArrayList<>());
        mockMvc.perform(MockMvcRequestBuilders.post("/application")
                        .content(objectMapper.writeValueAsString(loanApplicationRequestDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void addClientOffer() throws Exception{
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(10L);
        loanOfferDTO.setRequestedAmount(BigDecimal.valueOf(2000000));
        loanOfferDTO.setTotalAmount(BigDecimal.valueOf(2060000));
        loanOfferDTO.setTerm(24);
        loanOfferDTO.setMonthlyPayment(BigDecimal.valueOf(100864.01));
        loanOfferDTO.setRate(BigDecimal.valueOf(16));
        loanOfferDTO.setIsInsuranceEnabled(true);
        loanOfferDTO.setIsSalaryClient(true);

        doNothing().when(applicationService).putDeal(any(LoanOfferDTO.class));
        mockMvc.perform(MockMvcRequestBuilders.put("/application/offer")
                        .content(objectMapper.writeValueAsString(loanOfferDTO)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}