package org.example.remository.custom.impl;

import org.example.Model.DTO.CR.EmployeeCR;
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

    @Override
    public boolean save(EmployeeDTO employee) throws SQLException, ClassNotFoundException {
        System.out.println("Repository : "+employee);
        Session session = HibernateUtill.getSession();
        session.beginTransaction();
        session.persist(new ModelMapper().map(employee, Employee.class));
        session.getTransaction().commit();
        session.close();
        return true;
    }

    @Override
    public boolean update(EmployeeDTO employee,Integer id) throws SQLException, ClassNotFoundException {
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
    public ObservableList<EmployeeCR> findAll(){
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
            list.add(modelMapper.map(employee,EmployeeCR.class));
        });
        return list;
    }

    @Override
    public Integer findLastId() {
        return null;
    }

    @Override
    public EmployeeDTO find(Integer integer) throws SQLException, ClassNotFoundException {
        ResultSet rst = CrudUtill.execute("select * from employer where id = ?",integer);
        rst.next();
        return new EmployeeDTO(
                rst.getString(2),
                rst.getString(3),
                rst.getString(4),
                rst.getString(5),
                rst.getString(6),
                rst.getString(7),
                rst.getString(8),
                rst.getString(9)
        );
    }
}
