package org.example.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.AnchorPane;
import org.example.Utill.CrudUtill;
import org.example.Model.DTO.TM.ItemTM;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.ButtonType;

public class PlaceOrderFormController implements Initializable {
    public AnchorPane LodeFormContent;

    // Employee Fields
    public JFXComboBox<String> cmbEmployeeId;
    public JFXTextField txtEmployeeName;

    // Order and Customer Fields
    public JFXTextField txtOrderId;
    public JFXTextField txtCustomerName;
    public JFXTextField txtCustomerContact;
    public JFXTextField txtCustomerEmail;

    // Item Input Fields
    public JFXTextField txtItemCode;
    public JFXTextField txtQty;
    public JFXTextField txtQtyOnHand;
    public JFXTextField txtSellingPrice;
    public JFXTextField txtProfit;
    public JFXTextField txtType;
    public JFXTextField txtSize;
    public JFXTextField txtDiscount;
    public JFXTextField txtDescription;

    // Cash Input Field
    public JFXTextField txtCash;

    // Table View and Columns
    public TableView<ItemTM> tblOrderCart;
    public TableColumn<ItemTM, String> colItemCode;
    public TableColumn<ItemTM, String> colDescription;
    public TableColumn<ItemTM, Integer> colQty;
    public TableColumn<ItemTM, Double> colUnitPrice;
    public TableColumn<ItemTM, String> colDate;
    public TableColumn<ItemTM, Double> colDiscount;
    public TableColumn<ItemTM, String> colType;
    public TableColumn<ItemTM, String> colSize;
    public TableColumn<ItemTM, Double> colAmount;
    public TableColumn<ItemTM, Object> colOption;

    // Total/Discount Labels
    public Label lblTotal;
    public Label lblDiscount;
    public Label lblBalance;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("DEBUG: PlaceOrderFormController initialized!");

        // 1. Order ID Auto-Generate කිරීම
        generateNextOrderId();

        // 2. ⬅️ FIX: Employee IDs ComboBox එකට Load කිරීම
        loadEmployeeIds();

        // 3. Table Configuration
        setCellValueFactory();
        tblOrderCart.setItems(FXCollections.observableArrayList());
        calculateTotal();
    }

    // ------------------- Order ID Auto-Generate Logic -------------------
    private void generateNextOrderId() {
        try {
            ResultSet rst = CrudUtill.execute("SELECT orderId FROM `order` ORDER BY orderId DESC LIMIT 1");

            if (rst.next()) {
                String lastOrderId = rst.getString("orderId");
                String prefix = "ORD-";
                String numberString = lastOrderId.replaceAll("[^0-9]", "");

                int number = 0;
                if (!numberString.isEmpty()) {
                    number = Integer.parseInt(numberString);
                }
                number++;

                String nextOrderId = String.format("%s%03d", prefix, number);
                txtOrderId.setText(nextOrderId);
            } else {
                txtOrderId.setText("ORD-001");
            }
        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error generating Order ID: " + e.getMessage()).show();
            txtOrderId.setText("ID Generation Failed");
        }
    }

    // ------------------- Employee Logic (FIX: Now called in initialize) -------------------
    private void loadEmployeeIds() {
        try {
            // Assuming your employee table uses empId
            ResultSet rst = CrudUtill.execute("SELECT empId FROM employee");
            ObservableList<String> idList = FXCollections.observableArrayList();

            while (rst.next()) {
                idList.add(rst.getString("empId"));
            }

            cmbEmployeeId.setItems(idList);

        } catch (SQLException | ClassNotFoundException e) {
            new Alert(Alert.AlertType.ERROR, "Error loading Employee IDs: " + e.getMessage()).show();
        }
    }

    public void cmbEmployeeIdOnAction(ActionEvent actionEvent) {
        String selectedId = cmbEmployeeId.getSelectionModel().getSelectedItem();

        if (selectedId != null) {
            try {
                ResultSet rst = CrudUtill.execute("SELECT name FROM employee WHERE empId = ?", selectedId);

                if (rst.next()) {
                    txtEmployeeName.setText(rst.getString("name"));
                } else {
                    txtEmployeeName.setText("");
                }
            } catch (SQLException | ClassNotFoundException e) {
                new Alert(Alert.AlertType.ERROR, "Error fetching Employee Name: " + e.getMessage()).show();
            }
        }
    }

    // ------------------- Cart Table Config and Remove Button Logic -------------------

    private void setCellValueFactory() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        colOption.setCellValueFactory(param -> new ReadOnlyObjectWrapper(new JFXButton("Remove")));

        colOption.setCellFactory(param -> new TableCell<ItemTM, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                JFXButton btnRemove = new JFXButton("Remove");
                btnRemove.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnRemove.setOnAction(event -> {
                    Optional<ButtonType> result = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to remove this item?", ButtonType.YES, ButtonType.NO).showAndWait();

                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        ItemTM itemToRemove = getTableView().getItems().get(getIndex());

                        tblOrderCart.getItems().remove(itemToRemove);
                        calculateTotal();
                        new Alert(Alert.AlertType.INFORMATION, itemToRemove.getItemCode() + " removed from cart.").show();
                    }
                });

                setGraphic(btnRemove);
            }
        });
    }

    // ------------------- Add To Cart Logic (වෙනස් නොවේ) -------------------

    public void addToCartOnAction(ActionEvent actionEvent) {

        String itemCode = txtItemCode.getText();
        String qtyText = txtQty.getText();
        String discountText = txtDiscount.getText().isEmpty() ? "0" : txtDiscount.getText();

        if (itemCode.isEmpty() || qtyText.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please enter both Item Code and Quantity.").show();
            return;
        }

        int qty;
        double discountPercentage;

        try {
            qty = Integer.parseInt(qtyText);
            discountPercentage = Double.parseDouble(discountText);

            if (qty <= 0) {
                new Alert(Alert.AlertType.ERROR, "Quantity must be greater than zero.").show();
                return;
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid number for Quantity or Discount.").show();
            return;
        }

        try {
            ResultSet rst = CrudUtill.execute("SELECT description, sellingPrice, type, size, quantity FROM item WHERE itemCode = ?", itemCode);

            if (rst.next()) {

                int qtyOnHand = rst.getInt("quantity");
                if (qty > qtyOnHand) {
                    new Alert(Alert.AlertType.WARNING, "Quantity requested exceeds the Quantity on Hand (" + qtyOnHand + ")").show();
                    return;
                }

                String description = rst.getString("description");
                double unitPrice = rst.getDouble("sellingPrice");
                String type = rst.getString("type");
                String size = rst.getString("size");

                double amount = qty * unitPrice * (1 - (discountPercentage / 100));
                String date = java.time.LocalDate.now().toString();

                ItemTM newCartItem = new ItemTM(
                        itemCode,
                        description,
                        qty,
                        unitPrice,
                        date,
                        discountPercentage,
                        type,
                        size,
                        amount,
                        null
                );

                ObservableList<ItemTM> currentItems = tblOrderCart.getItems();
                if (currentItems == null) {
                    currentItems = FXCollections.observableArrayList();
                }
                currentItems.add(newCartItem);
                tblOrderCart.setItems(currentItems);

                calculateTotal();

                txtItemCode.clear();
                txtQty.clear();
                txtDiscount.clear();

            } else {
                new Alert(Alert.AlertType.ERROR, "Item Code Not Found!").show();
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    // ------------------- Total/Balance Calculation Logic (වෙනස් නොවේ) -------------------

    private void calculateTotal() {
        ObservableList<ItemTM> items = tblOrderCart.getItems();
        double total = 0.0;
        double totalDiscount = 0.0;

        for (ItemTM item : items) {
            total += item.getAmount();
            double originalAmount = item.getQty() * item.getUnitPrice();
            totalDiscount += originalAmount * (item.getDiscount() / 100);
        }

        lblTotal.setText(String.format("%.2f", total));
        lblDiscount.setText(String.format("%.2f", totalDiscount));

        calculateBalance();
    }

    public void txtCashOnAction(ActionEvent actionEvent) {
        calculateBalance();
    }

    private void calculateBalance() {
        double total = 0.0;
        try {
            total = Double.parseDouble(lblTotal.getText());
        } catch (NumberFormatException e) {
            total = 0.0;
        }

        double cash = 0.0;

        try {
            if (!txtCash.getText().isEmpty()) {
                cash = Double.parseDouble(txtCash.getText());
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid cash amount entered!").show();
            txtCash.clear();
            cash = 0.0;
        }

        double balance = cash - total;

        lblBalance.setText(String.format("%.2f", balance));
    }

    // ------------------- Final Place Order Transaction Logic (Validation Added) -------------------

    public void placeOrderOnAction(ActionEvent actionEvent) {
        // Input Validation Check
        if (txtOrderId.getText().isEmpty() || cmbEmployeeId.getValue() == null || txtCustomerName.getText().isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Order ID, Employee, and Customer Name are required.").show();
            return;
        }

        if (tblOrderCart.getItems().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "The cart is empty!").show();
            return;
        }

        // Final Balance Check (Customer must pay at least Total amount)
        double total = Double.parseDouble(lblTotal.getText());
        double cash = 0.0;
        try {
            if (!txtCash.getText().isEmpty()) {
                cash = Double.parseDouble(txtCash.getText());
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Please enter a valid Cash amount.").show();
            return;
        }

        if (cash < total) {
            new Alert(Alert.AlertType.ERROR, "Cash amount is insufficient to cover the Total (" + String.format("%.2f", total) + ")").show();
            return;
        }

        String orderId = txtOrderId.getText();
        String empId = cmbEmployeeId.getValue();

        try {
            // 3.1 Insert into `order` table
            CrudUtill.execute(
                    "INSERT INTO `order` (orderId, date, totalDiscount, total, empId, customerName, customerEmail, customerContact) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    orderId,
                    java.time.LocalDate.now().toString(),
                    Double.parseDouble(lblDiscount.getText()),
                    total,
                    empId,
                    txtCustomerName.getText(),
                    txtCustomerEmail.getText(),
                    txtCustomerContact.getText()
            );

            // 3.2 Insert into `order_details` and update `item` quantity
            for (ItemTM item : tblOrderCart.getItems()) {
                // Insert into order_details
                CrudUtill.execute(
                        "INSERT INTO order_details (orderId, itemCode, qty, unitPrice, discount, amount) VALUES (?, ?, ?, ?, ?, ?)",
                        orderId,
                        item.getItemCode(),
                        item.getQty(),
                        item.getUnitPrice(),
                        item.getDiscount(),
                        item.getAmount()
                );

                // Update item quantity (Reduce stock)
                CrudUtill.execute(
                        "UPDATE item SET quantity = quantity - ? WHERE itemCode = ?",
                        item.getQty(),
                        item.getItemCode()
                );
            }

            new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully! Balance Due: " + lblBalance.getText()).show();

            // Clear the form after success
            tblOrderCart.getItems().clear();
            calculateTotal();
            txtCustomerName.clear();
            txtCustomerContact.clear();
            txtCustomerEmail.clear();
            txtCash.clear();

            // Generate the next Order ID
            generateNextOrderId();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to Place Order. A database error occurred: " + e.getMessage()).show();
        }
    }


    public void backBtnOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/dashbord_form.fxml");

        assert resource != null;

        Parent load = (Parent) FXMLLoader.load(resource);
        this.LodeFormContent.getChildren().clear();
        this.LodeFormContent.getChildren().add(load);
    }
}