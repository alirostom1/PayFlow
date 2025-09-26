package io.github.alirostom1.payflow.service;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


import io.github.alirostom1.payflow.model.entity.FixedSub;
import io.github.alirostom1.payflow.model.entity.FlexSub;
import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.entity.Subscription;
import io.github.alirostom1.payflow.model.enums.Pstatus;
import io.github.alirostom1.payflow.model.enums.Sstatus;
import io.github.alirostom1.payflow.repository.Interface.SubscriptionRepositoryInterface;
import io.github.alirostom1.payflow.service.Interface.PaymentServiceInterface;
import io.github.alirostom1.payflow.service.Interface.SubscriptionServiceInterface;
import io.github.alirostom1.payflow.util.Scheduler;

public class SubscriptionService implements SubscriptionServiceInterface {
    private final SubscriptionRepositoryInterface subRepository;
    private final PaymentServiceInterface paymentService;
    
    public SubscriptionService(SubscriptionRepositoryInterface subRepository, PaymentServiceInterface paymentService){
        this.paymentService = paymentService;
        this.subRepository = subRepository;
    }

    @Override
    public FixedSub createFixedSub(String service,double price,LocalDateTime startDate,int monthsEngaged,String paymentType){
        try{
            validateSubscriptionData(service,price, startDate);
            if(monthsEngaged <= 0){
                throw new IllegalArgumentException("Commitment months must be positive !");
            }
            LocalDateTime endDate = startDate.plusMonths(monthsEngaged);
            String id = UUID.randomUUID().toString();
            FixedSub sub = new FixedSub(id, service, price, startDate, endDate, Sstatus.ACTIVE, monthsEngaged);
            boolean created = subRepository.create(sub);
            if(!created){
                throw new RuntimeException("Failed to create subscription !");
            }
            Payment p = new Payment(
                UUID.randomUUID().toString(),
                startDate,
                LocalDateTime.now(),
                paymentType,
                Pstatus.PAID,
                id
            );
            paymentService.create(p);
            Scheduler.schedule(()->{
                    if(p.getStatus() == Pstatus.UNPAID){
                        try{
                            paymentService.markAsOverdue(p.getId());
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, p.getDueDate().plusDays(3));
            for(int i = 1; i < monthsEngaged; i++){
                Payment p2 = new Payment(
                    UUID.randomUUID().toString(),
                    p.getDueDate().plusMonths(i),
                    null,
                    null,
                    Pstatus.UNPAID,
                    id
                );
                paymentService.create(p2);
                Scheduler.schedule(()->{
                    if(p2.getStatus() == Pstatus.UNPAID){
                        try{
                            paymentService.markAsOverdue(p2.getId());
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, p2.getDueDate().plusDays(3));
            }
            Scheduler.schedule(()->{
                try{
                    sub.setStatus(Sstatus.SUSPENDED);
                    subRepository.updateStatus(sub);
                }catch(SQLException e){
                    e.printStackTrace();
                }
            }, endDate);
            return sub; 

        }catch(SQLException e){
            throw new RuntimeException("Database error while creating subscription ! ",e);
        }
    }
    public FlexSub createFlexSub(String service,double price,LocalDateTime startDate,LocalDateTime endDate,String paymentType){
        try{
            validateSubscriptionData(service, price, startDate);
            if(endDate != null && endDate.isBefore(startDate)){
                throw new IllegalArgumentException("end Date can not be before start date");
            }
            if(endDate == null){
                endDate = startDate.plusMonths(3); // default 3 months
            }
            String id = UUID.randomUUID().toString();
            FlexSub sub = new FlexSub(id, service, price, startDate, endDate, Sstatus.ACTIVE);
            boolean created = subRepository.create(sub);
            if(!created){
                throw new RuntimeException("Failed to create subcription!");
            }
            Payment p = new Payment(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                paymentType,
                Pstatus.PAID,
                id
            );
            paymentService.create(p);
            Scheduler.schedule(()->{
                    if(p.getStatus() == Pstatus.UNPAID){
                        try{
                            paymentService.markAsOverdue(p.getId());
                            this.suspend(id);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
            }, p.getDueDate().plusDays(3));
            return sub;
        }catch(SQLException e){
            throw new RuntimeException("Database error while creating subscription !");
        }
    }


    @Override
    public Optional<Subscription> findById(String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                throw new IllegalArgumentException("Subscription ID cannot be null or empty");
            }
            return subRepository.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding subscription by ID: " + id, e);
        }
    }

    @Override
    public List<Subscription> getAll() {
        try {
            return subRepository.findAll()
                .stream()
                .sorted((s1, s2) -> s2.getStartDate().compareTo(s1.getStartDate()))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all subscriptions", e);
        }
    }

    @Override
    public List<Subscription> getAllActive() {
        try{
            return subRepository.findAll()
                .stream()
                .filter((s) -> s.getStatus().equals(Sstatus.ACTIVE))
                .sorted((s1,s2) -> s2.getStartDate().compareTo(s1.getStartDate()))
                .collect(Collectors.toList());
        }catch(SQLException e){
            throw new RuntimeException("Error retrieving all active subscriptions", e);
        }
    }

    @Override
    public List<Subscription> findByType(String type) {
        try{
            return subRepository.findAll()
                .stream()
                .filter((s) -> type.equals("flexible") ? s instanceof FlexSub : s instanceof FixedSub)
                .sorted((s1,s2) -> s2.getStartDate().compareTo(s1.getStartDate()))
                .collect(Collectors.toList());
        }catch(SQLException e){
            throw new RuntimeException("Error retrieving all "+ type +" subscriptions", e);
        }
    }

    @Override
    public List<Subscription> findByStatus(Sstatus status) {
        try {
            return subRepository.findByStatus(status)
                .stream()
                .sorted((s1, s2) -> s2.getStartDate().compareTo(s1.getStartDate()))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Error finding subscriptions by status: " + status, e);
        }
    }

    @Override
    public Subscription update(Subscription subscription) {
        try {
            subRepository.update(subscription);
            return subscription;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while updating subscription with ID " + subscription.getId(), e);
        }
    }

    @Override
    public Subscription cancel(String id) {
        try {
            Optional<Subscription> optionalSub = subRepository.findById(id);
            if (!optionalSub.isPresent()) {
                throw new IllegalArgumentException("Subscription with ID " + id + " not found");
            }
            Subscription sub = optionalSub.get();
            if (sub.getStatus() == Sstatus.CANCELLED) {
                throw new IllegalStateException("Subscription with ID " + id + " is already canceled");
            }
            sub.setStatus(Sstatus.CANCELLED);
            subRepository.updateStatus(sub);
            return sub;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while canceling subscription with ID " + id, e);
        }
    }

    @Override
    public Subscription suspend(String id) {
        try {
            Optional<Subscription> optionalSub = subRepository.findById(id);
            if (!optionalSub.isPresent()) {
                throw new IllegalArgumentException("Subscription with ID " + id + " not found");
            }
            Subscription sub = optionalSub.get();
            if (sub.getStatus() == Sstatus.SUSPENDED) {
                throw new IllegalStateException("Subscription with ID " + id + " is already suspended");
            }
            sub.setStatus(Sstatus.SUSPENDED);
            subRepository.updateStatus(sub);
            return sub;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while suspending subscription with ID " + id, e);
        }
    }

    @Override
    public Subscription activate(String id) {
        try {
            Optional<Subscription> optionalSub = subRepository.findById(id);
            if (!optionalSub.isPresent()) {
                throw new IllegalArgumentException("Subscription with ID " + id + " not found");
            }
            Subscription sub = optionalSub.get();
            if (sub.getStatus() == Sstatus.ACTIVE) {
                throw new IllegalStateException("Subscription with ID " + id + " is already active");
            }
            sub.setStatus(Sstatus.ACTIVE);
            subRepository.updateStatus(sub);
            return sub;
        } catch (SQLException e) {
            throw new RuntimeException("Database error while activating subscription with ID " + id, e);
        }
    }

    @Override
    public boolean delete(String id) {
        try {
            Optional<Subscription> optionalSub = subRepository.findById(id);
            if (!optionalSub.isPresent()) {
                throw new IllegalArgumentException("Subscription with ID " + id + " not found");
            }
            Subscription sub = optionalSub.get();
            paymentService.getBySubId(id).forEach(
                p -> {
                    try {
                        paymentService.delete(p.getId());
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to delete payment with ID " + p.getId(), e);
                    }
                }
            );
            return subRepository.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while deleting subscription with ID " + id, e);
        }

    }
    
    private void validateSubscriptionData(String service, double price, LocalDateTime startDate) {
        if (service == null || service.trim().isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Monthly amount must be positive");
        }
        if (startDate == null || startDate.isBefore(LocalDateTime.now().minusDays(1))) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
    }
    @Override
    public void pay(String subId,String paymentType){
        try{
            Optional<Subscription> opSub = subRepository.findById(subId);
            if(!opSub.isPresent()){
                throw new RuntimeException("Couldn't find such subscription !");
            }
            Subscription sub = opSub.get();
            if(sub.getStatus().equals(Sstatus.CANCELLED)){
                throw new RuntimeException("Subscription is cancelled !");
            }
            if(sub instanceof FlexSub){
                List<Payment> payments = paymentService.getBySubId(sub.getId());
                if(payments.isEmpty()){
                    throw new RuntimeException("No payments for such subscription ! ");
                }
                if(sub.getStatus().equals(Sstatus.SUSPENDED)){
                    sub.setStatus(Sstatus.ACTIVE);
                    subRepository.updateStatus(sub);
                    Payment p = new Payment(
                        UUID.randomUUID().toString(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        paymentType,
                        Pstatus.PAID,
                        sub.getId()
                    );
                    paymentService.create(p);
                    Scheduler.schedule(()->{
                        if(p.getStatus() == Pstatus.UNPAID){
                            try{
                                paymentService.markAsOverdue(p.getId());
                                this.suspend(subId);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, p.getDueDate().plusDays(3));
                    return;
                }else{
                    Optional<Payment> payment = payments.stream()
                                    .filter(p -> p.getStatus().equals(Pstatus.UNPAID))
                                    .findFirst();
                    payment.ifPresent(p->{
                        p.setPaymentDate(LocalDateTime.now());
                        p.setPaymentType(paymentType);
                        p.setStatus(Pstatus.PAID);
                        paymentService.markAsPaid(p);
                        Payment newP = new Payment(
                            UUID.randomUUID().toString(),
                            p.getDueDate().plusMonths(1),
                            null,
                            null,
                            Pstatus.UNPAID,
                            sub.getId()
                        );
                        paymentService.create(newP);
                        Scheduler.schedule(()->{
                            if(newP.getStatus() == Pstatus.UNPAID){
                                try{
                                    paymentService.markAsOverdue(newP.getId());
                                    this.suspend(subId);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }, newP.getDueDate().plusDays(3) ); 
                    });
                    if(!payment.isPresent()){
                        throw new RuntimeException("No unpaid payments for such subscription !");
                    }
                }
            }else{
                List<Payment> payments = paymentService.getBySubId(sub.getId());
                if(payments.isEmpty()){
                    throw new RuntimeException("No payments for such subscription ! ");
                }
                Optional<Payment> payment = payments.stream()
                                    .filter(p -> p.getStatus().equals(Pstatus.UNPAID) || p.getStatus().equals(Pstatus.OVERDUE))
                                    .findFirst();
                payment.ifPresent(p->{
                    p.setPaymentDate(LocalDateTime.now());
                    p.setPaymentType(paymentType);
                    p.setStatus(Pstatus.PAID);
                    paymentService.markAsPaid(p);
                    if(sub.getEndDate().equals(p.getDueDate().plusMonths(1))){
                        sub.setStatus(Sstatus.SUSPENDED);
                        try{
                            subRepository.updateStatus(sub);
                        }catch(SQLException e){
                            throw new RuntimeException("Couldn't update subscription status !");
                        }
                    }
                });
                if(!payment.isPresent()){
                    throw new RuntimeException("No unpaid payments for such subscription !");
                }
            }
        }catch(SQLException e){
            throw new RuntimeException("Couldn't find such subscription !");
        }
    }
    @Override
    public double getTotalPaidAmount(String subId){
        try{
            Optional<Subscription> opSub = subRepository.findById(subId);
            if(!opSub.isPresent()){
                throw new RuntimeException("Couldn't find such subscription !");
            }
            Subscription sub = opSub.get();
            List<Payment> payments = paymentService.getBySubId(sub.getId());
            if(payments.isEmpty()){
                throw new RuntimeException("No payments for such subscription ! ");
            }
            double total = payments.stream()
                            .filter(p -> p.getStatus().equals(Pstatus.PAID))
                            .mapToDouble(p -> sub.getPrice())
                            .sum();
            return total;
        }catch(SQLException e){
            throw new RuntimeException("Couldn't find such subscription !");
        }
    }
}
