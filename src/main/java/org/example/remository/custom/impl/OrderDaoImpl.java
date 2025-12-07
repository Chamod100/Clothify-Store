package org.example.remository.custom.impl;

import org.example.Model.DTO.CR.OrderCR;
import org.example.Model.DTO.OrderDTO;
import org.example.remository.custom.OrderDao;

import java.util.List;

public class OrderDaoImpl implements OrderDao {

    @Override
    public boolean save(OrderDTO dto) {
        return false;
    }

    @Override
    public boolean update(OrderDTO dto , Integer Id) {
        return false;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    @Override
    public List<OrderCR> findAll() {
        return null;
    }

    @Override
    public Integer findLastId() {
        return null;
    }

    @Override
    public OrderDTO find(Integer integer) {
        return null;
    }
}
