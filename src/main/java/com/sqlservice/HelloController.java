package com.sqlservice;


import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
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
        tableView.getColumns().clear();
        AppFunctions.setTableColumnNames(tableView, resultSet); //sätter kolumnNamn efter resultSetets kolumnNamn


        //ObservableList<ObservableList> dataList = AppFunktioner.fillList(resultSet);
        FilteredList <ObservableList> filteredData = new FilteredList<>(AppFunctions.fillList(resultSet), b -> true); //Wrappar dataList i en FilteredList.
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

        tableView.setItems(filteredData);
        ContosoConnection.connectionClose(resultSet);


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
            String studentID = AppFunctions.getValueOfCell(studentTableView,0);
            try{
                dataAccessLayer.deleteStudent(studentID);
                searchTableWithTextField(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
            }catch (SQLException e){
                e.printStackTrace();
                System.out.println(e.getErrorCode());
            }

        }catch (Exception e){
            System.out.println(e);
        }
    }




    @FXML Button courseViewButton;
    @FXML private AnchorPane anchorRoot;
    @FXML private AnchorPane parentContainer;

    @FXML private void loadCourseScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        Scene scene = courseViewButton.getScene();

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
    //KLART!



}