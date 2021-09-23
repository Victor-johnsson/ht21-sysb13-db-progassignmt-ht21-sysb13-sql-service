package com.sqlservice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminViewController {


    @FXML TableView courseTableView;
    @FXML TableView studentTableView;
    @FXML TextField searchCourseTextField;
    @FXML TextField searchStudentTextField;
    @FXML Button addStudentOnCourseButton;
    @FXML Button removeStudentFromCourseButton;
    @FXML Button setGradeButton;
    @FXML TextField gradeTextField;
    @FXML Button resetButton;
    @FXML Button showCourseStatistics;
    @FXML Button showStudentsOnCourse;
    @FXML Button showCoursesStudied;
    @FXML Button showCompletedCourses;
    @FXML ChoiceBox<String> gradesComboBox;
    @FXML TextArea feedbackTextArea;

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
    //Metod för att lägga till en student på en kurs.
    //OBS: det går inte att lägga till en student om det överskrider 45 credits totalt.
    public void addStudentOnCourse(ActionEvent event) {
        try {
            //kolla att course och student view har valts
            if(!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())){
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                if(dataAccessLayer.potentialCredits(courseCode,studentID)<=45){
                    int i = dataAccessLayer.addToStudies(studentID, courseCode);
                    if (i == 0) {
                        feedbackTextArea.setText("something happened");

                    } else if (i == 1) {
                        feedbackTextArea.setText("The student " + studentID + " has been added to the course: " + courseCode);
                    }
                }else{
                    feedbackTextArea.setText("A student may only take 45 credits per semester" + "\n" +
                            "Max credits exceeded");
                }

            } else {
                feedbackTextArea.setText("Select a course and a student!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
            //ERROR HANTERING BLABLABLA
        }
    }

    //Ta bort en student från en specifik kurs.
    public void removeStudentFromCourse(ActionEvent event) {
        try {
            if(!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())){
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                int i = dataAccessLayer.removeFromStudies(studentID, courseCode);
                if (i == 0) {
                    feedbackTextArea.setText("No student was removed from course : " + courseCode);
                } else if (i == 1) {
                    feedbackTextArea.setText("The student " + studentID + "has been removed from the course : " + courseCode);
                }
            } else {
                feedbackTextArea.setText("Select a course and a student!");
            }


        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    //Tar bort studenten från "Studies" och flyttar till "HasStudied" och sätter ett betyg
    public void setGrade(ActionEvent event) {
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
            if (!(courseTableView.getSelectionModel().isEmpty())) {
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

    //Metod som visar kursstatisik.
    public void onCourseStatistics(ActionEvent event) {
        try {
            if (!(courseTableView.getSelectionModel().isEmpty())) {
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                String courseName = AppFunctions.getValueOfCell(courseTableView, 1);
                ResultSet resultSet = dataAccessLayer.getStudentsWhoHaveStudied(courseCode);
                String percentage = dataAccessLayer.percentageOfGrade(courseCode, "A");
                feedbackTextArea.setText("Percentage of students who got an A on " + courseName + ": " + percentage + "%");

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

    //Metod som visar resultat på kurser en specifik student har studerat.
    public void onShowStudiedCourses(ActionEvent event){
        try {
            if (!(studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                ResultSet resultSet = dataAccessLayer.getActiveCoursesForStudent(studentID);

                AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, resultSet);
            } else {
                feedbackTextArea.setText("Please select a student!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    //Visar resultat på en specifik students genomförda kurser.
    public void onShowCompletedCourses(ActionEvent event){
        try {
            if (!(studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                ResultSet resultSet = dataAccessLayer.getCompletedCoursesForStudent(studentID);

                AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, resultSet);
            } else {
                feedbackTextArea.setText("Please select a student!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    //Återställer till originalvy för Course tableView och Student tableView.
    public void onResetButton(ActionEvent event){
        try {
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,dataAccessLayer.getAllFromTable("Course"));
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,dataAccessLayer.getAllFromTable("Student"));

        }catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }

    @FXML AnchorPane parentContainer;
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
