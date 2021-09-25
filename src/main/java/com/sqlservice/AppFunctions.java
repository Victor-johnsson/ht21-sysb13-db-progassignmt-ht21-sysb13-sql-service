package com.sqlservice;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class AppFunctions {
    //samlar alla metoder som är universella som kan kalla på dem från alla andra klasser, generella metoder.


    public static ObservableList<ObservableList> fillList(ResultSet resultSet) throws SQLException {
        /**
         * ******************************
         * Data added to ObservableList *
         ********************************/
        ObservableList<ObservableList> dataSet = FXCollections.observableArrayList();

        while (resultSet.next()) { //itererar över resultSet. För första raden skapar vi en observable list (kallar för row).
            ObservableList<String> row = FXCollections.observableArrayList();   //listan som är raderna.
                                                                                //för varje kolumn i den raden, lägger vi till värdet i den observablelist.
                                                                                //Den lägger till alla värden från resultSet till observableList.
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                //Iterate Column
                row.add(resultSet.getString(i));
            }
            dataSet.add(row); //lägger till row(är en observableList) i dataList.
        }
        return dataSet;
    }


    public static void setTableColumnNames(TableView tableView, ResultSet resultSet) throws SQLException{
        /**
         * ********************************
         * TABLE COLUMN NAMES ADDED DYNAMICALLY *
         *********************************
         */
        for(int i=0; i<resultSet.getMetaData().getColumnCount(); i++) {
            //We are using non property style for making dynamic table
            final int j = i;
            TableColumn col = new TableColumn(resultSet.getMetaData().getColumnName(i + 1));
            /**Nytt table column. För varje kolumn skapar vi en ny kolumn som har namnet av den kolumnamnet på index i + 1.
             * första kolumnen är på index 1 i SQL och inte 0 som i en Array. **/

            //sätter vilket värde det ska vara i den kolumnen.
            col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
            tableView.getColumns().addAll(col); //lägger till kolumner i tableView som vi har på rad 81. (kallar på metoden)
        }
    }

    public static void updateSearchableTableView(TableView tableView, TextField textField, ResultSet resultSet) throws SQLException { //tar in tableView,
        //textField, String som är namnet på table som vi vill fylla. När vi kallar på denna metoden kan vi säga vilket
        //table vi vill kolla på.

        //ob-list som vi fyller med det som
        //metoden fillTableViewByName returnerar.
        tableView.getColumns().clear();
        AppFunctions.setTableColumnNames(tableView, resultSet); //sätter kolumnNamn efter resultSetets kolumnNamn


        ObservableList<ObservableList> dataList = AppFunctions.fillList(resultSet);
        ContosoConnection.connectionClose(resultSet);
        FilteredList<ObservableList> filteredData = new FilteredList<>(dataList, b -> true);//Wrappar dataList i en FilteredList.
        //b -> true gör att den kan lyssna när vi skriver i sökfältet.

        textField.textProperty().addListener((observable, oldvalue, newValue) ->{ //lägger till en listener som lyssnar efter när man skriver in något i searchfieldet
            //oldValue ändras aldrig, men det gör newValue.
            filteredData.setPredicate( row -> {
                if(newValue == null || newValue.isEmpty()){ //ifall inget är skrivet i sökfältet visas hela resultsetet!
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase(Locale.ROOT); //gör att vi allt är lowercase
                //Ifall någon entitet i resultsetet överensstämmer med söksträngen returneras den/dessa!
                //fail safe.
                return row.toString().toLowerCase().contains(lowerCaseFilter);

            });
        });
        //ÄNTLIGEN LÄGGER VI IN DATAN I TABLEVIEW
        tableView.setItems(filteredData);

    }

    public static String getValueOfCell(TableView tableView, int columnIndex){

            ObservableList<ObservableList> row = tableView.getSelectionModel().getSelectedItems();//Hämtar raden vi vill få columnen från!
            ObservableList<ObservableList> objectList = row.get(0); //Alltid vara 0. För det är alltid rad 0 den hämtar.
            Object object = objectList.get(columnIndex); //hämtar objekt(ID, name) på index i listan.
            String cellValue = object.toString(); //gör objektet till en sträng för att returnera denna!
        return cellValue;
    }



    //KOLLA PÅ FUNKTONEN IFALL KODEN EJ ÄR UNIK!!
    public static String getUniqueCode(String dbTableName, String idColumnName, String startingLetter) throws SQLException{
        DataAccessLayer dataAccessLayer = new DataAccessLayer();
        ResultSet resultSet = dataAccessLayer.getAllFromTable(dbTableName);
        //Antingen från Student eller Course
        ArrayList<String> arrayList = new ArrayList<>();
        while (resultSet.next()){
            arrayList.add(resultSet.getString(idColumnName));
        }
        ContosoConnection.connectionClose(resultSet);
        while (true) {
            int randomNum = ThreadLocalRandom.current().nextInt(00000, 99999);
            String randomCode = startingLetter + randomNum;
            if (!(arrayList.contains(randomCode))) {

                return randomCode;
            }
        }
    }

    public static void changeView(Parent root, Button viewButton, AnchorPane parentContainer, AnchorPane anchorRoot) {
        Scene scene = viewButton.getScene();

        root.translateYProperty().set(scene.getHeight());

        parentContainer.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.DISCRETE);
        KeyFrame kf = new KeyFrame(Duration.seconds(0.02),kv);
        timeline.getKeyFrames().add(kf);

        timeline.setOnFinished(event1 -> {
            parentContainer.getChildren().remove(anchorRoot);
        });
        timeline.play();
    }

    //Errorhantering för skumma SQL-fel
    public static void unexpectedError(TextArea textArea, SQLException exception){
        /*if(e.getErrorCode() == 0) {
            textArea.setText("Could not connect to database server. \nPlease contact support!");
        } else if (e.getErrorCode() == 2628) {
            textArea.setText("Field is limited to 200 characters");
            System.out.println(e.getMessage());
        } else {
            e.printStackTrace();
            textArea.setText("Ooops, something went wrong. \nPlease contact system administrator");
        }

         */
        String exceptionMessage = exception.getMessage();
        int errorCode = exception.getErrorCode();
        if(exceptionMessage.contains("pk_student")){
            textArea.setText("StudentID is not unique, try again with a unique StudentID");
        }else if(exceptionMessage.contains("uc_studentSSN")){
            textArea.setText("Student with this social security number already exist");
        }else if(exceptionMessage.contains("pk_course")){
            textArea.setText("Course with this course code already exist, try again with unique course code");
        }else if(exceptionMessage.contains("pk_hasStudied")) {
            textArea.setText("This student already has a grade on this course \nand we do not allow two grades for the same student");
        }else if(exceptionMessage.contains("pk_studies")){
            textArea.setText("This student is already studying this course");
        }else if(errorCode == 2628 && exceptionMessage.contains("studentName")) {
            textArea.setText("A students name is limited to 200 characters");
        }else if((errorCode == 2628) && exceptionMessage.contains("studentSSN")){
            textArea.setText("A students social security number is limited to 12 characters");
        }else if(errorCode == 2628 && exceptionMessage.contains("studentCity")){
            textArea.setText("A students city is limited to 200 characters");
        }else if(errorCode == 2628 && exceptionMessage.contains("courseName")){
            textArea.setText("Course name is limited to 200 characters");
        }else if(errorCode == 2628 && exceptionMessage.contains("grade")){
            textArea.setText("Grade is limited to be one character between A-F \n" +
                    "Make sure a grade has been chosen!");
        }else if(errorCode == 0) {
            textArea.setText("Could not connect to database server. \nPlease contact support!");
        }else{
            exception.printStackTrace();
            textArea.setText("Ooops, something went wrong. \nPlease contact system administrator");
        }

    }

}
