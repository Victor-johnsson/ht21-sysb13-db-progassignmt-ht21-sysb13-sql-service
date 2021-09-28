package com.sqlservice;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.io.File;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;

import java.sql.SQLException;

public class MetaController {
    @FXML
    TableView metaTableView;
    @FXML TextField searchbar;
    @FXML TextArea feedbackTextArea;
    DALAdventureWorks dalAdventureWorks = new DALAdventureWorks();

    private HostServices hostServices ;

    public HostServices getHostServices() {
        return hostServices ;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices ;
    }



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

    public void openExcelFile(ActionEvent actionEvent){
        File excelFile = new File("src/main/resources/reports/Excel Assignment.xlsx");
        try {
            getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void openCustomerReport(ActionEvent actionEvent){
        File excelFile = new File("C:\\Users\\Victo\\Desktop\\AdventureWorks Reports\\CustomerReport.accdb");
        try {
            getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }
    public void openProductReport(ActionEvent actionEvent){
        File excelFile = new File("C:\\Users\\Victo\\Desktop\\AdventureWorks Reports\\Products.accdb");
        try {
            getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }


}
