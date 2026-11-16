package com.chhatkh.dev.services;

import com.chhatkh.dev.models.enums.PaymentMethod;
import com.chhatkh.dev.models.enums.PaymentStatus;
import com.chhatkh.dev.payload.request.RentHubReq.ApplicationReq;
import com.chhatkh.dev.payload.response.RentHubRes.ApplicationRes;
import com.chhatkh.dev.payload.response.RentHubRes.ContractRes;
import com.chhatkh.dev.payload.response.RentHubRes.PaymentRes;

import java.math.BigDecimal;
import java.util.List;

public interface RentalService {
    ApplicationRes apply(ApplicationReq req);

    List<ApplicationRes> myApplications();

    List<ApplicationRes> incomingApplications();

    ApplicationRes cancelApplication(Long id);

    ApplicationRes reject(Long id);

    ContractRes approve(Long id, BigDecimal depositOverride);

    PaymentRes pay(Long paymentId, PaymentMethod method);

    List<PaymentRes> payments(Long contractId);

    List<ContractRes> myContracts();

    List<ContractRes> landlordContracts();

    List<ContractRes> expiring(int days);

    ContractRes renew(Long contractId, int months);

    ContractRes end(Long contractId);

    ContractRes moveIn(Long contractId);

    List<PaymentRes> landlordPayments(PaymentStatus status);
}
