package io.github.alirostom1.payflow.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.entity.Subscription;
import io.github.alirostom1.payflow.model.enums.Pstatus;
import io.github.alirostom1.payflow.model.enums.Sstatus;
import io.github.alirostom1.payflow.service.SubscriptionService;
import io.github.alirostom1.payflow.service.Interface.PaymentServiceInterface;
import io.github.alirostom1.payflow.service.Interface.SubscriptionServiceInterface;

public class PaymentUI {
    private final Scanner scanner = new Scanner(System.in);
    private final PaymentServiceInterface paymentService;
    private final SubscriptionServiceInterface subService;

    public PaymentUI(PaymentServiceInterface paymentService,SubscriptionServiceInterface subService){
        this.paymentService = paymentService;
        this.subService = subService;
    }
    public void run(){
        while (true) {
            System.out.println("=== Payment Management ===");
            System.out.println("1. list subscription payments");
            System.out.println("2. Pay a subscription(1 month at a time)");
            System.out.println("3. Display a subscription's payments");
            System.out.println("4. List Overdue Payments with total amount due");
            System.out.println("5. Display total amount Payed by a subscription");
            System.out.println("6. Display last 5 payments made");
            System.out.println("7. display finacial reports");
            System.out.println("8. update payment");
            System.out.println("9. Delete a payment");
            System.out.println("10. Return to main menu");
            System.out.print("Choose an option: ");
                try{
                    String choice = scanner.nextLine();
                    switch (choice) {
                        case "1":
                            listPayments();
                            break;
                        case "2":
                            pay();
                            break;
                        case "3":
                            displaySubPayments();
                            break;
                        case "4":
                            listOverduePayments();
                            break;
                        case "5":
                            displayTotalPaidBySub();
                            break;
                        case "6":
                            displayLast5Payments();
                            break;
                        case "7":
                            displayFinancialReports();
                            break;
                        case "8":
                            updatePayment();
                            break;
                        case "9":
                            deletePayment();
                            break;
                        case "10":
                            System.out.println("Returning...");
                            return;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                }
            }catch(Exception e){
                System.out.println("Invalid input. Please try again.");
                continue;
            }
        }

    }
    public void listPayments(){
        try {
            System.out.print("Enter your subcription id:");
            String subId = scanner.nextLine();
            List<Payment> payments = paymentService.getBySubId(subId);
            for(int i=0;i<payments.size();i++){
                System.out.println("*******Payment n° "+ (i+1) +"*******");
                System.out.println(payments.get(i));
                if(i+1 < payments.size()){
                    System.out.println("Press anything for next payment !");
                    scanner.nextLine();
                }
            }
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error listing payments: " + e.getMessage());
        }
    }

    public void displayLast5Payments(){
        try {
            List<Payment> payments = paymentService.getLast5Payments();
            for(int i=0;i<payments.size();i++){
                System.out.println("*******Payment n° "+ (i+1) +"*******");
                System.out.println(payments.get(i));
                if(i+1 < payments.size()){
                    System.out.println("Press anything for next payment !");
                    scanner.nextLine();
                }
            }
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error displaying last 5 payments: " + e.getMessage());
        }
    }
    public void displayTotalPaidBySub(){
        try {
            System.out.print("Enter your subcription id:");
            String subId = scanner.nextLine();
            double totalPaid = subService.getTotalPaidAmount(subId);
            System.out.println("Total amount paid by subscription " + subId + ": " + totalPaid);
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error displaying total paid by subscription: " + e.getMessage());
        }
    }
    public void listOverduePayments(){
        try {
            List<Payment> payments = paymentService.getOverduePayments();
            double totalDue = payments.stream().mapToDouble(p -> {
                try {
                    Subscription sub = subService.findById(p.getSubId()).get();
                    return sub.getPrice();
                } catch (Exception e) {
                    return 0;
                }
            }).sum();
            System.out.println("Total amount due for overdue payments: " + totalDue);
            for(int i=0;i<payments.size();i++){
                System.out.println("*******Payment n° "+ (i+1) +"*******");
                System.out.println(payments.get(i));
                if(i+1 < payments.size()){
                    System.out.println("Press anything for next payment !");
                    scanner.nextLine();
                }
            }
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error listing overdue payments: " + e.getMessage());
        }
    }
    public void displayFinancialReports() {
        try {
            List<Payment> allPayments = paymentService.getAllPayments();
            LocalDateTime now = LocalDateTime.now();
    
            long monthlyPaid = allPayments.stream()
                .filter(p -> p.getStatus().name().equals("PAID"))
                .filter(p -> p.getPaymentDate() != null &&
                    p.getPaymentDate().getYear() == now.getYear() &&
                    p.getPaymentDate().getMonth() == now.getMonth())
                .count();
    
            long yearlyPaid = allPayments.stream()
                .filter(p -> p.getStatus().name().equals("PAID"))
                .filter(p -> p.getPaymentDate() != null &&
                    p.getPaymentDate().getYear() == now.getYear())
                .count();
    
            long unpaid = allPayments.stream()
                .filter(p -> p.getStatus().name().equals("UNPAID"))
                .count();
    
            System.out.println("Monthly payments done: " + monthlyPaid);
            System.out.println("Annual payments done: " + yearlyPaid);
            System.out.println("Payments not paid: " + unpaid);
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error displaying financial reports: " + e.getMessage());
        }
    }






    public void pay(){
        try {
            System.out.print("Enter your subscription id: ");
            String subId = scanner.nextLine();
            System.out.print("Enter type of payment:");
            String paymentType = scanner.nextLine();
            subService.pay(subId,paymentType);
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error processing payment: " + e.getMessage());
        }
    }
    public void displaySubPayments(){
        try {
            System.out.print("Enter your subscription id: ");
            String subId = scanner.nextLine();
            List<Payment> payments = paymentService.getBySubId(subId);
            for(int i=0;i<payments.size();i++){
                System.out.println("*******Payment n° "+ (i+1) +"*******");
                System.out.println(payments.get(i));
                if(i+1 < payments.size()){
                    System.out.println("Press anything for next payment !");
                    scanner.nextLine();
                }
            }
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error displaying subscription payments: " + e.getMessage());
        }
    }
    public void updatePayment(){
        try {
            System.out.print("Enter payment ID to update: ");
            String paymentId = scanner.nextLine();
            Optional<Payment> optionalPayment = paymentService.findById(paymentId);
            if(!optionalPayment.isPresent()){
                System.out.println("Payment with ID " + paymentId + " not found.");
                return;
            }
            Payment payment = optionalPayment.get();
            System.out.println("Current Payment Details:");
            System.out.println(payment);
            System.out.print("Enter new payment type (current: " + payment.getPaymentType() + "): ");
            String newPaymentType = scanner.nextLine();
            if(newPaymentType.length() != 0){
                payment.setPaymentType(newPaymentType);
            }
            System.out.print("Enter new status (PAID, UNPAID, OVERDUE) (current: " + payment.getStatus() + "): ");
            String newStatusStr = scanner.nextLine();
            if(newStatusStr.length() != 0){
                try{
                    Pstatus newStatus = Pstatus.valueOf(newStatusStr.toUpperCase());
                    payment.setStatus(newStatus);
                }catch(IllegalArgumentException e){
                    System.out.println("Invalid status. Update aborted.");
                    return;
                }
            }
            boolean success = paymentService.update(payment);
            if(success){
                System.out.println("Payment updated successfully.");
            }else{
                System.out.println("Failed to update payment.");
            }
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error updating payment: " + e.getMessage());
        }
    }
    public void deletePayment(){
        try {
            System.out.print("Enter payment ID to delete: ");
            String paymentId = scanner.nextLine();
            Optional<Payment> optionalPayment = paymentService.findById(paymentId);
            if(!optionalPayment.isPresent()){
                System.out.println("Payment with ID " + paymentId + " not found.");
                return;
            }
            Payment payment = optionalPayment.get();
            System.out.println("Payment Details:");
            System.out.println(payment);
            System.out.print("Are you sure you want to delete this payment? (yes/no): ");
            String confirmation = scanner.nextLine();
            if(confirmation.equalsIgnoreCase("yes")){
                boolean success = paymentService.delete(paymentId);
                if(success){
                    System.out.println("Payment deleted successfully.");
                }else{
                    System.out.println("Failed to delete payment.");
                }
            }else{
                System.out.println("Deletion cancelled.");
            }
            System.out.println("Press anything to continue !");
            scanner.nextLine();
        } catch (Exception e) {
            System.out.println("Error deleting payment: " + e.getMessage());
        }
    }
}
