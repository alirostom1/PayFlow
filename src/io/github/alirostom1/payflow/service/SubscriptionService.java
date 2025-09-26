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

public class SubscriptionService implements SubscriptionServiceInterface {
    private final SubscriptionRepositoryInterface subRepository;
    private final PaymentServiceInterface paymentService;
    
    public SubscriptionService(SubscriptionRepositoryInterface subRepository, PaymentServiceInterface paymentService){
        this.paymentService = paymentService;
        this.subRepository = subRepository;
    }

    @Override
    public FixedSub createFixedSub(String service,double price,LocalDateTime startDate,int monthsEngaged){
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
                LocalDateTime.now(),
                LocalDateTime.now(),
                "Credit Card",
                Pstatus.PAID,
                id
            );
            paymentService.create(p);
            for(int i = 1; i < monthsEngaged; i++){
                Payment p2 = new Payment(
                    UUID.randomUUID().toString(),
                    p.getDueDate().plusSeconds(i),
                    null,
                    null,
                    Pstatus.UNPAID,
                    id
                );
                paymentService.create(p2);
            }
            return sub; 

        }catch(SQLException e){
            throw new RuntimeException("Database error while creating subscription ! ",e);
        }
    }
    public FlexSub createFlexSub(String service,double price,LocalDateTime startDate,LocalDateTime endDate){
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
            if (sub.getStatus() != Sstatus.CANCELLED) {
                throw new IllegalStateException("Only cancelled subscriptions can be deleted");
            }
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
}
