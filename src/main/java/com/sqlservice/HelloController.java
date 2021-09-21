package com.sqlservice;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

import java.sql.*;
import java.util.Locale;

public class HelloController {
    DataAccessLayer dataAccessLayer = new DataAccessLayer();

    @FXML TableView studentTableView;
    @FXML TextField studentNameTextField;
    @FXML TextField studentSSNTextField;
    @FXML TextField studentAddressTextField;
    @FXML TextField searchStudentTextField;
    @FXML Button addStudentButton;
    @FXML Button deleteStudentButton;
    @FXML TextField studentIDTextField;



    public void initialize(){ //JavaFX metod. När man startar projektet så är detta det absolut första som körs innan
        //något annat händer.
        // ERRORHANTERING!
        try {
            //searchTableWithTextField(tableView,filteredTextField,"Course");
            searchTableWithTextField(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
            //dataAccessLayer gör att vi kan välja vilken resultSet vi vill visa.
        } catch (SQLException e) {
            e.printStackTrace();

        }




    }

    public void searchTableWithTextField(TableView tableView, TextField textField, ResultSet resultSet) throws SQLException { //tar in tableView,
        //textField, String som är namnet på table som vi vill fylla. När vi kallar på denna metoden kan vi säga vilket
        //table vi vill kolla på.

        //ob-list som vi fyller med det som
        //metoden fillTableViewByName returnerar.

        ObservableList<ObservableList> dataList = fillTableViewByResultSet(tableView, resultSet);
        FilteredList <ObservableList> filteredData = new FilteredList<ObservableList>(dataList,b-> true); //Wrappar dataList i listan filteredData.
        //b -> true gör att den kan lyssna när vi skriver i sökfältet.

        textField.textProperty().addListener((observable, oldvalue, newValue) ->{ //lägger till en listener som lyssnar efter när man skriver in något i searchfieldet
            //oldValue ändras aldrig, men det gör newValue.
            filteredData.setPredicate( row -> {
                if(newValue == null || newValue.isEmpty()){ //ifall inget är skrivet i sökfältet visas hela resultsetet!
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase(Locale.ROOT); //gör att vi allt är lowercase
                if(row.toString().toLowerCase().contains(lowerCaseFilter)){ //Ifall någon entitet i resultsetet överensstämmer med söksträngen returneras den/dessa!
                    return true;
                }
                else return false; //fail safe.

            });
        });

        SortedList<Object> sortedData = new SortedList<>(filteredData); //gör listan till en sorterad lista
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData); //kommentar


    }


    public ObservableList<ObservableList> fillTableViewByResultSet(TableView tableView, ResultSet resultSet) throws SQLException{

        ObservableList<ObservableList> dataList = FXCollections.observableArrayList();
        /**
         * Inuti den observerbara listan, har vi en lista av observerbara listor, den tar in tableName (tex Course) och tableView
         * som är vilket tableView vi ska fylla(länkat till FXML-dokumentet). Vi har en metod som anropar denna metoden i denna klassen.
         * skapar en observableList och för att kunna få in listan i ett tableview i JavaFX.
         * Vill man istället ha ett resultSet där man joinar två olika tables?
         * getAllfromTableName behövs även ändras då. **/


        for(int i=0; i<resultSet.getMetaData().getColumnCount(); i++) {
            /**Den lägger till alla kolumner från resultset.
             * courseCode, credits, name = den itererar över de. Så länge i < så många kolumner som finns i resultset, så kör den
             * loopen. Första gången vi kör är i = 0.
             * sätter att j = i **/
         final int j = i;

            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));

                /**Nytt table column. För varje kolumn skapar vi en ny kolumn som har namnet av den kolumnamnet på index i + 1.
                 * första kolumnen är på index 1 i SQL och inte 0 som i en Array. **/

            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() { //sätter vilket värde det ska vara i den kolumnen.
                @Override
                public ObservableValue call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            tableView.getColumns().addAll(col);        //lägger till kolumner i tableView som vi har på rad 81. (kallar på metoden)
            System.out.println("Column ["+i+"] name: " + resultSet.getMetaData().getColumnName(i+1)); //printar ut.
        }
        /**
         * ObservableValue och ObservableTables etc är wrappers vilka låter obervera värdet/tablet och kunna göra ändringar till det med tex listeners!
         * Methoden ovan sätter lägger till columns i ett TableView och döper dessa till deras namn enlig resultsetet!**/


        while (resultSet.next()) { //itererar över resultSet. För första raden skapar vi en observable list (kallar för row).
            ObservableList<String> row = FXCollections.observableArrayList(); //listan som är raderna.
            //för varje kolumn i den raden, lägger vi till värdet i den observablelist.
            //Den lägger till alla värden från resultSet till observableList.
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                row.add(resultSet.getString(i));
            }
            System.out.println("Row [1] added" + row);
            dataList.add(row); //lägger till row(är en observableList) i dataList.
        }

        ContosoConnection.connectionClose(resultSet);


        return dataList;
    }


    public void onAddStudentButton(ActionEvent event){
        try{
            String studentID = studentIDTextField.getText();//kanske skapa en inbyggd räknare
            String studentName =  studentNameTextField.getText();
            String studentSSN = studentSSNTextField.getText();//kanske skapa en check som endast tillåter siffror
            String studentAddress = studentAddressTextField.getText();

            int i = dataAccessLayer.createStudent(studentID,studentSSN,studentName,studentAddress);
            if(i==0){
                System.out.println("no rows affected");
            }else if(i==1){
                System.out.println("one row affected");
            }
            searchTableWithTextField(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
            int errorCode = e.getErrorCode();
            if(errorCode == 2627){
                System.out.println("StudentID already exists! ");//Måste brytas ut! 2627 är för alla typer av constraint violations
            }
        }

    }

    public void setDeleteStudentButton(ActionEvent event){
        try{
            //String studentID = studentTableView.getSelectionModel().getSelectedItems().get(0).toString();
            String student = studentTableView.getSelectionModel().getSelectedCells().toString();
            System.out.println(student);
            //System.out.println(student);

            /*
            TablePosition pos = studentTableView.getSelectionModel().getSelectedCells().get(0);
            int row = pos.getRow();
            Object item = studentTableView.getItems().get(row);
            TableColumn col = pos.getTableColumn();
            String data = (String) col.getCellObservableValue(item).getValue();

            /*

            int i =dataAccessLayer.deleteStudent(studentID);
            if(i==0){
                System.out.println("no rows affected");
            }else if(i==1){
                System.out.println("one row affected");
            }
             */
            searchTableWithTextField(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));



        }catch (Exception e){
            System.out.println(e);
        }
    }
    //TablePosition pos = studentTableView.getSelectionModel().getSelectedCells().get(0);
    //int row = pos.getRow();
    //Item item = studentTableView.getItems().get(row);
    //TableColumn col = pos.getTableColumn();
    //String data = (String) col.getCellObservableValue(item).getValue();



}