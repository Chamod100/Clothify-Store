package org.example.remository.custom;

import org.example.Model.DTO.CR.ItemCR;
import org.example.Model.DTO.ItemDTO;
import org.example.remository.CrudDao;

import java.sql.SQLException;
import java.util.List;

public interface ItemDao extends CrudDao<ItemDTO,Integer> {
    List<ItemCR> findAll() throws SQLException, ClassNotFoundException;
}
