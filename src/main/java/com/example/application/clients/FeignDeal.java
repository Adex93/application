package com.example.application.clients;

import com.example.application.dto.LoanApplicationRequestDTO;
import com.example.application.dto.LoanOfferDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "feignOffers", url = "${custom.application.clients.url}")
public interface FeignDeal {

    @PostMapping(value = "/application")
    ResponseEntity<List<LoanOfferDTO>> getListLoanDTO(@Valid @RequestBody LoanApplicationRequestDTO loanApplicationRequestDTOBody);

    @PutMapping(value = "/offer")
    void saveLoanOfferDTOinDeal(@Valid @RequestBody LoanOfferDTO loanOfferDTO);

}


