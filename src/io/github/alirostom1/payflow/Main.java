package io.github.alirostom1.payflow;

import java.sql.Connection;
import java.time.LocalDateTime;

import io.github.alirostom1.payflow.database.DatabaseConnection;
import io.github.alirostom1.payflow.repository.PaymentRepository;
import io.github.alirostom1.payflow.repository.SubscriptionRepository;
import io.github.alirostom1.payflow.repository.Interface.PaymentRepositoryInterface;
import io.github.alirostom1.payflow.repository.Interface.SubscriptionRepositoryInterface;
import io.github.alirostom1.payflow.service.PaymentService;
import io.github.alirostom1.payflow.service.SubscriptionService;
import io.github.alirostom1.payflow.service.Interface.PaymentServiceInterface;
import io.github.alirostom1.payflow.service.Interface.SubscriptionServiceInterface;
import io.github.alirostom1.payflow.ui.SubscriptionUI;
import io.github.alirostom1.payflow.ui.MainMenuUI;
import io.github.alirostom1.payflow.ui.PaymentUI;

public class Main{
    public final static void main(String[] args){
        Connection connection = DatabaseConnection.getInstance().getConnection();
        SubscriptionRepositoryInterface subRepo = new SubscriptionRepository(connection);
        PaymentRepositoryInterface payRepo = new PaymentRepository(connection);
        PaymentServiceInterface payService = new PaymentService(payRepo);
        SubscriptionServiceInterface subService = new SubscriptionService(subRepo, payService);
        SubscriptionUI subUI = new SubscriptionUI(subService);
        PaymentUI payUI = new PaymentUI(payService, subService);
        MainMenuUI mainMenu = new MainMenuUI(subUI,payUI);
        mainMenu.run();
    }
}