package io.github.alirostom1.payflow.service.Interface;

import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.Payment;

public interface PaymentServiceInterface {
    boolean create(Payment p);
    Optional<Payment> findById(String id);
    List<Payment> getAllPayments();
    List<Payment> getBySubId(String id);
    List<Payment> getUnpaidPayments(String subscriptionId);
    List<Payment> getOverduePayments();
    boolean markAsPaid(Payment p);
    boolean markAsOverdue(String id);
    boolean update(Payment p);
    boolean delete(String id);

    List<Payment> getLast5Payments();
    void generateMonthlyPayments();
    
}
