package com.sqlservice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML Button resetButton;
    @FXML ComboBox<String> gradesComboBox;
    @FXML TextArea feedbackTextArea;

    final DataAccessLayer dataAccessLayer = new DataAccessLayer();
    final ObservableList<String> gradeOptions = FXCollections.observableArrayList("A","B","C","D","E","F");

    public void initialize() {
        try {
            ResultSet resultSetCourse = dataAccessLayer.getAllFromTable("Course");
            ResultSet resultSetStudent = dataAccessLayer.getAllFromTable("Student");
            AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, resultSetCourse);
            AppFunctions.updateSearchableTableView(studentTableView, searchStudentTextField, resultSetStudent);
            gradesComboBox.getItems().addAll(gradeOptions);
            studentTableView.setPlaceholder(new Label("Could not find any students"));
            courseTableView.setPlaceholder(new Label("Could not find any courses"));
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getErrorCode());
        }
    }
    //Metod för att lägga till en student på en kurs.
    //OBS: det går inte att lägga till en student om det överskrider 45 credits totalt.
    public void addStudentOnCourse() {
        try {
            //kolla att course och student view har valts
            if (!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);

                double potentialCredits =  dataAccessLayer.potentialCredits(courseCode,studentID);
                if(potentialCredits<=45){
                    int i = dataAccessLayer.addToStudies(studentID, courseCode);
                    if (i == 0) {
                        feedbackTextArea.setText("Something strange happened, try again");

                    } else if (i == 1) {
                        feedbackTextArea.setText("The student " + studentID + " has been added to the course: " + courseCode);
                    }
                }else{
                    feedbackTextArea.setText("A student may only take 45 credits per semester" + "\n" +
                            "Max credits exceeded");
                }

            } else {
                feedbackTextArea.setText("Please select a course and a student!");
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    //Ta bort en student från en specifik kurs.
    public void removeStudentFromCourse() {
        try {
            if (!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                int i = dataAccessLayer.removeFromStudies(studentID, courseCode);
                if (i == 0) {
                    feedbackTextArea.setText("No student was removed from course : " + courseCode);
                } else if (i == 1) {
                    feedbackTextArea.setText("The student " + studentID + " has been removed from the course : " + courseCode);
                }
            } else {
                feedbackTextArea.setText("Please select a course and a student!");
            }

        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);

        }
    }

    //Tar bort studenten från "Studies" och flyttar till "HasStudied" och sätter ett betyg
    public void setGrade() {
        try {
            if (!(courseTableView.getSelectionModel().isEmpty() || studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                String grade = gradesComboBox.getValue();
                if(grade==null){ //Om man inte valt ett A-F, utan står bara grade
                    feedbackTextArea.setText("Choose a grade");
                }else{
                    if (dataAccessLayer.isStudentOnCourse(studentID, courseCode)) { //Kollar om studenten finns på kursen
                        if (!(dataAccessLayer.hasPreviousGrade(studentID,courseCode))){ //Om personen inte har ett tidigare grade,
                            dataAccessLayer.addToHasStudied(studentID, courseCode, grade); //Lägger till i HasStudied

                        }else { //ifall personen har tidigare betyg uppdaterar vi betyget (om man fått F och läst om)
                            dataAccessLayer.updateGrade(studentID, courseCode, grade);
                        }
                        dataAccessLayer.removeFromStudies(studentID, courseCode); //Tar bort från Studies
                        feedbackTextArea.setText("Grade " + grade + " was added for " + studentID + " on course " + courseCode);
                    } else {
                        feedbackTextArea.setText("Student does not study this course, can not add a grade"); //isStudentOnCourse
                    }
                }

            } else {
                feedbackTextArea.setText("Please select a course and a student!");
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    //Visar studenter aktiva på en kurs
    public void onShowStudentsOnCourse() {
        try {
            if (!(courseTableView.getSelectionModel().isEmpty())) {
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                ResultSet resultSet = dataAccessLayer.getStudentsOnCourse(courseCode);

                AppFunctions.updateSearchableTableView(studentTableView, searchStudentTextField, resultSet);
                System.out.println(courseTableView.getItems().isEmpty());
            } else {
                feedbackTextArea.setText("Please select a course!");
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    //Metod som visar kursstatisik.
    public void onCourseStatistics() {
        try {
            if (!(courseTableView.getSelectionModel().isEmpty())) {
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                String courseName = AppFunctions.getValueOfCell(courseTableView, 1);
                ResultSet resultSet = dataAccessLayer.getStudentsWhoHaveStudied(courseCode);
                String percentage = dataAccessLayer.percentageOfGrade(courseCode, "A");
                feedbackTextArea.setText("Percentage of students who got an A on " + courseName + ": " + percentage + "%");

                AppFunctions.updateSearchableTableView(studentTableView, searchStudentTextField, resultSet);
            } else {
                feedbackTextArea.setText("Please select a course!");
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    //Metod som visar resultat på kurser en specifik student har studerat.
    public void onShowStudiedCourses(){
        try {
            if (!(studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                ResultSet resultSet = dataAccessLayer.getActiveCoursesForStudent(studentID);

                AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, resultSet);
            } else {
                feedbackTextArea.setText("Please select a student!");
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    //Visar resultat på en specifik students genomförda kurser.
    public void onShowCompletedCourses(){
        try {
            if (!(studentTableView.getSelectionModel().isEmpty())) {
                String studentID = AppFunctions.getValueOfCell(studentTableView, 0);
                ResultSet resultSet = dataAccessLayer.getCompletedCoursesForStudent(studentID);

                AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, resultSet);
            } else {
                feedbackTextArea.setText("Please select a student!");
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    public void onThroughputButton(){
        try{
            ResultSet resultSet = dataAccessLayer.getTopThroughput();
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    //Återställer till originalvy för Course tableView och Student tableView.
    public void onResetButton(){
        try {
            ResultSet resultSetCourse = dataAccessLayer.getAllFromTable("Course");
            ResultSet resultSetStudent = dataAccessLayer.getAllFromTable("Student");
            AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, resultSetCourse);
            AppFunctions.updateSearchableTableView(studentTableView, searchStudentTextField, resultSetStudent);
            feedbackTextArea.setText("");

        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(feedbackTextArea,e);
        }
    }

    @FXML AnchorPane parentContainer;
    @FXML AnchorPane anchorRoot;

    @FXML private void loadCourseScene() throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        AppFunctions.changeView(root, addStudentOnCourseButton, parentContainer, anchorRoot);
    }

    @FXML private void loadStudentScene() throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("studentView.fxml"));
        AppFunctions.changeView(root, addStudentOnCourseButton, parentContainer, anchorRoot);
    }

}
