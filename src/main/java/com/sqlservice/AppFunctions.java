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
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() { //sätter vilket värde det ska vara i den kolumnen.
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });

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
                if(row.toString().toLowerCase().contains(lowerCaseFilter)){ //Ifall någon entitet i resultsetet överensstämmer med söksträngen returneras den/dessa!
                    return true;
                }
                else return false; //fail safe.

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

    public static boolean checkSelectionModel(TableView tableView, TableView tableView2){
        if(tableView.getSelectionModel().isEmpty() || tableView2.getSelectionModel().isEmpty()){
            return true;
        }

        return false;
    }


}
