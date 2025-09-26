package io.github.alirostom1.payflow.ui;

import java.util.Scanner;

public class MainMenuUI {
    private final Scanner scanner = new Scanner(System.in);
    private final SubscriptionUI subUI;
    private final PaymentUI payUI;


    public MainMenuUI(SubscriptionUI subUI,PaymentUI payUI) {
        this.subUI = subUI;
        this.payUI = payUI;
    }

    public void run(){
        while(true){
            System.out.println("=== Main Menu ===");
            System.out.println("1. Manage Subscriptions");
            System.out.println("2. Manage Payments");
            System.out.print("Choose an option: ");
            try{
                String choice = scanner.nextLine();
                switch(choice){
                    case "1":
                        subUI.run();
                        break;
                    case "2":
                        payUI.run();
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }catch(Exception e){
                System.out.println("Invalid input. Please try again.");
                continue;
            }
        }
    }
}
