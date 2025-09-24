package io.github.alirostom1.payflow.repository.Interface;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Repository<E> {
    boolean create(E e) throws SQLException;
    Optional<E> findById(String id) throws SQLException;
    List<E> findAll() throws SQLException;
    boolean updateStatus(E e) throws SQLException;
}
