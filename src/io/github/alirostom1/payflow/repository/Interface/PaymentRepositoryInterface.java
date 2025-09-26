package io.github.alirostom1.payflow.repository.Interface;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.enums.Pstatus;

public interface PaymentRepositoryInterface {
    boolean create(Payment p) throws SQLException;
    Optional<Payment> findById(String id) throws SQLException;
    List<Payment> findAll() throws SQLException;
    boolean updateStatus(Payment p) throws SQLException;
    List<Payment> findByStatus(Pstatus status) throws SQLException;
    List<Payment> findBySubscriptionId(String subscriptionId) throws SQLException;
    boolean delete(String id) throws SQLException;
}
