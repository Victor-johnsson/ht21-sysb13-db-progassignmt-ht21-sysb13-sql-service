package com.sqlservice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.sql.SQLException;

public class CourseController {

    @FXML TableView courseTableView;
    @FXML TextField searchCourseTextField;
    @FXML TextField courseNameTextField;
    @FXML TextField courseCreditsTextField;
    @FXML TextArea courseFeedbackArea;
    @FXML Button createCourseButton;
    @FXML Button deleteCourseButton;

    DataAccessLayer dataAccessLayer = new DataAccessLayer();

    public void initialize(){ //JavaFX metod. När man startar projektet så är detta det absolut första som körs innan
        //något annat händer.
        // ERRORHANTERING!
        try {
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,dataAccessLayer.getAllFromTable("Course"));
            //dataAccessLayer gör att vi kan välja vilken resultSet vi vill visa.
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onAddCourseButton(ActionEvent event){
        try{
            String regex = "[0-9]+\\.?[0-9]+";
            if (courseNameTextField.getText().isBlank()){
                courseFeedbackArea.setText("Please enter a name for the course");
            }else if (courseCreditsTextField.getText().isBlank()){
                courseFeedbackArea.setText("Please enter credits for the course");
            }else if (!courseCreditsTextField.getText().matches(regex)) {
                courseFeedbackArea.setText("Please enter credits in digits only");
            }else if (Double.parseDouble(courseCreditsTextField.getText())> 30){
                courseFeedbackArea.setText("Sorry, the maximum credits are 30 per course");
            }else { //Om checkarna godtas kör metoden nedan:
                String courseID = AppFunctions.randomCode("Course","courseCode","C");
                String courseName =  courseNameTextField.getText();
                double courseCredits = Double.parseDouble(courseCreditsTextField.getText());
                int i = dataAccessLayer.createCourse(courseID, courseName, courseCredits);//skickar info från course scenen till DAL
                if (i == 0) {
                    courseFeedbackArea.setText("No course was created");
                } else if (i == 1) {
                    courseFeedbackArea.setText("One course was created");
                }
            }
            AppFunctions.updateSearchableTableView(courseTableView,searchCourseTextField,dataAccessLayer.getAllFromTable("Course"));
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
            int errorCode = e.getErrorCode();
            if(errorCode == 2627){
                courseFeedbackArea.setText("CourseCode already exists!");//Måste brytas ut! 2627 är för alla typer av constraint violations
                //Felmeddelandet kan inte lyda som det gör... För det handlar om man får samma siffror i rnd och inte att det redan finns samma kurskod(för användaren)
            }
        }
    }
    public void onDeleteCourseButton(ActionEvent event){
        try{
            //"Please select a course"
            //Om användaren inte markerar en/flera rader ska ett felmeddelande skickas ut "You have to mark a row to delete"
            if (courseTableView.getSelectionModel().getSelectedItems().isEmpty()){
                courseFeedbackArea.setText("Please select a course to delete");
            }else {
                String courseCode = AppFunctions.getValueOfCell(courseTableView, 0);
                int i = dataAccessLayer.deleteCourse(courseCode);
                if (i == 0) {
                    courseFeedbackArea.setText("No course was removed");
                } else if (i == 1) {
                    courseFeedbackArea.setText("Course removed!");
                }
                AppFunctions.updateSearchableTableView(courseTableView, searchCourseTextField, dataAccessLayer.getAllFromTable("Course"));
            }
        }catch (SQLException e){
            System.out.println(e.getErrorCode());
            e.printStackTrace();
        }
    }
}
