package com.sqlservice;



import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import java.sql.*;
import java.util.Locale;
import java.util.function.Predicate;

public class HelloController {
    DataAccessLayer dataAccessLayer = new DataAccessLayer();
    @FXML
    private TableView tableView;

    @FXML
    private Label welcomeText;

    @FXML TextField filteredTextField;

    @FXML Button searchButton;
    @FXML Button resetButton;

    public void initialize(){
        try {
            searchTableWithTextField(tableView,filteredTextField,"Course");
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

    public void searchTableWithTextField(TableView tableView, TextField textField, String databaseTableName) throws SQLException {

        ObservableList<ObservableList> dataList = fillTableViewByTableName(databaseTableName,tableView);
        FilteredList <ObservableList> filteredData = new FilteredList<ObservableList>(dataList,b-> true);

        textField.textProperty().addListener((observable, oldvalue, newValue) ->{ //lägger till en listener som lyssnar efter när man skriver in något i searchfieldet
            filteredData.setPredicate( object -> {
                if(newValue == null || newValue.isEmpty()){ //ifall inget är skrivet i sökfältet visas hela resultsetet!
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase(Locale.ROOT); //gör att vi allt är lowercase
                if(object.toString().toLowerCase().contains(lowerCaseFilter)){ //Ifall någon entitet i resultsetet överensstämmer med söksträngen returneras den/dessa!
                    return true;
                }
                else return false;

            });
        });

        SortedList<Object> sortedData = new SortedList<>(filteredData); //gör listan till en sorterad lista
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

    }




    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");



    }
    public ObservableList<ObservableList> fillTableViewByTableName(String tableName, TableView tableView) throws SQLException{

        ObservableList<ObservableList> dataList = FXCollections.observableArrayList(); //skapar en observableList
        ResultSet rs = dataAccessLayer.getAllFromTable(tableName); //hämtar resultsetet från ett visst table --> borde kanske ändra här så att istället för ett specifikt table, så hämtar den ett resultset beroende på sqlStatment vi vill ha

        for(int i=0; i<rs.getMetaData().getColumnCount(); i++) {
            final int j = i;

            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            tableView.getColumns().addAll(col);
            System.out.println("Column ["+i+"] name: " + rs.getMetaData().getColumnName(i+1));
        }
        //ObservableValue och ObservableTables etc är wrappers vilka låter obervera värdet/tablet och kunna göra ändringar till det med tex listeners!
        //Methoden ovan sätter lägger till columns i ett TableView och döper dessa till deras namn enlig resultsetet!


        while (rs.next()) {
            ObservableList<String> row = FXCollections.observableArrayList();
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                row.add(rs.getString(i));
            }
            System.out.println("Row [1] added" + row);
            dataList.add(row);
        }
        //tableView.setItems(dataList);
            /*
            Connection connection = rs.getStatement().getConnection();
            Statement statement = rs.getStatement();
            rs.close();
            statement.close();
            connection.close();

             */
        ContosoConnection.ConnectionClose(rs);
        return dataList;







    }




}