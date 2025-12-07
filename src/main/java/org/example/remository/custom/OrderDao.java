package org.example.remository.custom;

import org.example.Model.DTO.CR.OrderCR;
import org.example.Model.DTO.OrderDTO;
import org.example.remository.CrudDao;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao extends CrudDao<OrderDTO,Integer> {
    List<OrderCR> findAll() throws SQLException, ClassNotFoundException;
}
