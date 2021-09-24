package com.sqlservice;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

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


    //JavaFX metod. När man startar projektet så är detta det absolut första som körs innan något annat händer.
    public void initialize(){
        // ERRORHANTERING!
        try {
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
            //dataAccessLayer gör att vi kan välja vilken resultSet vi vill visa.


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //En metod som styr knappen för att lägga till en student.
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
                String studentID = AppFunctions.getUniqueCode("Student", "studentID", "S");
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
                //behöver 2st 2627 error hanteringar, en för UNIQUE och en för PRIMARY KEY
                studentFeedbackArea.setText("Ooops, something went wrong. Please contact system administrator");
            }else if(errorCode == 2628){
                studentFeedbackArea.setText("Name and address fields are limited to 200 characters");
            }
        }
    }
    //En metod som styr knappen för att ta bort en student.
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



    //Metod för att byta view
    @FXML private void loadCourseScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        AppFunctions.changeView(root, addStudentButton, parentContainer, anchorRoot);
    }

    //Metod som??
    @FXML private void loadAdminScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("adminView.fxml"));
        AppFunctions.changeView(root, addStudentButton, parentContainer, anchorRoot);
    }
}