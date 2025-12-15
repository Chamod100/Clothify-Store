package org.example.remository.custom.impl;

import org.example.Model.DTO.ItemDTO;
import org.example.Model.DTO.TM.ItemTM;
import org.example.remository.custom.ItemDao;

import java.util.List;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean save(ItemDTO dto) {
        return false;
    }

    @Override
    public boolean update(ItemDTO dto, Integer id) {
        return false;
    }

    @Override
    public boolean delete(Integer integer) {
        return false;
    }

    @Override
    public List<ItemTM> findAll() {
        return null;
    }

    @Override
    public Integer findLastId() {
        return null;
    }

    @Override
    public ItemDTO find(Integer integer) {
        return null;
    }
}
