package com.sqlservice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MetaController {
    @FXML
    TableView metaTableView;
    @FXML
    Button allKeysButton;
    @FXML Button customerDataButton;
    @FXML Button tableConstraintButton;
    @FXML Button allTablesButton;
    @FXML Button customerColumnsButton;
    @FXML Button maxRowsButton;
    @FXML TextField searchbar;
    @FXML TextArea feedbackTextArea;
    DALAdventureWorks dalAdventureWorks = new DALAdventureWorks();


    public void onAllKeysButton(ActionEvent actionEvent){
        try {
           ResultSet resultSet = dalAdventureWorks.getAllKeys();
           AppFunctions.updateSearchableTableView(metaTableView,searchbar,resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedError(feedbackTextArea,e);
        }
    }

    public void onCustomerContentButton(ActionEvent actionEvent) {
        try {
            ResultSet resultSet = dalAdventureWorks.getCustomerContent();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);

        }catch (SQLException e){
            AppFunctions.unexpectedError(feedbackTextArea,e);
        }
    }

    public void onTableConstraintsButton(ActionEvent actionEvent) {
        try {
            ResultSet resultSet = dalAdventureWorks.getTableConstraints();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedError(feedbackTextArea,e);
        }
    }

    public void onAllTablesButton(ActionEvent actionEvent) {
        try {
            ResultSet resultSet = dalAdventureWorks.getAllTables();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);
        }catch (SQLException e) {
            AppFunctions.unexpectedError(feedbackTextArea,e);
        }
    }

    public void onCustomerColumnsButton(ActionEvent actionEvent) {
        try {
            ResultSet resultSet = dalAdventureWorks.getCustomerColumns();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);

        }catch (SQLException e){
            AppFunctions.unexpectedError(feedbackTextArea,e);
        }
    }

    public void onTableWithMostRowsButton(ActionEvent actionEvent) {
        try {
            ResultSet resultSet = dalAdventureWorks.getTableWithMostRows();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedError(feedbackTextArea,e);
        }
    }

    @FXML private AnchorPane anchorRoot;
    @FXML private AnchorPane parentContainer;

    //Metoders för att byta view
    @FXML private void loadCourseScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        AppFunctions.changeView(root, allKeysButton, parentContainer, anchorRoot);
    }

    @FXML private void loadAdminScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("adminView.fxml"));
        AppFunctions.changeView(root, allKeysButton, parentContainer, anchorRoot);
    }

    @FXML private void loadStudentScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("studentView.fxml"));
        AppFunctions.changeView(root, allKeysButton, parentContainer, anchorRoot);
    }
}
