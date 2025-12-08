package org.example.remository.custom.impl;

import org.example.Model.DTO.CR.OrderDetailsCR;
import org.example.Model.DTO.OrderDetailsDTO;
import org.example.remository.custom.OrderDetailsDao;

import java.util.List;

public class OrderDetailDaoImpl implements OrderDetailsDao {

    @Override
    public boolean save(OrderDetailsDTO dto) {
        return false;
    }

    @Override
    public boolean update(OrderDetailsDTO dto,Integer id) {
        return false;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    @Override
    public List<OrderDetailsCR> findAll() {
        return null;
    }

    @Override
    public Integer findLastId() {
        return null;
    }

    @Override
    public OrderDetailsDTO find(Integer integer) {
        return null;
    }
}
