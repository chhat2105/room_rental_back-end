package com.chhatkh.dev.services.impl;

import com.chhatkh.dev.exception.BadRequestException;
import com.chhatkh.dev.exception.ForbiddenException;
import com.chhatkh.dev.exception.ResourceNotFoundException;
import com.chhatkh.dev.models.Payment;
import com.chhatkh.dev.models.Property;
import com.chhatkh.dev.models.RentalApplication;
import com.chhatkh.dev.models.RentalContract;
import com.chhatkh.dev.models.Room;
import com.chhatkh.dev.models.enums.*;
import com.chhatkh.dev.payload.request.RentHubReq.ApplicationReq;
import com.chhatkh.dev.payload.response.RentHubRes.ApplicationRes;
import com.chhatkh.dev.payload.response.RentHubRes.ContractRes;
import com.chhatkh.dev.payload.response.RentHubRes.PaymentRes;
import com.chhatkh.dev.repository.PaymentRepository;
import com.chhatkh.dev.repository.PropertyRepository;
import com.chhatkh.dev.repository.RentalApplicationRepository;
import com.chhatkh.dev.repository.RentalContractRepository;
import com.chhatkh.dev.repository.RoomRepository;
import com.chhatkh.dev.security.CurrentUser;
import com.chhatkh.dev.services.RentalService;
import com.chhatkh.dev.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Rental process:
 * apply -> landlord approve/reject -> contract (PENDING_PAYMENT) + deposit payment
 * -> deposit paid -> contract ACTIVE + monthly rent schedule -> renew / end (COMPLETED)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RentalServiceImpl implements RentalService {
    private final RentalApplicationRepository appRepo;
    private final RentalContractRepository contractRepo;
    private final PaymentRepository paymentRepo;
    private final PropertyRepository propertyRepo;
    private final RoomRepository roomRepo;
    private final CurrentUser currentUser;

    @Override
    public ApplicationRes apply(ApplicationReq req) {
        if (!req.endDate().isAfter(req.startDate())) throw new BadRequestException("End date must be after start date");
        Property p = propertyRepo.findById(req.propertyId()).orElseThrow(() -> new ResourceNotFoundException("Property not found"));
        if (p.getStatus() != PropertyStatus.PUBLISHED && p.getStatus() != PropertyStatus.PENDING_REVIEW)
            throw new BadRequestException("Property is not available");
        Room room = null;
        if (req.roomId() != null) {
            room = roomRepo.findById(req.roomId()).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
            if (!room.getProperty().getId().equals(p.getId())) throw new BadRequestException("Room does not belong to this property");
            if (room.getStatus() != RoomStatus.AVAILABLE) throw new BadRequestException("Room is not available");
        }
        RentalApplication a = new RentalApplication();
        a.setTenant(currentUser.get());
        a.setProperty(p);
        a.setRoom(room);
        a.setStartDate(req.startDate());
        a.setEndDate(req.endDate());
        return Mapper.application(appRepo.save(a));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRes> myApplications() {
        return appRepo.findByTenantIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::application).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationRes> incomingApplications() {
        return appRepo.findByPropertyLandlordIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::application).toList();
    }

    @Override
    public ApplicationRes cancelApplication(Long id) {
        RentalApplication a = findApp(id);
        if (!a.getTenant().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your application");
        if (a.getStatus() != ApplicationStatus.PENDING) throw new BadRequestException("Only pending applications can be cancelled");
        a.setStatus(ApplicationStatus.CANCELLED);
        return Mapper.application(a);
    }

    @Override
    public ApplicationRes reject(Long id) {
        RentalApplication a = pendingOwnedApp(id);
        a.setStatus(ApplicationStatus.REJECTED);
        return Mapper.application(a);
    }

    @Override
    public ContractRes approve(Long id, BigDecimal depositOverride) {
        RentalApplication a = pendingOwnedApp(id);
        Room room = a.getRoom();
        if (room != null) {
            if (room.getStatus() != RoomStatus.AVAILABLE) throw new BadRequestException("Room is no longer available");
            room.setStatus(RoomStatus.RESERVED);
        }
        a.setStatus(ApplicationStatus.APPROVED);

        BigDecimal rent = room != null ? room.getPrice() : a.getProperty().getPrice();
        RentalContract c = new RentalContract();
        c.setApplication(a);
        c.setRent(rent);
        c.setDeposit(depositOverride != null ? depositOverride : rent);
        c.setStartDate(a.getStartDate());
        c.setEndDate(a.getEndDate());
        c.setStatus(ContractStatus.PENDING_PAYMENT);
        contractRepo.save(c);

        Payment dep = new Payment();
        dep.setContract(c);
        dep.setType(PaymentType.DEPOSIT);
        dep.setAmount(c.getDeposit());
        dep.setDueDate(LocalDate.now().plusDays(3));
        dep.setStatus(PaymentStatus.PENDING);
        paymentRepo.save(dep);
        return Mapper.contract(c);
    }

    @Override
    public PaymentRes pay(Long paymentId, PaymentMethod method) {
        Payment p = paymentRepo.findById(paymentId).orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        RentalContract c = p.getContract();
        if (!c.getApplication().getTenant().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your payment");
        if (p.getStatus() == PaymentStatus.PAID || p.getStatus() == PaymentStatus.CANCELLED)
            throw new BadRequestException("Payment is already " + p.getStatus());

        p.setStatus(PaymentStatus.PAID);
        p.setPaymentMethod(method);
        p.setPaymentDate(LocalDateTime.now());

        if (p.getType() == PaymentType.DEPOSIT && c.getStatus() == ContractStatus.PENDING_PAYMENT) {
            c.setStatus(ContractStatus.ACTIVE);
            Room room = c.getApplication().getRoom();
            if (room != null) room.setStatus(RoomStatus.OCCUPIED);
            else c.getApplication().getProperty().setStatus(PropertyStatus.RENTED);
            generateRent(c, c.getStartDate(), c.getEndDate());
        }
        return Mapper.payment(p);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRes> payments(Long contractId) {
        RentalContract c = findContract(contractId);
        assertParty(c);
        return paymentRepo.findByContractIdOrderByDueDateAsc(contractId).stream().map(Mapper::payment).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractRes> myContracts() {
        return contractRepo.findByApplicationTenantIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::contract).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractRes> landlordContracts() {
        return contractRepo.findByApplicationPropertyLandlordIdOrderByIdDesc(currentUser.get().getId()).stream().map(Mapper::contract).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContractRes> expiring(int days) {
        Long me = currentUser.get().getId();
        LocalDate today = LocalDate.now(), limit = today.plusDays(days);
        Map<Long, RentalContract> out = new LinkedHashMap<>();
        contractRepo.findByApplicationTenantIdAndStatusAndEndDateBetween(me, ContractStatus.ACTIVE, today, limit).forEach(c -> out.put(c.getId(), c));
        contractRepo.findByApplicationPropertyLandlordIdAndStatusAndEndDateBetween(me, ContractStatus.ACTIVE, today, limit).forEach(c -> out.put(c.getId(), c));
        return out.values().stream().map(Mapper::contract).toList();
    }

    @Override
    public ContractRes renew(Long contractId, int months) {
        RentalContract c = findContract(contractId);
        assertParty(c);
        if (c.getStatus() != ContractStatus.ACTIVE) throw new BadRequestException("Only ACTIVE contracts can be renewed");
        LocalDate oldEnd = c.getEndDate(), newEnd = oldEnd.plusMonths(months);
        c.setEndDate(newEnd);
        generateRent(c, oldEnd, newEnd);
        return Mapper.contract(c);
    }

    @Override
    public ContractRes end(Long contractId) {
        RentalContract c = findContract(contractId);
        assertParty(c);
        if (c.getStatus() != ContractStatus.ACTIVE && c.getStatus() != ContractStatus.PENDING_PAYMENT)
            throw new BadRequestException("Contract already closed");
        boolean started = c.getStatus() == ContractStatus.ACTIVE;
        c.setStatus(started ? ContractStatus.COMPLETED : ContractStatus.CANCELLED);

        Room room = c.getApplication().getRoom();
        if (room != null) room.setStatus(RoomStatus.AVAILABLE);
        else c.getApplication().getProperty().setStatus(PropertyStatus.PUBLISHED);

        paymentRepo.findByContractIdOrderByDueDateAsc(contractId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .forEach(p -> p.setStatus(PaymentStatus.OVERDUE == p.getStatus() ? PaymentStatus.OVERDUE : PaymentStatus.CANCELLED));
        return Mapper.contract(c);
    }

    @Override
    public ContractRes moveIn(Long contractId) {
        RentalContract c = findContract(contractId);
        if (!c.getApplication().getTenant().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your contract");
        if (c.getStatus() != ContractStatus.ACTIVE) throw new BadRequestException("Pay the deposit first – contract is not active");
        if (c.getMovedInAt() != null) throw new BadRequestException("Already moved in");
        c.setMovedInAt(LocalDateTime.now());
        return Mapper.contract(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRes> landlordPayments(PaymentStatus status) {
        return paymentRepo.findByContractApplicationPropertyLandlordIdOrderByDueDateAsc(currentUser.get().getId()).stream()
                .filter(p -> status == null || p.getStatus() == status).map(Mapper::payment).toList();
    }

    private void generateRent(RentalContract c, LocalDate from, LocalDate to) {
        for (LocalDate d = from; d.isBefore(to); d = d.plusMonths(1)) {
            Payment p = new Payment();
            p.setContract(c);
            p.setType(PaymentType.RENT);
            p.setAmount(c.getRent());
            p.setDueDate(d);
            p.setStatus(PaymentStatus.PENDING);
            paymentRepo.save(p);
        }
    }

    private RentalApplication findApp(Long id) {
        return appRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Application not found"));
    }

    private RentalContract findContract(Long id) {
        return contractRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
    }

    private RentalApplication pendingOwnedApp(Long id) {
        RentalApplication a = findApp(id);
        if (!a.getProperty().getLandlord().getId().equals(currentUser.get().getId())) throw new ForbiddenException("Not your property");
        if (a.getStatus() != ApplicationStatus.PENDING) throw new BadRequestException("Application already handled");
        return a;
    }

    private void assertParty(RentalContract c) {
        Long me = currentUser.get().getId();
        boolean tenant = c.getApplication().getTenant().getId().equals(me);
        boolean landlord = c.getApplication().getProperty().getLandlord().getId().equals(me);
        if (!tenant && !landlord) throw new ForbiddenException("You are not part of this contract");
    }
}
