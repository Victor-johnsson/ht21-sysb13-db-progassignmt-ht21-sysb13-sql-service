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
    @FXML //hur grafiska gränssnittet ska se ut. Det är JavaFX.
    private TableView tableView;

    @FXML
    private Label welcomeText;

    @FXML TextField filteredTextField;

    @FXML Button searchButton;
    @FXML Button resetButton;

    public void initialize(){ //JavaFX metod. När man startar projektet så är detta det absolut första som körs innan
        //något annat händer.
        // ERRORHANTERING!
        try {
            searchTableWithTextField(tableView,filteredTextField,"Course");
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }

    public void searchTableWithTextField(TableView tableView, TextField textField, String databaseTableName) throws SQLException { //tar in tableView,
        //textField, String som är namnet på table som vi vill fylla. När vi kallar på denna metoden kan vi säga vilket
        //table vi vill kolla på.

        ObservableList<ObservableList> dataList = fillTableViewByTableName(databaseTableName,tableView); //ob-list som vi fyller med det som
        //metoden fillTableViewByName returnerar.
        FilteredList <ObservableList> filteredData = new FilteredList<ObservableList>(dataList,b-> true); //Wrappar dataList i listan filteredData.
        //b -> true gör att den kan lyssna när vi skriver i sökfältet.

        textField.textProperty().addListener((observable, oldvalue, newValue) ->{ //lägger till en listener som lyssnar efter när man skriver in något i searchfieldet. (textField är sökfältet).
            //oldValue ändras aldrig, men det gör newValue.
            filteredData.setPredicate( object -> { //row är en rad.
                if(newValue == null || newValue.isEmpty()){ //ifall inget är skrivet i sökfältet visas hela resultsetet!
                    return true;

                }

                String lowerCaseFilter = newValue.toLowerCase(Locale.ROOT); //Allt vi skriver in är lowercase.
                if(object.toString().toLowerCase().contains(lowerCaseFilter)){ //Ifall någon entitet i resultsetet överensstämmer med söksträngen returneras den/dessa!
                    //row är en rad. Gör raden till sträng, lowerCaseFilter är newValue fast i lowerCase. Om raden innehåller det vi skriver in i sökfältet, returnerar
                    //vi den raden.
                    return true;
                }
                else return false; //fail safe.

            });
        });

        SortedList<Object> sortedData = new SortedList<>(filteredData); //gör listan till en sorterad lista
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData); //kommentar


    }




    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");



    }
    public ObservableList<ObservableList> fillTableViewByTableName(String tableName, TableView tableView) throws SQLException{
        //Inuti den observerbara listan, har vi en lista av observerbara listor, den tar in tableName (tex Course) och tableView
        //som är vilket tableView vi ska fylla(länkat till FXML-dokumentet). Vi har en metod som anropar denna metoden i denna klassen.

        ObservableList<ObservableList> dataList = FXCollections.observableArrayList(); //skapar en observableList och för att kunna få in listan i ett tableview i JavaFX.
        ResultSet rs = dataAccessLayer.getAllFromTable(tableName); //hämtar resultsetet från ett visst table --> borde kanske ändra här så att den istället hämtar ett resultset beroende på sqlStatment vi vill ha.
        //Vill man istället ha ett resultSet där man joinar två olika tables?
        //getAllfromTableName behövs även ändras då.

        for(int i=0; i<rs.getMetaData().getColumnCount(); i++) { //Den lägger till alla kolumner från resultset.
            //courseCode, credits, name = den itererar över de. Så länge i < så många kolumner som finns i resultset, så kör den
            //loopen. Första gången vi kör är i = 0.
            final int j = i; //sätter att j = i

            TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1)); //Nytt table column. För varje kolumn skapar vi en ny kolumn som har namnet av den kolumnamnet på index i + 1.
            //första kolumnen är på index 1 och inte 0.
            col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() { //sätter vilket värde det ska vara i den kolumnen.
                @Override
                public ObservableValue call(CellDataFeatures<ObservableList, String> param) {
                    return new SimpleStringProperty(param.getValue().get(j).toString());
                }
            });
            tableView.getColumns().addAll(col); //lägger till kolumner i tableView som vi har på rad 81. (kallar på metoden)
            System.out.println("Column ["+i+"] name: " + rs.getMetaData().getColumnName(i+1)); //printar ut.
        }
        //ObservableValue och ObservableTables etc är wrappers vilka låter obervera värdet/tablet och kunna göra ändringar till det med tex listeners!
        //Methoden ovan sätter lägger till columns i ett TableView och döper dessa till deras namn enlig resultsetet!


        while (rs.next()) { //itererar över resultSet. För första raden skapar vi en observable list (kallar för row).
            ObservableList<String> row = FXCollections.observableArrayList(); //listan som är raderna.
            //för varje kolumn i den raden, lägger vi till värdet i den observablelist.
            //Den lägger till alla värden från resultSet till observableList.
            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                row.add(rs.getString(i));
            }
            System.out.println("Row [1] added" + row);
            dataList.add(row); //lägger till row(är en observableList) i dataList.
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