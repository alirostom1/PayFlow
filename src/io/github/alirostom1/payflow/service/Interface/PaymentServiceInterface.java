package io.github.alirostom1.payflow.service.Interface;

import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.entity.Subscription;

public interface PaymentServiceInterface {
    boolean create(Payment p);
    Optional<Payment> findById(String id);
    List<Payment> getBySubId(String id);
    List<Payment> getUnpaidPayments(String subscriptionId);
    List<Payment> getOverduePayments();
    boolean markAsPaid(String id);
    boolean delete(String id);

    void generateMonthlyPayments();
    
}
