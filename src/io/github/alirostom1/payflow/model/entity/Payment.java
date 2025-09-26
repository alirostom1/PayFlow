package io.github.alirostom1.payflow.model.entity;

import java.time.LocalDateTime;

import io.github.alirostom1.payflow.model.enums.Pstatus;

public class Payment {
    private String id;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private String paymentType;
    private Pstatus status;
    private String subId;

    public Payment(){}

    public Payment(String id,LocalDateTime dueDate,LocalDateTime paymentDate,String paymentType,Pstatus status,String subId){
        this.id = id;
        this.dueDate = dueDate;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType;
        this.status = status;
        this.subId = subId;
    }
    public String getId(){
        return this.id;
    }
    public void setId(String id){
        this.id = id;
    }
    public LocalDateTime getDueDate(){
        return this.dueDate;
    }
    public void setDueDate(LocalDateTime dueDate){
        this.dueDate =  dueDate;
    }
    public LocalDateTime getPaymentDate(){
        return this.paymentDate;
    }
    public void setPaymentDate(LocalDateTime paymentDate){
        this.paymentDate = paymentDate;
    }
    public String getPaymentType(){
        return this.paymentType;
    }
    public void setPaymentType(String paymentType){
        this.paymentType = paymentType;
    }
    public Pstatus getStatus(){
        return this.status;
    }
    public void setStatus(Pstatus status){
        this.status = status;
    }
    public String getSubId(){
        return this.subId;
    }
    public void setSubId(String subId){
        this.subId = subId;
    }
}
