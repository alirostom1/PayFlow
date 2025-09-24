package io.github.alirostom1.payflow.repository;


import java.security.cert.PKIXRevocationChecker.Option;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.alirostom1.payflow.model.entity.FixedSub;
import io.github.alirostom1.payflow.model.entity.FlexSub;
import io.github.alirostom1.payflow.model.entity.Subscription;
import io.github.alirostom1.payflow.model.enums.Sstatus;
import io.github.alirostom1.payflow.repository.Interface.Repository;

public class SubscriptionRepository implements Repository<Subscription>{
    private final Connection connection;

    public SubscriptionRepository(Connection connection){
        this.connection = connection;
    }

    @Override
    public boolean create(Subscription s) throws SQLException {
        String query = "INSERT INTO subscriptions" +
                    "(id,serviceName,monthly_amount,startDate,endDate,status,subscription_type,monthsEngaged)" +
                    "values (?,?,?,?,?,?,?,?)";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1,s.getId());
            stmt.setString(2,s.getService());
            stmt.setDouble(3,s.getPrice());
            stmt.setTimestamp(4, Timestamp.valueOf(s.getStartDate()));
            stmt.setTimestamp(5,Timestamp.valueOf(s.getEndDate()));
            stmt.setString(6, s.geStatus().toString());
            if(s instanceof FlexSub){
                stmt.setString(7,"FLEXIBLE");
                stmt.setString(8,null);
            }else{
                FixedSub fs = (FixedSub) s;
                stmt.setString(7,"FIXED");
                stmt.setInt(8,fs.getMonthsEngaged());
            }
            return true;
        }
    }

    @Override
    public Optional<Subscription> findById(String id) throws SQLException{
        List<Subscription> subs = this.findAll();
        Optional<Subscription> sub = subs.stream().filter(s -> s.getId() == id).findFirst();
        return sub;
    }

    @Override
    public List<Subscription> findAll() throws SQLException {
        List<Subscription> subs = new ArrayList<>();
        String query = "SELECT * from subscriptions";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                if(rs.getString("subscription_type").equals("FIXED")){
                    FixedSub fs = new FixedSub(
                                    rs.getString("id"),
                                    rs.getString("serviceName"),
                                    rs.getDouble("monthly_amount"),
                                    rs.getTimestamp("startDate").toLocalDateTime(),
                                    rs.getTimestamp("endDate").toLocalDateTime(),
                                    Sstatus.valueOf(rs.getString("status")),
                                    rs.getInt("monthsEngaged")
                                );
                    subs.add(fs);
                }else{
                    FlexSub fs = new FlexSub(
                                    rs.getString("id"),
                                    rs.getString("serviceName"),
                                    rs.getDouble("monthly_amount"),
                                    rs.getTimestamp("startDate").toLocalDateTime(),
                                    rs.getTimestamp("endDate").toLocalDateTime(),
                                    Sstatus.valueOf(rs.getString("status"))
                                );
                    subs.add(fs);
                }
            }
            return subs;
        }
    }

    @Override
    public boolean updateStatus(Subscription e) throws SQLException {
        String query = "Update subscriptions set status = ? where id = ?";
        try(PreparedStatement stmt = connection.prepareStatement(query)){
            stmt.setString(1, e.geStatus().toString());
            stmt.setString(2, e.getId());
            stmt.executeUpdate();
            return true;
        }
    }
    
}
