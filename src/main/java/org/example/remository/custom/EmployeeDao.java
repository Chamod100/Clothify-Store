package org.example.remository.custom;

import org.example.Model.DTO.TM.EmployeeCR;
import org.example.Model.DTO.EmployeeDTO;
import org.example.remository.CrudDao;
import javafx.collections.ObservableList;

import java.sql.ResultSet; // ⬅️ ResultSet import කරන්න
import java.sql.SQLException;

public interface EmployeeDao extends CrudDao<EmployeeDTO,Integer> {

    // ⬅️ NEW: Employee ID Auto-Generation සඳහා Method එක එකතු කරන්න
    ResultSet generateNextId() throws SQLException, ClassNotFoundException;

    ObservableList<EmployeeCR> findAll() throws SQLException, ClassNotFoundException;
}