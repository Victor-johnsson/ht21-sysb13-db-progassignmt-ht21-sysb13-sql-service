package com.sqlservice;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CourseController {

    @FXML TableView courseTableView;
    @FXML TextField searchCourseTextField;
    @FXML TextField courseNameTextField;
    @FXML TextField courseCreditsTextField;
    @FXML TextArea courseFeedbackArea;
    @FXML Button createCourseButton;
    @FXML Button deleteCourseButton;

    DataAccessLayer dataAccessLayer = new DataAccessLayer();

    ArrayList<Double> arrayList  = new ArrayList<Double>();


    //JavaFX metod. När man startar projektet så är detta det absolut första som körs innan något annat händer.
    public void initialize(){
        try {
            for(double i=0.5; i<=30; i=i+0.5){
                arrayList.add(i);
            }
            ResultSet resultSet = dataAccessLayer.getAllFromTable("Course");
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,resultSet);
            //dataAccessLayer gör att vi kan välja vilken resultSet vi vill visa.
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(courseFeedbackArea,e);
        }
    }

    //En metod som styr knappen för att lägga till en kurs.
    public void onAddCourseButton() {

        try {
            String regex = "[0-9.]*";
            if (courseNameTextField.getText().isBlank()) {
                courseFeedbackArea.setText("Please enter a name for the course");
            } else if (courseCreditsTextField.getText().isBlank()) {
                courseFeedbackArea.setText("Please enter credits for the course");
            } else if (!courseCreditsTextField.getText().matches(regex)) {
                courseFeedbackArea.setText("Please enter credits in digits and only .5 decimal");


            }else if(!(arrayList.contains(Double.valueOf(courseCreditsTextField.getText())))){
                courseFeedbackArea.setText("Please enter credits in digits and only .5 decimal");

            } else if (Double.parseDouble(courseCreditsTextField.getText()) > 30) {
                courseFeedbackArea.setText("Sorry, the maximum credits are 30 per course");
            } else { //Om checkarna godtas kör metoden nedan:
                String courseCode = AppFunctions.getUniqueCode("Course", "courseCode", "C");
                String courseName = courseNameTextField.getText();
                double courseCredits = Double.parseDouble(courseCreditsTextField.getText());
                int i = dataAccessLayer.createCourse(courseCode, courseName, courseCredits);//skickar info från course scenen till DAL
                if (i == 0) {
                    courseFeedbackArea.setText("No course was created!");
                } else if (i == 1) {
                    courseFeedbackArea.setText(courseName + " with course code: " + courseCode + " was created"); //Hämtar vilken kurs som skapats
                }
            }
            ResultSet resultSet = dataAccessLayer.getAllFromTable("Course");
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,resultSet);
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(courseFeedbackArea, e);
        }
    }
        //En metod som styr knappen för att ta bort en kurs.
    public void onDeleteCourseButton (){
        try {
            //"Please select a course"
            //Om användaren inte markerar en/flera rader ska ett felmeddelande skickas ut "You have to mark a row to delete"
            if (courseTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                courseFeedbackArea.setText("Please select a course to remove");
            } else {
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                String courseName = AppFunctions.getValueOfCell(courseTableView, 1);
                int i = dataAccessLayer.deleteCourse(courseCode);
                if (i == 0) {
                    courseFeedbackArea.setText("No course was removed!");
                } else if (i == 1) {
                    courseFeedbackArea.setText("Course " + courseName + " with course code: " + courseCode + " was removed!");
                }
                ResultSet resultSet = dataAccessLayer.getAllFromTable("Course");
                AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,resultSet);
            }
        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(courseFeedbackArea, e);
        }
    }

    @FXML private AnchorPane anchorRoot;
    @FXML private AnchorPane parentContainer;

    //Metod för att byta view.
    @FXML private void loadAdminScene() throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("adminView.fxml"));
        AppFunctions.changeView(root, createCourseButton, parentContainer, anchorRoot);
    }

    @FXML private void loadStudentScene() throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("studentView.fxml"));
        AppFunctions.changeView(root, createCourseButton, parentContainer, anchorRoot);
    }
}
