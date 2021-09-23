package com.sqlservice;


import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
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
    @FXML TextArea studentFeedbackArea;
    @FXML MenuItem loadCourseView;
    @FXML MenuItem loadAdminView;



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
            String regex = "[0-9]+";
            if (studentNameTextField.getText().isBlank()){
                studentFeedbackArea.setText("Please enter a name");
            }else if (studentSSNTextField.getText().isBlank()){
                studentFeedbackArea.setText("Please enter a SSN");
            }else if (studentSSNTextField.getText().length() != 12 || !studentSSNTextField.getText().matches(regex)){
                studentFeedbackArea.setText("SSN must be exactly 12 digits!");
            }else if (studentAddressTextField.getText().isBlank()){
                studentFeedbackArea.setText("Please enter an address");
            }else {
                String studentID = AppFunctions.randomCode("Student", "studentID", "S");
                String studentName = studentNameTextField.getText();
                String studentSSN = studentSSNTextField.getText();//kanske skapa en check som endast tillåter siffror
                String studentAddress = studentAddressTextField.getText();

                int i = dataAccessLayer.createStudent(studentID, studentSSN, studentName, studentAddress);
                if (i == 0) {
                    studentFeedbackArea.setText("No student was created");
                } else if (i == 1) {
                    studentFeedbackArea.setText("Student " + studentName + " with " + studentID + " was created");
                }
            }
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
            int errorCode = e.getErrorCode();
            if(errorCode == 2627){
                //behöver 2 2627 error hanteringar, en för UNIQUE och en för PRIMARY KEY
                studentFeedbackArea.setText("Student with this SSN already exists! Enter a different SSN" );
            }
        }
    }

    public void onDeleteStudentButton(ActionEvent event){
        try{
            if (studentTableView.getSelectionModel().isEmpty()){
                studentFeedbackArea.setText("Please select a student to remove");
            }else {
                String studentName = AppFunctions.getValueOfCell(studentTableView, 2);
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                int i = dataAccessLayer.deleteStudent(studentID);
                if (i == 0) {
                    studentFeedbackArea.setText("No student was removed!");
                } else if (i == 1) {
                    studentFeedbackArea.setText("Student " + studentName + "with student ID: " + studentID + " was removed!");
                }
            }
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));

        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
        }
    }


    @FXML private AnchorPane anchorRoot;
    @FXML private AnchorPane parentContainer;



    //metod att byta view
    @FXML private void loadCourseScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        AppFunctions.changeView(root, addStudentButton, parentContainer, anchorRoot);
    }

    @FXML private void loadAdminScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("adminView.fxml"));
        AppFunctions.changeView(root, addStudentButton, parentContainer, anchorRoot);
    }
}