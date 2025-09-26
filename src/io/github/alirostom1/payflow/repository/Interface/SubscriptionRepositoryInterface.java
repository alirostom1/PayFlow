package io.github.alirostom1.payflow.repository.Interface;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.Subscription;
import io.github.alirostom1.payflow.model.enums.Sstatus;

public interface SubscriptionRepositoryInterface{
    boolean create(Subscription s) throws SQLException;
    Optional<Subscription> findById(String id) throws SQLException;
    List<Subscription> findAll() throws SQLException;
    boolean updateStatus(Subscription e) throws SQLException;
    List<Subscription> findByStatus(Sstatus status) throws SQLException;
    boolean update(Subscription s) throws SQLException;
    boolean delete(String id) throws SQLException;
}
