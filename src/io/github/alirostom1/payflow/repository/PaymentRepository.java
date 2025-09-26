package io.github.alirostom1.payflow.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.enums.Pstatus;
import io.github.alirostom1.payflow.repository.Interface.PaymentRepositoryInterface;

public class PaymentRepository implements PaymentRepositoryInterface {

    private final Connection connection;

    public PaymentRepository(Connection connection){
        this.connection = connection;
    }


    @Override
    public boolean create(Payment p) throws SQLException {
        String query = "INSERT INTO payments(id,subscription_id,dueDate,paymentDate,paiment_type,status)" +
                        "values (?,?,?,?,?,?)";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, p.getId());
            stmt.setString(2, p.getSubId());
            stmt.setTimestamp(3, Timestamp.valueOf(p.getDueDate()));
            if (p.getPaymentDate() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(p.getPaymentDate()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setString(5, p.getPaymentType());
            stmt.setString(6, p.getStatus().toString());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    @Override
    public Optional<Payment> findById(String id) throws SQLException {
        List<Payment> payments = this.findAll();
        return payments.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public List<Payment> findAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments";
        try(Statement stmt = connection.createStatement()){
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()){
                Payment p = new Payment(
                                    rs.getString("id"),
                                    rs.getTimestamp("dueDate").toLocalDateTime(),
                                    rs.getTimestamp("paymentDate") != null ? 
                                    rs.getTimestamp("paymentDate").toLocalDateTime() : null,
                                    rs.getString("payment_type"),
                                    Pstatus.valueOf(rs.getString("status")),
                                    rs.getString("subscription_id")
                                );
                payments.add(p);
            }
            return payments;
        }
    }

    @Override
    public boolean updateStatus(Payment e) throws SQLException {
        
        String query = "UPDATE payments set status = ?  where id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1,e.getStatus().toString());
            stmt.setString(2,e.getId());
            stmt.executeUpdate();
            return true;
        }
    }
    public List<Payment> findBySubscriptionId(String subscriptionId) throws SQLException {
        String query = "SELECT * FROM payments WHERE subscription_id = ? ORDER BY dueDate";
        List<Payment> payments = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, subscriptionId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payment p = new Payment(
                                    rs.getString("id"),
                                    rs.getTimestamp("dueDate").toLocalDateTime(),
                                    rs.getTimestamp("paymentDate") != null ? 
                                    rs.getTimestamp("paymentDate").toLocalDateTime() : null,
                                    rs.getString("payment_type"),
                                    Pstatus.valueOf(rs.getString("status")),
                                    rs.getString("subscription_id")
                                ); 
                payments.add(p);
            }
            return payments;
        }
    }
    public List<Payment> findByStatus(Pstatus status) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE status = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, status.toString());
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                Payment p = new Payment(
                                    rs.getString("id"),
                                    rs.getTimestamp("dueDate").toLocalDateTime(),
                                    rs.getTimestamp("paymentDate") != null ? 
                                    rs.getTimestamp("paymentDate").toLocalDateTime() : null,
                                    rs.getString("payment_type"),
                                    Pstatus.valueOf(rs.getString("status")),
                                    rs.getString("subscription_id")
                                );
                payments.add(p);
            }
            return payments;
        }
    }
    public boolean delete(String id) throws SQLException {
        String query = "DELETE FROM payments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

}
