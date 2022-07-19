package com.example.application.services;

import com.example.application.clients.FeignDeal;
import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.myExceptions.ConnectionException;
import com.example.application.myExceptions.ScoringException;
import feign.RetryableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Slf4j
@Component
public class ApplicationService {

    final
    FeignDeal feignDeal;

    public ApplicationService(FeignDeal feignConveyor) {
        this.feignDeal = feignConveyor;
    }

    public List<LoanOfferDTO> getResponseListLoanDTO(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        log.info("Вызвана функция scoring класса ApplicationService для осуществления прескоринга");
        scoring(loanApplicationRequestDTO);
        try {
            log.info("Произведен POST запрос на /deal/application MC Deal");
            return feignDeal.getListLoanDTO(loanApplicationRequestDTO).getBody();
        } catch (RetryableException e) {
            log.error("Отсутствует подключение к микросервису Deal");
            throw new ConnectionException("Отсутствует подключение к микросервису Deal");
        }
    }

    public void putDeal(LoanOfferDTO loanOfferDTO) {
        try {
            log.info("Произведен PUT запрос на /deal/offer MC Deal");
            feignDeal.saveLoanOfferDTOinDeal(loanOfferDTO);
        } catch (RetryableException e) {
            log.error("Отсутствует подключение к микросервису Deal");
            throw new ConnectionException("Отсутствует подключение к микросервису Deal");
        }

    }

    public void scoring(LoanApplicationRequestDTO loanApplicationRequestDTO) {
        if (loanApplicationRequestDTO.getTerm() < 6) {
            throw new ScoringException("Прескоринг не пройден - срок кредита должен быть не менее 6 месяцев");
        } else if (loanApplicationRequestDTO.getAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
            throw new ScoringException("Прескоринг не пройден - сумма кредита должна быть не менее 10000");
        } else if (Period.between(loanApplicationRequestDTO.getBirthdate(), LocalDate.now()).getYears() < 18) {
            throw new ScoringException("Прескоринг не пройден - возраст должен быть не менее полных 18 лет");
        }
    }

}
