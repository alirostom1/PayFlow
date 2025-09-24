package io.github.alirostom1.payflow.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.alirostom1.payflow.model.entity.Payment;
import io.github.alirostom1.payflow.model.enums.Pstatus;
import io.github.alirostom1.payflow.repository.Interface.Repository;

public class PaymentRepository implements Repository<Payment> {

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
            stmt.setTimestamp(4, Timestamp.valueOf(p.getPaymentDate()));
            stmt.setString(5, p.getPaymentType());
            stmt.setString(6, p.getStatus().toString());
            stmt.executeUpdate();
            return true;
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
                                    rs.getTimestamp("paymentDate").toLocalDateTime(),
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
        
        String query = "UPDATE payments set status = ? , paymentDate = ? where id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1,e.getStatus().toString());
            stmt.setTimestamp(2,Timestamp.valueOf(e.getPaymentDate()));
            stmt.setString(3,e.getId());
            stmt.executeUpdate();
            return true;
        }
    }
    
}
