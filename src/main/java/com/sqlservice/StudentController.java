package com.sqlservice;


import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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

public class StudentController {
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
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
            //dataAccessLayer gör att vi kan välja vilken resultSet vi vill visa.
        } catch (SQLException e) {
            e.printStackTrace();

        }
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

            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
            int errorCode = e.getErrorCode();
            if(errorCode == 2627){
                System.out.println("StudentID already exists! ");//Måste brytas ut! 2627 är för alla typer av constraint violations
            }
        }

    }

    public void onDeleteStudentButton(ActionEvent event){
        try{

            String studentID = AppFunctions.getValueOfCell(studentTableView,0);
            int i  = dataAccessLayer.deleteStudent(studentID);
            if(i==0){
                System.out.println("No student was removed");
            }else if(i==1){
                System.out.println("Student removed");
            }

            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));

        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
        }
    }




    @FXML Button courseViewButton;
    @FXML private AnchorPane anchorRoot;
    @FXML private AnchorPane parentContainer;

    //metod att byta view
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