package io.github.alirostom1.payflow.service.Interface;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.FixedSub;
import io.github.alirostom1.payflow.model.entity.FlexSub;
import io.github.alirostom1.payflow.model.entity.Subscription;
import io.github.alirostom1.payflow.model.enums.Sstatus;

public interface SubscriptionServiceInterface{
    FlexSub createFlexSub(String service,double price,LocalDateTime startDate,LocalDateTime endDate);
    FixedSub createFixedSub(String service,double price,LocalDateTime startDate,int monthsEngaged);
    Optional<Subscription> findById(String id);
    List<Subscription> getAll();
    List<Subscription> getAllActive();
    List<Subscription> findByType(String type);
    List<Subscription> findByStatus(Sstatus status);
    
    Subscription update(Subscription sub);
    Subscription cancel(String id);
    Subscription suspend(String id);
    Subscription activate(String id);
    
    boolean delete(String id);
}
