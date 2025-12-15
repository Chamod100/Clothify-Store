package org.example.controller;

import org.example.remository.DaoFactory;
import org.example.remository.custom.ItemDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;

public class AddItemsFormController {

    public AnchorPane LodeFormContent;

    ItemDao itemDao = DaoFactory.getDaoFactory().getDaoType(DaoFactory.DaoType.ITEM);

    public void backBtnOnAction(ActionEvent actionEvent) throws IOException {
        URL resource = this.getClass().getResource("/view/dashbord_form.fxml");

        assert resource != null;

        Parent load = FXMLLoader.load(resource);
        this.LodeFormContent.getChildren().clear();
        this.LodeFormContent.getChildren().add(load);
    }
}
