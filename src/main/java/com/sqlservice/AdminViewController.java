package com.sqlservice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

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
            //int i = dataAccessLayer.addToStudies(studentID, courseID);
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





}
