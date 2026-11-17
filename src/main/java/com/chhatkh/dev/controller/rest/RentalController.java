package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.models.enums.PaymentStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ApplicationReq;
import com.chhatkh.dev.payload.request.RentHubReq.ApproveReq;
import com.chhatkh.dev.payload.request.RentHubReq.PayReq;
import com.chhatkh.dev.payload.request.RentHubReq.RenewReq;
import com.chhatkh.dev.payload.response.RentHubRes.ApplicationRes;
import com.chhatkh.dev.payload.response.RentHubRes.ContractRes;
import com.chhatkh.dev.payload.response.RentHubRes.PaymentRes;
import com.chhatkh.dev.services.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Rental process: request -> review -> agreement -> payment -> active -> renew / end */
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService service;

    // --- applications ---
    @PostMapping("/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplicationRes apply(@Valid @RequestBody ApplicationReq req) {
        return service.apply(req);
    }

    @GetMapping("/applications/my")
    public List<ApplicationRes> myApplications() {
        return service.myApplications();
    }

    @PostMapping("/applications/{id}/cancel")
    public ApplicationRes cancel(@PathVariable Long id) {
        return service.cancelApplication(id);
    }

    @GetMapping("/applications/incoming")
    public List<ApplicationRes> incoming() {
        return service.incomingApplications();
    }

    @PostMapping("/applications/{id}/approve")
    public ContractRes approve(@PathVariable Long id, @Valid @RequestBody(required = false) ApproveReq req) {
        return service.approve(id, req == null ? null : req.deposit());
    }

    @PostMapping("/applications/{id}/reject")
    public ApplicationRes reject(@PathVariable Long id) {
        return service.reject(id);
    }

    // --- contracts ---
    @GetMapping("/contracts/my")
    public List<ContractRes> myContracts() {
        return service.myContracts();
    }

    @GetMapping("/contracts/landlord")
    public List<ContractRes> landlordContracts() {
        return service.landlordContracts();
    }

    @GetMapping("/contracts/expiring")
    public List<ContractRes> expiring(@RequestParam(defaultValue = "30") int days) {
        return service.expiring(days);
    }

    @GetMapping("/contracts/{id}/payments")
    public List<PaymentRes> payments(@PathVariable Long id) {
        return service.payments(id);
    }

    @PostMapping("/contracts/{id}/renew")
    public ContractRes renew(@PathVariable Long id, @Valid @RequestBody RenewReq req) {
        return service.renew(id, req.months());
    }

    @PostMapping("/contracts/{id}/end")
    public ContractRes end(@PathVariable Long id) {
        return service.end(id);
    }

    // --- payments (deposit & monthly rent) ---
    @PostMapping("/payments/{id}/pay")
    public PaymentRes pay(@PathVariable Long id, @Valid @RequestBody PayReq req) {
        return service.pay(id, req.method());
    }

    @PostMapping("/contracts/{id}/move-in")
    public ContractRes moveIn(@PathVariable Long id) {
        return service.moveIn(id);
    }

    @GetMapping("/payments/landlord")
    public List<PaymentRes> landlordPayments(@RequestParam(required = false) PaymentStatus status) {
        return service.landlordPayments(status);
    }
}
