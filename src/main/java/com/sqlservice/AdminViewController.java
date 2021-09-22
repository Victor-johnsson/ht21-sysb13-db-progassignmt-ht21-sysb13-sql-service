package com.sqlservice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLOutput;

public class AdminViewController {

    @FXML
    TableView showOnCourseView;
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

    DataAccessLayer dataAccessLayer = new DataAccessLayer();

    public void initialize(){
        try{
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,dataAccessLayer.getAllFromTable("Course"));
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    public void addStudentOnCourse(ActionEvent event) {
        try {

            String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
            String courseID = AppFunctions.getValueOfCell(courseTableView, 0);
            int i = dataAccessLayer.addToStudies(studentID, courseID);
            if (i == 0) {
                System.out.println("No student was added to the course: " + courseID);
            } else if (i == 1) {
                System.out.println("The student " + studentID + " has been added to the course: " + courseID);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
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
            String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
            String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
            //System.out.println(courseCode);
            //System.out.println(studentID);
            String grade = gradeTextField.getText().toUpperCase();
            if(dataAccessLayer.isStudentOnCourse(studentID,courseCode) == true){
                dataAccessLayer.addToHasStudied(studentID,courseCode,grade);
                dataAccessLayer.removeFromStudies(studentID,courseCode);
                System.out.println("Grade " + grade + " was added for" + studentID + " on course: "+ courseCode );
            }else {
                System.out.println("Student doesn't study this course, can't add a grade");
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

            AppFunctions.updateSearchableTableView(showOnCourseView,searchStudentTextField,resultSet);

        }catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }





    }







}
