package com.chhatkh.dev.controller.rest;

import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.request.RentHubReq.*;
import com.chhatkh.dev.payload.response.RentHubRes.*;
import com.chhatkh.dev.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;

/** Rental process: request -> review -> agreement -> payment -> active -> renew / end */
@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {
    private final RentalService service;

    // --- applications ---
    @PostMapping("/applications") @PreAuthorize("hasRole('TENANT')") @ResponseStatus(HttpStatus.CREATED)
    public ApplicationRes apply(@Valid @RequestBody ApplicationReq req) { return service.apply(req); }

    @GetMapping("/applications/my") @PreAuthorize("hasRole('TENANT')")
    public List<ApplicationRes> myApplications() { return service.myApplications(); }

    @PostMapping("/applications/{id}/cancel") @PreAuthorize("hasRole('TENANT')")
    public ApplicationRes cancel(@PathVariable Long id) { return service.cancelApplication(id); }

    @GetMapping("/applications/incoming") @PreAuthorize("hasRole('LANDLORD')")
    public List<ApplicationRes> incoming() { return service.incomingApplications(); }

    @PostMapping("/applications/{id}/approve") @PreAuthorize("hasRole('LANDLORD')")
    public ContractRes approve(@PathVariable Long id, @Valid @RequestBody(required = false) ApproveReq req) {
        return service.approve(id, req == null ? null : req.deposit());
    }

    @PostMapping("/applications/{id}/reject") @PreAuthorize("hasRole('LANDLORD')")
    public ApplicationRes reject(@PathVariable Long id) { return service.reject(id); }

    // --- contracts ---
    @GetMapping("/contracts/my") @PreAuthorize("hasRole('TENANT')")
    public List<ContractRes> myContracts() { return service.myContracts(); }

    @GetMapping("/contracts/landlord") @PreAuthorize("hasRole('LANDLORD')")
    public List<ContractRes> landlordContracts() { return service.landlordContracts(); }

    @GetMapping("/contracts/expiring")
    public List<ContractRes> expiring(@RequestParam(defaultValue = "30") int days) { return service.expiring(days); }

    @GetMapping("/contracts/{id}/payments")
    public List<PaymentRes> payments(@PathVariable Long id) { return service.payments(id); }

    @PostMapping("/contracts/{id}/renew")
    public ContractRes renew(@PathVariable Long id, @Valid @RequestBody RenewReq req) { return service.renew(id, req.months()); }

    @PostMapping("/contracts/{id}/end")
    public ContractRes end(@PathVariable Long id) { return service.end(id); }

    // --- payments (deposit & monthly rent) ---
    @PostMapping("/payments/{id}/pay") @PreAuthorize("hasRole('TENANT')")
    public PaymentRes pay(@PathVariable Long id, @Valid @RequestBody PayReq req) { return service.pay(id, req.method()); }
}
