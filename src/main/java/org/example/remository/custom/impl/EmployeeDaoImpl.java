package org.example.remository.custom.impl;

import org.example.Model.DTO.TM.EmployeeCR;
import org.example.Model.DTO.EmployeeDTO;
import org.example.Model.Entity.Employee;
import org.example.Utill.CrudUtill;
import org.example.Utill.HibernateUtill;
import org.example.remository.custom.EmployeeDao;
import org.hibernate.Session;
import org.modelmapper.ModelMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeDaoImpl implements EmployeeDao {

    // ⬅️ NEW METHOD: Auto-Generate ID Logic
    @Override
    public ResultSet generateNextId() throws SQLException, ClassNotFoundException {
        // We use CrudUtill here as we are querying raw data (empId string)
        // that hasn't been mapped cleanly to a single Employee Entity.
        return CrudUtill.execute("SELECT empId FROM employee ORDER BY empId DESC LIMIT 1");
    }

    @Override
    public boolean save(EmployeeDTO employee) throws SQLException, ClassNotFoundException {
        System.out.println("Repository : " + employee);
        Session session = HibernateUtill.getSession();
        session.beginTransaction();
        session.persist(new ModelMapper().map(employee, Employee.class));
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(EmployeeDTO employee, Integer id) throws SQLException, ClassNotFoundException {
        return CrudUtill.execute("UPDATE Employer set  title=?,name=?,nic=?,dateOfBirth=?,address=?,contactNo=?,bankAccNo=?,bankBranch=? WHERE id=?",
                employee.getTitle(),
                employee.getName(),
                employee.getNic(),
                employee.getDob(),
                employee.getAddress(),
                employee.getContact(),
                employee.getBankAccountNo(),
                employee.getBankBranch(),
                id
        );
    }

    @Override
    public boolean delete(Integer id) {
        Session session = HibernateUtill.getSession();
        try {
            session.beginTransaction();
            // Load the entity to be deleted
            Employee employee = session.get(Employee.class, id);
            if (employee != null) {
                session.delete(employee); // Delete the entity
                session.getTransaction().commit();
                return true;
            } else {
                System.out.println("Employee with ID " + id + " not found.");
                return false;
            }
        } catch (Exception e) {
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    @Override
    public ObservableList<EmployeeCR> findAll() {
        ObservableList<EmployeeCR> list = FXCollections.observableArrayList();
        Session session = HibernateUtill.getSession();
        session.beginTransaction();

        // Retrieve all employees using HQL
        List<Employee> employeeList = session.createQuery("from Employee", Employee.class).getResultList();

        session.getTransaction().commit();
        session.close();

        // Map Employee entities to EmployeeDto using ModelMapper
        ModelMapper modelMapper = new ModelMapper();
        employeeList.stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
        employeeList.forEach(employee -> {
            list.add(modelMapper.map(employee, EmployeeCR.class));
        });
        return list;
    }

    @Override
    public Integer findLastId() {
        return null;
    }

    @Override
    public EmployeeDTO find(Integer integer) throws SQLException, ClassNotFoundException {
        // Assuming employee table uses 'empId' as the first column, and you query by ID
        ResultSet rst = CrudUtill.execute("select * from employee where empId = ?", integer);
        rst.next();

        // ⬅️ FIX 2: rst.getString(1) යනු ID එකයි. එය DTO constructor එකේ මුලට ඇතුළත් කරන්න.
        return new EmployeeDTO(
                rst.getString(1), // empId (New)
                rst.getString(2), // name
                rst.getString(3), // title
                rst.getString(4), // nic
                rst.getString(5), // address
                rst.getString(6), // dob
                rst.getString(7), // contact
                rst.getString(8), // bankAccountNo
                rst.getString(9)  // bankBranch
        );
    }
}