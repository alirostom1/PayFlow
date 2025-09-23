package io.github.alirostom1.payflow.model.entity;

import java.time.LocalDateTime;

import io.github.alirostom1.payflow.model.enums.Sstatus;

public abstract class Subscription {
    private String id;
    private String service;
    private double price;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Sstatus status;

    public Subscription(){}

    public Subscription(String id,String service,double price,LocalDateTime startDate,LocalDateTime endDate,Sstatus status){
        this.id = id;
        this.service = service;
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }
    public String getService(){
        return this.service;
    }
    public void setService(String service){
        this.service = service;
    }
    public double getPrice(){
        return this.price;
    }
    public void setPrice(double price){
        this.price = price;
    }
    public LocalDateTime getStartDate(){
        return this.startDate;
    }
    public void setStartDate(LocalDateTime startDate){
        this.startDate = startDate;
    }
    public LocalDateTime getEndDate(){
        return this.endDate;
    }
    public void setEndDate(LocalDateTime endDate){
        this.endDate = endDate;
    }
    public Sstatus geStatus(){
        return this.status;
    }
    public void setStatus(Sstatus status){
        this.status = status;
    }
}
