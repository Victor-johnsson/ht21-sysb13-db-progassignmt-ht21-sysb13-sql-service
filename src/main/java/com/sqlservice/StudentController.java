package com.sqlservice;


import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
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
    private HostServices hostServices ;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices ;
    }




    //JavaFX metod. När man startar projektet så är detta det absolut första som körs innan något annat händer.
    public void initialize(){
        try {
            ResultSet resultSet = dataAccessLayer.getAllFromTable("Student");
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,resultSet);
            //dataAccessLayer gör att vi kan välja vilken resultSet vi vill visa.



        } catch (SQLException e) {
            AppFunctions.unexpectedSQLError(studentFeedbackArea, e);
        }

    }

    //En metod som styr knappen för att lägga till en student.
    public void onAddStudentButton(ActionEvent event){
        try {
            String regexSSN = "[0-9]+";
            if (studentNameTextField.getText().isBlank()) {
                studentFeedbackArea.setText("Please enter a name");
            }else if (studentSSNTextField.getText().isBlank()){
                studentFeedbackArea.setText("Please enter a SSN");
            }else if (studentSSNTextField.getText().length() != 12 || !studentSSNTextField.getText().matches(regexSSN)){
                studentFeedbackArea.setText("SSN must be exactly 12 digits!");
            }else if (studentAddressTextField.getText().isBlank()){
                studentFeedbackArea.setText("Please enter an address");
            }else {
                String studentID = AppFunctions.getUniqueCode("Student", "studentID", "S");
                String studentName = studentNameTextField.getText();
                String studentSSN = studentSSNTextField.getText();
                String studentAddress = studentAddressTextField.getText();

                int i = dataAccessLayer.createStudent(studentID, studentSSN, studentName, studentAddress);
                if (i == 0) {
                    studentFeedbackArea.setText("Something went terribly wrong, no student was created");
                } else if (i == 1) {
                    studentFeedbackArea.setText("Student " + studentName + " with " + studentID + " was created");
                }
            }
            ResultSet resultSet = dataAccessLayer.getAllFromTable("Student");
            AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,resultSet);
        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(studentFeedbackArea, e);
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
                ResultSet resultSet = dataAccessLayer.getAllFromTable("Student");
                AppFunctions.updateSearchableTableView(studentTableView,searchStudentTextField,resultSet);
            }
        }catch (SQLException e){
            AppFunctions.unexpectedSQLError(studentFeedbackArea, e);
        }
    }

    public void test(ActionEvent actionEvent){
        try {
            File file = new File("C:\\Users\\Victo\\IdeaProjects\\HT21_SQL_ProgrammingAssignment\\src\\main\\resources\\reports\\AW_Customers.pdf");
            getHostServices().showDocument(file.toURI().toURL().toExternalForm());

        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }



    @FXML private AnchorPane anchorRoot;
    @FXML private AnchorPane parentContainer;

    //Metoders för att byta view
    @FXML private void loadCourseScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("courseView.fxml"));
        AppFunctions.changeView(root, addStudentButton, parentContainer, anchorRoot);
    }

    @FXML private void loadAdminScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(HelloApplication.class.getResource("adminView.fxml"));
        AppFunctions.changeView(root, addStudentButton, parentContainer, anchorRoot);
    }

    @FXML private void loadMetaScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("metaView.fxml"));
        Parent metaRoot = loader.load();
        MetaController controller = loader.getController();
        controller.setHostServices(hostServices);
        Stage metaStage = new Stage();
        metaStage.setScene(new Scene(metaRoot));
        metaStage.show();

    }
}