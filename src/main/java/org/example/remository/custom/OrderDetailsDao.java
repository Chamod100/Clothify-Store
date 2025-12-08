package org.example.remository.custom;

import org.example.Model.DTO.CR.OrderDetailsCR;
import org.example.Model.DTO.OrderDetailsDTO;
import org.example.remository.CrudDao;

import java.sql.SQLException;
import java.util.List;

public interface OrderDetailsDao extends CrudDao<OrderDetailsDTO,Integer> {
    List<OrderDetailsCR> findAll() throws SQLException, ClassNotFoundException;
}
