package io.github.alirostom1.payflow.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.enums.Pstatus;
import io.github.alirostom1.payflow.repository.Interface.PaymentRepositoryInterface;
import io.github.alirostom1.payflow.service.Interface.PaymentServiceInterface;
import io.github.alirostom1.payflow.util.Scheduler;

public class PaymentService implements PaymentServiceInterface {
    private final PaymentRepositoryInterface paymentRepository;

    public PaymentService(PaymentRepositoryInterface paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public boolean create(Payment p) {
        try {
            boolean created = paymentRepository.create(p);
            if(created) {
                Scheduler.schedule(()->{
                    if(p.getStatus() == Pstatus.UNPAID){
                        p.setStatus(Pstatus.OVERDUE);
                        try{
                            paymentRepository.updateStatus(p);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, p.getDueDate().plusDays(3));
                return true;
            } else {
                throw new RuntimeException("Failed to create payment in the repository.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create payment: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Payment> findById(String id) {
        try {
            return paymentRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find payment by ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> getBySubId(String id) {
        try {
            return paymentRepository.findBySubscriptionId(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get payments by subscription ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> getUnpaidPayments(String subscriptionId) {
        try {
            return paymentRepository.findBySubscriptionId(subscriptionId).stream()
                    .filter(p -> p.getStatus().name().equals("UNPAID"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get unpaid payments: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Payment> getOverduePayments() {
        try {
            return paymentRepository.findAll()
                    .stream()
                    .filter(p -> p.getStatus().name().equals("OVERDUE"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to get overdue payments: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean markAsPaid(String id) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(id);
            if (paymentOpt.isPresent()) {
                if(paymentOpt.get().getStatus() == Pstatus.PAID) {
                    throw new RuntimeException("Payment is already marked as PAID.");
                }
                Payment payment = paymentOpt.get();
                payment.setStatus(Pstatus.PAID);
                payment.setPaymentDate(LocalDateTime.now());
                return paymentRepository.updateStatus(payment);
            } else {
                throw new RuntimeException("Payment not found with ID: " + id);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to mark payment as paid: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            return paymentRepository.delete(id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete payment: " + e.getMessage(), e);
        }
    }

    @Override
    public void generateMonthlyPayments() {
        
    }

}
