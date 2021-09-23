package com.sqlservice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class AdminViewController {




    @FXML
    TableView courseTableView;
    @FXML
    TableView studentTableView;
    @FXML
    TextField searchCourseTextField;
    @FXML
    TextField searchStudentTextField;
    @FXML
    Button addStudentOnCourseButton;
    @FXML
    Button removeStudentFromCourseButton;
    @FXML
    Button setGradeButton;
    @FXML
    TextField gradeTextField;
    @FXML
    Button resetButton;
    @FXML
    Button showCourseStatistics;
    @FXML
    Button showStudentsOnCourse;
    @FXML
    Button showCoursesStudied;
    @FXML
    Button showCompletedCourses;

    @FXML
    ChoiceBox<String> gradesComboBox;


    DataAccessLayer dataAccessLayer = new DataAccessLayer();



    public void initialize(){
        try{
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,dataAccessLayer.getAllFromTable("Course"));
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
            //onClickingCourse();
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    public void addStudentOnCourse(ActionEvent event) {
        try {
            //kolla att course och student view har valts
            if(!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())){
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseID = AppFunctions.getValueOfCell(courseTableView, 0);

                int i = dataAccessLayer.addToStudies(studentID, courseID);
                if (i == 0) {
                    System.out.println("something happened");

                } else if (i == 1) {
                    System.out.println("The student " + studentID + " has been added to the course: " + courseID);
                }

            }else {
                System.out.println("Select a course and a student!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            //ERROR HANTERING BLABLABLA
        }
    }

    public void removeStudentFromCourse(ActionEvent event) {
        try {

            String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
            String courseID = AppFunctions.getValueOfCell(courseTableView, 0);
            int i = dataAccessLayer.removeFromStudies(studentID, courseID);
            if (i == 0) {
                System.out.println("No student was removed from course : " + courseID);
            } else if (i == 1) {
                System.out.println("The student " + studentID + "has been removed from the course : " + courseID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    public void setGrade(ActionEvent event){
        try {
            if(!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())){
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                String grade = gradeTextField.getText().toUpperCase();
                if(dataAccessLayer.isStudentOnCourse(studentID,courseCode)){
                    dataAccessLayer.addToHasStudied(studentID,courseCode,grade);
                    dataAccessLayer.removeFromStudies(studentID,courseCode);
                    System.out.println("Grade " + grade + " was added for " + studentID + " on course "+ courseCode );
                }else {
                    System.out.println("Student doesn't study this course, can't add a grade");
                }
            }else{
                System.out.println("Select a course and a student!");
            }

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }



    public void onShowStudentsOnCourse(ActionEvent event){
        try {
            String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
            ResultSet resultSet = dataAccessLayer.getStudentsOnCourse(courseCode);

                AppFunctions.updateSearchableTableView(studentTableView, searchStudentTextField, resultSet);
            }
            else {
                System.out.println("please select a course");
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    public void onCourseStatistics(ActionEvent event){
        try{
            if (courseTableView.getSelectionModel().isEmpty()) {
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                ResultSet resultSet = dataAccessLayer.getStudentsWhoHaveStudied(courseCode);

                AppFunctions.updateSearchableTableView(studentTableView, searchStudentTextField, resultSet);
            }
            else {
                System.out.println("please select a course");
            }
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }



    @FXML
    AnchorPane parentContainer;
    @FXML AnchorPane anchorRoot;


    @FXML private void loadCourseScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        AppFunctions.changeView(root, addStudentOnCourseButton, parentContainer, anchorRoot);
    }

    @FXML private void loadStudentScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("studentView.fxml"));
        AppFunctions.changeView(root, addStudentOnCourseButton, parentContainer, anchorRoot);
    }
/*
    @FXML TextField searchCourseViewTextField;
    public void onClickingCourse(){
        courseTableView.setOnMouseClicked(e ->{
            String courseCode = AppFunctions.getValueOfCell(courseTableView,0);
            try{
                AppFunctions.updateSearchableTableView(showOnCourseView,searchCourseViewTextField,dataAccessLayer.getStudentsOnCourse(courseCode));
                while (dataAccessLayer.getStudentsOnCourse(courseCode).next()){
                    System.out.println(dataAccessLayer.getStudentsOnCourse(courseCode).getString(0));
                }

            } catch (NullPointerException e1) {
                //gör inget om man klickar i rutan fast inte på en person!
            }catch (SQLException e2){
                e2.printStackTrace();
            }
        });
    }



 */








}
