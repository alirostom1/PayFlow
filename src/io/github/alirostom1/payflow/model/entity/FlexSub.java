package io.github.alirostom1.payflow.model.entity;

import java.time.LocalDateTime;

import io.github.alirostom1.payflow.model.enums.Sstatus;

public class FlexSub extends Subscription {
    
    public FlexSub(String id,String service,double price,LocalDateTime startDate,LocalDateTime endDate,Sstatus status){
        super(id,service,price,startDate,endDate,status);
    }


    public String toString(){
        return super.toString() + 
        "Subscription type : flexible";
    }
}
