package org.example.controller;

import com.jfoenix.controls.JFXTextField;
import org.example.Model.DTO.TM.EmployeeCR;
import org.example.Model.DTO.EmployeeDTO;
import org.example.remository.DaoFactory;
import org.example.remository.custom.EmployeeDao;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EmployeeRegistrationFormController implements Initializable {
    public AnchorPane LodeFormContent;
    public JFXTextField txtEmpId;
    public JFXTextField txtEmpName;
    public JFXTextField txtEmpNic;
    public JFXTextField txtEmpAddress;
    public TableView <EmployeeCR> empTable;
    public TableColumn empColId;
    public TableColumn empColName;
    public TableColumn empColNic;
    public TableColumn empColAddress;
    public TableColumn empColDob;
    public TableColumn empColContact;
    public TableColumn empColBAccNo;
    public TableColumn empColBankBranch;
    public JFXTextField txtEmpContact;
    public JFXTextField txtEmpBankAcc;
    public JFXTextField txtEmpBankBranch;
    public DatePicker dEmpDate;
    public ComboBox cmbTitle;

    EmployeeDao employeeDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.EMPLOYEE);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // ⬅️ FIX 1: Employee ID ස්වයංක්‍රීයව ජනනය කිරීම
        generateNextEmployeeId();

        empColId.setCellValueFactory(new PropertyValueFactory<>("empId"));
        empColName.setCellValueFactory(new PropertyValueFactory<>("name"));
        empColNic.setCellValueFactory(new PropertyValueFactory<>("nic"));
        empColAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        empColDob.setCellValueFactory(new PropertyValueFactory<>("dob"));
        empColContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        empColBAccNo.setCellValueFactory(new PropertyValueFactory<>("bankAccountNo"));
        empColBankBranch.setCellValueFactory(new PropertyValueFactory<>("bankBranch"));
        cmbTitle.getItems().addAll("Mr","Ms","Mrs");
        loadTable();
        empTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (null != newValue) {
                setTableValuesToTxt(newValue);
            }
        });
    }

    // ------------------- Employee ID Auto-Generate Logic -------------------

    private void generateNextEmployeeId() {
        try {
            // employeeDao.generateNextId() මඟින් "SELECT empId FROM employee ORDER BY empId DESC LIMIT 1" ක්‍රියාත්මක වන බව උපකල්පනය කරයි
            ResultSet rst = employeeDao.generateNextId();

            if (rst.next()) {
                String lastEmpId = rst.getString(1);

                String numberString = lastEmpId.replaceAll("[^0-9]", ""); // අංක පමණක් ලබා ගනී
                String prefix = lastEmpId.replaceAll("[0-9]", "");     // අකුරු පමණක් ලබා ගනී

                int number = 0;
                if (!numberString.isEmpty()) {
                    number = Integer.parseInt(numberString);
                }
                number++;

                // Format the number back to three digits (e.g., 5 -> 005)
                String nextEmpId = String.format("%s%03d", prefix, number);

                txtEmpId.setText(nextEmpId);
            } else {
                // පළමු Employee නම්, E001 ලෙස සකසයි.
                txtEmpId.setText("E001");
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error generating Employee ID: " + e.getMessage()).show();
            txtEmpId.setText("ID Generation Failed");
        }
    }

    // ------------------- UI Methods -------------------

    private void setTableValuesToTxt(EmployeeCR newValue) {
        txtEmpId.setText(String.valueOf(newValue.getEmpId()));
        txtEmpName.setText(newValue.getName());
        txtEmpNic.setText(newValue.getNic());
        txtEmpAddress.setText(newValue.getAddress());
        txtEmpContact.setText(newValue.getContact());
        txtEmpBankAcc.setText(newValue.getBankAccountNo());
        txtEmpBankBranch.setText(newValue.getBankBranch());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate initialDate = LocalDate.parse(newValue.getDob(), formatter);
        dEmpDate.setValue(initialDate);
        cmbTitle.getSelectionModel().select(newValue.getTitle());
    }

    public void backBtnOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/dashbord_form.fxml");

        assert resource != null;

        Parent load = (Parent) FXMLLoader.load(resource);
        this.LodeFormContent.getChildren().clear();
        this.LodeFormContent.getChildren().add(load);
    }

    public void btnSaveOnAction(ActionEvent actionEvent){
        // ⬅️ FIX 2: Employee ID එකත් DTO constructor එකට ඇතුළත් කරයි
        EmployeeDTO employee=new EmployeeDTO(
                txtEmpId.getText(), // Assuming DTO constructor now accepts ID
                txtEmpName.getText(),
                cmbTitle.getSelectionModel().getSelectedItem().toString(),
                txtEmpNic.getText(),
                txtEmpAddress.getText(),
                dEmpDate.getValue().toString(),
                txtEmpContact.getText(),
                txtEmpBankAcc.getText(),
                txtEmpBankBranch.getText()
        );
        try {
            boolean isAdd = employeeDao.save(employee);
            if (isAdd){
                new Alert(Alert.AlertType.INFORMATION,"Employer Add Successfully !").show();
                loadTable();
                btnClearOnAction(actionEvent); // Clear fields
                generateNextEmployeeId(); // ⬅️ NEW: ඊළඟ ID එක ජනනය කරන්න
            }else{
                new Alert(Alert.AlertType.ERROR,"Something went wrong !").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnClearOnAction(ActionEvent actionEvent) {
        txtEmpName.setText("");
        txtEmpAddress.setText("");
        txtEmpBankAcc.setText("");
        // txtEmpId.setText("");  <-- ID එක clear නොකර Auto-Generate කරයි.
        txtEmpContact.setText("");
        txtEmpNic.setText("");
        txtEmpBankBranch.setText("");
        dEmpDate.setValue(null);
        cmbTitle.setValue(null);

        generateNextEmployeeId(); // ⬅️ Clear කළ පසුත් ID එකක් තිබිය යුතුය.
    }

    public void loadTable(){

        try {
            ObservableList <EmployeeCR> all = employeeDao.findAll();
            empTable.setItems(all);
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            e.printStackTrace();
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        // ⬅️ FIX 3: ID String එකෙන් අංක පමණක් ලබා ගෙන Integer එකක් බවට පරිවර්තනය කරයි
        String idText = txtEmpId.getText();
        if (idText.isEmpty()) return; // Prevent parsing empty string
        String idString = idText.replaceAll("[^0-9]", "");
        int empId = Integer.parseInt(idString);

        EmployeeDTO employee=new EmployeeDTO(
                // ⬅️ FIX 1: DTO Constructor එකට ID එක ඇතුළත් කරන්න (String format)
                txtEmpId.getText(),
                txtEmpName.getText(),
                cmbTitle.getSelectionModel().getSelectedItem().toString(),
                txtEmpNic.getText(),
                txtEmpAddress.getText(),
                dEmpDate.getValue().toString(),
                txtEmpContact.getText(),
                txtEmpBankAcc.getText(),
                txtEmpBankBranch.getText()
        );
        try {
            boolean isUpdate = employeeDao.update(employee, empId);
            if(isUpdate){
                new Alert(Alert.AlertType.INFORMATION,"Employer Update Successfully !").show();
                loadTable();
                btnClearOnAction(actionEvent);
            }else {
                new Alert(Alert.AlertType.ERROR,"Something went wrong !").show();
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        // ⬅️ FIX 3: ID String එකෙන් අංක පමණක් ලබා ගෙන Integer එකක් බවට පරිවර්තනය කරයි
        String idText = txtEmpId.getText();
        if (idText.isEmpty()) return;
        String idString = idText.replaceAll("[^0-9]", "");
        int empId = Integer.parseInt(idString);

        try {
            boolean delete = employeeDao.delete(empId);
            if(delete){
                new Alert(Alert.AlertType.INFORMATION,"Employer DELETE Successfully !").show();
                loadTable();
                btnClearOnAction( actionEvent);
            }else {
                new Alert(Alert.AlertType.ERROR,"Something went wrong !").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void txtIdSearchOnAction(ActionEvent actionEvent) {
        // ⬅️ FIX 3: ID String එකෙන් අංක පමණක් ලබා ගෙන Integer එකක් බවට පරිවර්තනය කරයි
        String idText = txtEmpId.getText();
        if (idText.isEmpty()) return;
        String idString = idText.replaceAll("[^0-9]", "");

        try {
            int empId = Integer.parseInt(idString);
            EmployeeDTO employeeDto=employeeDao.find(empId);

            // Assuming EmployeeDTO has a getter for empId
            // txtEmpId.setText(employeeDto.getEmpId()); // If DTO includes ID

            txtEmpName.setText(employeeDto.getName());
            cmbTitle.getSelectionModel().select(employeeDto.getTitle());
            txtEmpNic.setText(employeeDto.getNic());
            txtEmpAddress.setText(employeeDto.getAddress());
            txtEmpContact.setText(employeeDto.getContact());
            txtEmpBankAcc.setText(employeeDto.getBankAccountNo());
            txtEmpBankBranch.setText(employeeDto.getBankBranch());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate initialDate = LocalDate.parse(employeeDto.getDob(), formatter);
            dEmpDate.setValue(initialDate);

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Employee ID number for search.").show();
        }
    }
}