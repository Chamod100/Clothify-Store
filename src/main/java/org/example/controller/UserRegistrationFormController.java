package org.example.controller;

import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.Utill.CrudUtill;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.net.URL;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class UserRegistrationFormController implements Initializable {

    public JFXTextField txtAdminUserName;
    public JFXPasswordField txtAdminPassword;
    public JFXCheckBox checkBoxAdminPassword;
    public JFXButton btnSend;
    public JFXTextField txtUserName;
    public JFXTextField txtEmail;
    public JFXTextField txtOtp;
    public JFXButton verifyBtn;
    public JFXComboBox<String> cmbUserType;
    public JFXPasswordField txtUserPassword;
    public JFXPasswordField txtConformUserPassword;
    public JFXButton btnCreate;
    public JFXCheckBox checkBoxUserPassword;

    StringProperty pass1 = new SimpleStringProperty("");
    StringProperty pass2 = new SimpleStringProperty("");

    char[] otp;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        txtUserName.setDisable(true);
        txtEmail.setDisable(true);
        txtOtp.setDisable(true);
        btnSend.setDisable(true);
        verifyBtn.setVisible(false);
        cmbUserType.setDisable(true);
        txtUserPassword.setDisable(true);
        txtConformUserPassword.setDisable(true);
        btnCreate.setDisable(true);

        cmbUserType.getItems().addAll("Admin", "User");

        AtomicReference<String> v1 = new AtomicReference<>("");
        AtomicReference<String> v2 = new AtomicReference<>("");

        txtUserPassword.textProperty().bindBidirectional(pass1);
        txtConformUserPassword.textProperty().bindBidirectional(pass2);

        pass1.addListener((ob, oldV, newV) -> {
            v1.set(newV);
            checkPasswords(v1, v2);
        });

        pass2.addListener((ob, oldV, newV) -> {
            v2.set(newV);
            checkPasswords(v1, v2);
        });
    }

    private void checkPasswords(AtomicReference<String> p1, AtomicReference<String> p2) {
        if (p1.get().isEmpty() || p2.get().isEmpty()) {
            btnCreate.setDisable(true);
        } else if (p1.get().equals(p2.get())) {
            btnCreate.setDisable(false);
        } else {
            btnCreate.setDisable(true);
        }
    }

    public void backBtnOnAction(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_form.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            Stage current = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            current.close();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnAdminCheckOnAction(ActionEvent actionEvent) {
        try {
            ResultSet rs = CrudUtill.execute(
                    "SELECT user_name, password FROM user WHERE user_type='Admin' AND user_name=? AND password=?",
                    txtAdminUserName.getText(),
                    txtAdminPassword.getText()
            );

            if (rs.next()) {
                new Alert(Alert.AlertType.INFORMATION, "Admin Verified!").show();

                txtUserName.setDisable(false);
                txtEmail.setDisable(false);
                txtOtp.setDisable(false);
                btnSend.setDisable(false);

            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Admin Credentials!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnCreateOnAction(ActionEvent actionEvent) {

        if (!txtUserPassword.getText().equals(txtConformUserPassword.getText())) {
            new Alert(Alert.AlertType.ERROR, "Passwords do not match!").show();
            return;
        }

        String hashPw = BCrypt.hashpw(txtUserPassword.getText(), BCrypt.gensalt(12));

        try {
            boolean ok = CrudUtill.execute(
                    "INSERT INTO user (user_name, email, password, user_type) VALUES (?, ?, ?, ?)",
                    txtUserName.getText(),
                    txtEmail.getText(),
                    hashPw,
                    cmbUserType.getSelectionModel().getSelectedItem()
            );

            if (ok) {
                new Alert(Alert.AlertType.INFORMATION, "User Registered Successfully!").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "Registration Failed!").show();
            }

        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void btnOtpOnAction(ActionEvent actionEvent) {
        sendEmail();
    }

    public char[] generateOtp(int len) {
        String nums = "0123456789";
        Random r = new Random();
        char[] otp = new char[len];
        for (int i = 0; i < len; i++) {
            otp[i] = nums.charAt(r.nextInt(nums.length()));
        }
        return otp;
    }

    public void sendEmail() {

        String sender = "sharadamarasinha@gmail.com";
        String receiver = txtEmail.getText();
        otp = generateOtp(4);
        String pw = "nixo ubxy urmo pmkh"; // Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, pw);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sender));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(receiver));
            msg.setSubject("Clothify Store Registration - OTP Code");
            msg.setText("Your OTP Code is: " + new String(otp));

            Transport.send(msg);

            new Alert(Alert.AlertType.INFORMATION, "OTP sent successfully!").show();
            verifyBtn.setVisible(true);

        } catch (MessagingException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

    }

    public void verifyBtnOnAction(ActionEvent actionEvent) {

        String correctOtp = "";
        for (char c : otp) correctOtp += c;

        if (txtOtp.getText().equals(correctOtp)) {
            new Alert(Alert.AlertType.INFORMATION, "OTP Verified!").show();

            cmbUserType.setDisable(false);
            txtUserPassword.setDisable(false);
            txtConformUserPassword.setDisable(false);

        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid OTP!").show();
        }
    }

    public void checkBoxAdminPasswordOnAction(ActionEvent actionEvent) {
        if (checkBoxAdminPassword.isSelected()) {
            txtAdminPassword.setPromptText(txtAdminPassword.getText());
            txtAdminPassword.setText("");
        } else {
            txtAdminPassword.setText(txtAdminPassword.getPromptText());
            txtAdminPassword.setPromptText("");
        }
    }

    public void checkBoxUserPasswordOnAction(ActionEvent actionEvent) {
        if (checkBoxUserPassword.isSelected()) {
            txtUserPassword.setPromptText(txtUserPassword.getText());
            txtConformUserPassword.setPromptText(txtConformUserPassword.getText());

            txtUserPassword.setText("");
            txtConformUserPassword.setText("");

        } else {
            txtUserPassword.setText(txtUserPassword.getPromptText());
            txtConformUserPassword.setText(txtConformUserPassword.getPromptText());

            txtUserPassword.setPromptText("");
            txtConformUserPassword.setPromptText("");
        }
    }
}
