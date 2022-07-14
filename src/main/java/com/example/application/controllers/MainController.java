package com.example.application.controllers;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import com.example.application.services.ApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@Validated
@Tag(name = "Application controller")
public class MainController {

    final
    ApplicationService applicationService;

    public MainController(ApplicationService applicationService) throws RuntimeException {
        this.applicationService = applicationService;
    }

    @PostMapping("/application")
    public ResponseEntity<List<LoanOfferDTO>> addNewApplication(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTO) {

        log.info("Произвёлся POST запрос /application со следующим телом: " + loanApplicationRequestDTO);
        log.info("Вызвана функция getResponseListLoanDTO класса ApplicationService для формирования списка с кредитными предложениями LoanOffersDTO");
        return new ResponseEntity<>(applicationService.getResponseListLoanDTO(loanApplicationRequestDTO), HttpStatus.OK);
    }

    @PutMapping("/application/offer")
    public void addClientOffer(@Valid @RequestBody LoanOfferDTO loanOfferDTO) {
        log.info("Произвёлся POST запрос /application/offer со следующим телом: " + loanOfferDTO);
        log.info("Вызвана функция putDeal класса ApplicationService для передачи LoanOfferDTO микросервису Deal");
        applicationService.putDeal(loanOfferDTO);
    }

}
