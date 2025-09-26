package io.github.alirostom1.payflow.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import io.github.alirostom1.payflow.model.entity.Subscription;

import io.github.alirostom1.payflow.model.enums.Sstatus;
import io.github.alirostom1.payflow.service.Interface.SubscriptionServiceInterface;

public class SubscriptionUI {
    private final Scanner scanner = new Scanner(System.in);
    private final SubscriptionServiceInterface subscriptionService;

    public SubscriptionUI(SubscriptionServiceInterface subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    public void run() {
        while (true) {
            System.out.println("=== Subscription Management ===");
            System.out.println("1. Create Fixed Subscription");
            System.out.println("2. Create flexible Subscription");
            System.out.println("3. list active subscriptions");
            System.out.println("4. List suspended subcriptions");
            System.out.println("5. list cancelled subscriptions");
            System.out.println("6. Edit Subscription");
            System.out.println("7. Suspend Subscription");
            System.out.println("8. Cancel Subscription");
            System.out.println("9. Delete Subscription");
            System.out.println("10. Exit");
            System.out.print("Choose an option: ");
                try{
                    String choice = scanner.nextLine();
                    switch (choice) {
                        case "1":
                            createFixedSub();
                            break;
                        case "2":
                            createFlexSub();
                            break;
                        case "3":
                            listActiveSubs();
                            break;
                        case "4":
                            listSuspendedSubs();
                            break;
                        case "5":
                            listCancelledSubs();
                            break;
                        case "6":
                            editSub();
                            break;
                        case "7":
                            SuspendSub();
                            break;
                        case "8":
                            CancelSub();
                            break;
                        case "9":
                            DeleteSub();
                            break; 
                        case "10":
                            System.out.println("Exiting...");
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
    public void createFixedSub() {
        try {
            System.out.print("Enter service name: ");
            String name = scanner.nextLine();
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter start date (YYYY-MM-DDTHH:MM): ");
            LocalDateTime startDate = LocalDateTime.parse(scanner.nextLine());
            System.out.print("Enter duration in months: ");
            int monthsEngaged = Integer.parseInt(scanner.nextLine());
            System.out.print("Enter type of payment: ");
            String paymentType = scanner.nextLine();
            boolean success = subscriptionService.createFixedSub(name, amount, startDate, monthsEngaged,paymentType) != null;
            if (success) {
                System.out.println("Fixed subscription created successfully.");
            } else {
                System.out.println("Failed to create fixed subscription.");
            }
        } catch (DateTimeParseException dtpe) {
            System.out.println("Invalid date format. Please use YYYY-MM-DDTHH:MM");
        } catch (Exception e) {
            System.out.println("Error creating fixed subscription: " + e.getMessage());
        }
    }
    public void createFlexSub() {
        try {
            System.out.print("Enter service name: ");
            String name = scanner.nextLine();
            System.out.print("Enter amount: ");
            double amount = Double.parseDouble(scanner.nextLine());
            System.out.print("Enter start date (YYYY-MM-DDTHH:MM): ");
            LocalDateTime startDate = LocalDateTime.parse(scanner.nextLine());
            System.out.print("Enter your payment method: ");
            String paymentType = scanner.nextLine();
            boolean success = subscriptionService.createFlexSub(name, amount, startDate,null,paymentType) != null;
            if (success) {
                System.out.println("Flexible subscription created successfully.");
            }else{
                System.out.println("Failed to create flexible subscription.");
            }
        } catch (DateTimeParseException dtpe) {
            System.out.println("Invalid date format. Please use YYYY-MM-DDTHH:MM");
        }catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error creating flexible subscription: " + e.getMessage());
        }
    }
    public void listActiveSubs(){
        List<Subscription> subs = subscriptionService.findByStatus(Sstatus.ACTIVE);
        for(int i=0;i<subs.size();i++){
            System.out.println("*******subscription n°"+ (i+1) +"*******");
            System.out.println(subs.get(i));
            if(i+1 < subs.size()){
                System.out.println("Press anything for next subscription !");
                scanner.nextLine();
            }
        }
        System.out.println("Press anything to continue !");
        scanner.nextLine();
    }
    public void listSuspendedSubs(){
        List<Subscription> subs = subscriptionService.findByStatus(Sstatus.SUSPENDED);
        for(int i=0;i<subs.size();i++){
            System.out.println("*******subscription n°"+ (i+1) +"*******");
            System.out.println(subs.get(i));
            if(i+1 < subs.size()){
                System.out.println("Press anything for next subscription !");
                scanner.nextLine();
            }
        }
        System.out.println("Press anything to continue !");
        scanner.nextLine();
    }
    public void listCancelledSubs(){
        List<Subscription> subs = subscriptionService.findByStatus(Sstatus.CANCELLED);
        for(int i=0;i<subs.size();i++){
            System.out.println("*******subscription n°"+ (i+1) +"*******");
            System.out.println(subs.get(i));
            if(i+1 < subs.size()){
                System.out.println("Press anything for next subscription !");
                scanner.nextLine();
            }
        }
        System.out.println("Press anything to continue !");
        scanner.nextLine();
    }
    public void editSub(){
        try {
            System.out.print("Enter subscription ID to edit: ");
            String id = scanner.nextLine();
            Subscription sub = subscriptionService.findById(id).orElseThrow(() -> new RuntimeException("Subscription not found"));
            System.out.println("Current details: " + sub);
            System.out.print("Enter new service name (leave blank to keep current): ");
            String name = scanner.nextLine();
            if (!name.isEmpty()) {
                sub.setService(name);
            }
            System.out.print("Enter new amount (leave blank to keep current): ");
            String amountStr = scanner.nextLine();
            if (!amountStr.isEmpty()) {
                double amount = Double.parseDouble(amountStr);
                sub.setPrice(amount);
            }
            Subscription updatedSub = subscriptionService.update(sub);
            System.out.println("Subscription updated successfully: " + updatedSub);
        } catch (Exception e) {
            System.out.println("Error editing subscription: " + e.getMessage());
        }
    }
    public void SuspendSub(){
        try {
            System.out.print("Enter subscription ID to suspend: ");
            String id = scanner.nextLine();
            subscriptionService.suspend(id);
            System.out.println("Subscription suspended successfully.");
        } catch (Exception e) {
            System.out.println("Error suspending subscription: " + e.getMessage());
        }
    }
    public void CancelSub(){
        try {
            System.out.print("Enter subscription ID to cancel: ");
            String id = scanner.nextLine();
            subscriptionService.cancel(id);
            System.out.println("Subscription cancelled successfully.");
        } catch (Exception e) {
            System.out.println("Error cancelling subscription: " + e.getMessage());
        }
    }
    public void DeleteSub(){
        try {
            System.out.print("Enter subscription ID to delete: ");
            String id = scanner.nextLine();
            subscriptionService.delete(id);
            System.out.println("Subscription deleted successfully.");
        } catch (Exception e) {
            System.out.println("Error deleting subscription: " + e.getMessage());
        }
    }

}
