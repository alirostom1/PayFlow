package io.github.alirostom1.payflow.model.entity;

import java.time.LocalDateTime;

import io.github.alirostom1.payflow.model.enums.Sstatus;

public class FixedSub extends Subscription{
    private int monthsEngaged;
    
    public FixedSub(String id,String service,double price,LocalDateTime startDate,LocalDateTime endDate,Sstatus status,int monthsEngaged){
        super(id,service,price,startDate,endDate,status);
        this.monthsEngaged = monthsEngaged;
    }
    public String toString(){
        return super.toString() + 
        "subscription type: fixed";
    }
}
