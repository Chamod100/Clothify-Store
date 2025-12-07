package org.example.remository.custom;

import org.example.Model.DTO.CR.EmployeeCR;
import org.example.Model.DTO.EmployeeDTO;
import org.example.remository.CrudDao;
import javafx.collections.ObservableList;

import java.sql.SQLException;

public interface EmployeeDao extends CrudDao<EmployeeDTO,Integer> {
    ObservableList<EmployeeCR> findAll() throws SQLException, ClassNotFoundException;
}
