package com.example.application.services;

import com.example.application.clients.FeignDeal;
import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.myExceptions.ConnectionException;
import com.example.application.myExceptions.ScoringException;
import feign.RetryableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    FeignDeal feignDeal;

    LoanApplicationRequestDTO loanApplicationRequestDTO = new LoanApplicationRequestDTO();

    @BeforeEach
    void setLoanApplicationRequestDTO() {
        loanApplicationRequestDTO.setTerm(24);
        loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(2000000));
        loanApplicationRequestDTO.setFirstName("Aleksandr");
        loanApplicationRequestDTO.setLastName("Dmitriev");
        loanApplicationRequestDTO.setMiddleName("Sergeevich");
        loanApplicationRequestDTO.setEmail("dmitriev_alexandr93@mail.ru");
        loanApplicationRequestDTO.setBirthdate(LocalDate.of(1993, 7, 28));
        loanApplicationRequestDTO.setPassportSeries("1234");
        loanApplicationRequestDTO.setPassportNumber("123456");
    }


    @Test
    void getResponseListLoanDTO() {
        ApplicationService applicationService = new ApplicationService(feignDeal);
        List<LoanOfferDTO> list = new ArrayList<>();

        when(feignDeal.getListLoanDTO(any())).thenReturn(new ResponseEntity<>(list, HttpStatus.OK));
        List<LoanOfferDTO> resultTest = applicationService.getResponseListLoanDTO(loanApplicationRequestDTO);
        verify(feignDeal, times(1)).getListLoanDTO(any());
    }

    @Test
    void getResponseListLoanDTOShouldThrowException() {
        ApplicationService applicationService = new ApplicationService(feignDeal);
        try {
            loanApplicationRequestDTO.setTerm(5);
            List<LoanOfferDTO> resultTest = applicationService.getResponseListLoanDTO(loanApplicationRequestDTO);
        } catch (Exception e) {
            assertEquals(ScoringException.class, e.getClass());
        }
        try {
            loanApplicationRequestDTO.setTerm(6);
            loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(9999));
            List<LoanOfferDTO> resultTest = applicationService.getResponseListLoanDTO(loanApplicationRequestDTO);
        } catch (Exception e) {
            assertEquals(ScoringException.class, e.getClass());
        }
        try {
            loanApplicationRequestDTO.setAmount(BigDecimal.valueOf(10000));
            loanApplicationRequestDTO.setBirthdate(LocalDate.now().minusYears(17));
            List<LoanOfferDTO> resultTest = applicationService.getResponseListLoanDTO(loanApplicationRequestDTO);
        } catch (Exception e) {
            assertEquals(ScoringException.class, e.getClass());
        }
    }

    @Test
    void getResponseListLoanDTOConnectionException() {

        ApplicationService applicationService = new ApplicationService(feignDeal);
        try {
            doThrow(RetryableException.class).when(feignDeal).getListLoanDTO(any());
            applicationService.getResponseListLoanDTO(loanApplicationRequestDTO);
        } catch (RuntimeException e) {
            assertEquals(ConnectionException.class, e.getClass());
        }
    }

    @Test
    void putDeal() {
        ApplicationService applicationService = new ApplicationService(feignDeal);
        LoanOfferDTO loanOfferDTO = new LoanOfferDTO();
        loanOfferDTO.setApplicationId(10L);
        loanOfferDTO.setRequestedAmount(BigDecimal.valueOf(2000000));
        loanOfferDTO.setTotalAmount(BigDecimal.valueOf(2060000));
        loanOfferDTO.setTerm(24);
        loanOfferDTO.setMonthlyPayment(BigDecimal.valueOf(100864.01));
        loanOfferDTO.setIsInsuranceEnabled(true);
        loanOfferDTO.setIsSalaryClient(true);

        doNothing().when(feignDeal).saveLoanOfferDTOinDeal(any());
        applicationService.putDeal(loanOfferDTO);

        verify(feignDeal, times(1)).saveLoanOfferDTOinDeal(any());
    }

    @Test
    void putDealConnectionException() {

        ApplicationService applicationService = new ApplicationService(feignDeal);
        try {
            doThrow(RetryableException.class).when(feignDeal).saveLoanOfferDTOinDeal(any());
            applicationService.putDeal(new LoanOfferDTO());
        } catch (RuntimeException e) {
            assertEquals(ConnectionException.class, e.getClass());
        }
    }

}