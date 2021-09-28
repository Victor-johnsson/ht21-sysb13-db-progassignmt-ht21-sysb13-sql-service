package com.sqlservice;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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



    public void onAllKeysButton(){
        try {
           ResultSet resultSet = dalAdventureWorks.getAllKeys();
           AppFunctions.updateSearchableTableView(metaTableView,searchbar,resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void onCustomerContentButton() {
        try {
            ResultSet resultSet = dalAdventureWorks.getCustomerContent();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);

        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void onTableConstraintsButton() {
        try {
            ResultSet resultSet = dalAdventureWorks.getTableConstraints();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void onAllTablesButton() {
        try {
            ResultSet resultSet = dalAdventureWorks.getAllTables();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);
        }catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void onCustomerColumnsButton() {
        try {
            ResultSet resultSet = dalAdventureWorks.getCustomerColumns();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);

        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void onTableWithMostRowsButton() {
        try {
            ResultSet resultSet = dalAdventureWorks.getTableWithMostRows();
            AppFunctions.updateSearchableTableView(metaTableView, searchbar, resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void openExcelFile(){
        File excelFile = new File("src/main/resources/reports/Excel Assignment.xlsx");
        try {
            getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (IOException e) {
            feedbackTextArea.setText("Something went terribly wrong trying to find the file");
        }catch (NullPointerException exception) {
            feedbackTextArea.setText("Could not find the local services to open this kind of file, we are working on a fix");
        }
    }

    public void openCustomerReport(){
        File excelFile = new File("C:\\Users\\Victo\\Desktop\\AdventureWorks Reports\\CustomerReport.accdb");
        try {
            getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            feedbackTextArea.setText("Something went terribly wrong trying to find the file");
        }catch (NullPointerException exception){
            feedbackTextArea.setText("Could not find the local services to open this kind of file, we are working on a fix");
        }
    }

    public void openProductReport() {
        File excelFile = new File("C:\\Users\\Victo\\Desktop\\AdventureWorks Reports\\Products.accdb");
        try {
            getHostServices().showDocument(excelFile.toURI().toURL().toExternalForm());
        } catch (MalformedURLException e) {
            feedbackTextArea.setText("Something went terribly wrong trying to find the file");
        }catch (NullPointerException exception){
        feedbackTextArea.setText("Could not find the local services to open this kind of file, we are working on a fix");
        }
    }
}
