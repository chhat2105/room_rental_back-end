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
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService service;

    @GetMapping("/dashboard") public AdminDashboardRes dashboard() { return service.dashboard(); }

    // Users
    @GetMapping("/users")
    public Page<AdminUserRes> users(@PageableDefault(size = 20, sort = "id") Pageable pageable) { return service.users(pageable); }
    @PostMapping("/users/{id}/enable") public AdminUserRes enable(@PathVariable Long id) { return service.setEnabled(id, true); }
    @PostMapping("/users/{id}/disable") public AdminUserRes disable(@PathVariable Long id) { return service.setEnabled(id, false); }

    // Properties (verify listings). Default status = PENDING_REVIEW
    @GetMapping("/properties")
    public Page<PropertyRes> properties(@RequestParam(required = false) PropertyStatus status,
                                        @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) { return service.properties(status, pageable); }
    @PostMapping("/properties/{id}/verify") public PropertyRes verify(@PathVariable Long id) { return service.verify(id); }
    @PostMapping("/properties/{id}/reject") public PropertyRes reject(@PathVariable Long id, @Valid @RequestBody RejectReq req) { return service.reject(id, req.reason()); }
    @PostMapping("/properties/{id}/take-down") public PropertyRes takeDown(@PathVariable Long id, @Valid @RequestBody RejectReq req) { return service.takeDown(id, req.reason()); }

    // Rentals (monitor)
    @GetMapping("/rentals") public List<ContractRes> rentals(@RequestParam(required = false) ContractStatus status) { return service.rentals(status); }
    @GetMapping("/payments/overdue") public List<PaymentRes> overdue() { return service.overduePayments(); }

    // Complaints (resolve)
    @GetMapping("/complaints") public List<ComplaintRes> complaints(@RequestParam(required = false) ComplaintStatus status) { return service.complaints(status); }
    @PatchMapping("/complaints/{id}") public ComplaintRes resolve(@PathVariable Long id, @Valid @RequestBody ComplaintResolveReq req) { return service.resolve(id, req); }
}
