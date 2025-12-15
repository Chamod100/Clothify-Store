package org.example.remository.custom;

import org.example.Model.DTO.TM.OrderTM;
import org.example.Model.DTO.OrderDTO;
import org.example.remository.CrudDao;

import java.sql.SQLException;
import java.util.List;

public interface OrderDao extends CrudDao<OrderDTO,Integer> {
    List<OrderTM> findAll() throws SQLException, ClassNotFoundException;
}
