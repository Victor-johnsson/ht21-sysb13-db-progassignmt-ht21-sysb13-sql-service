package com.sqlservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;


public class HelloApplication extends Application {
    //klassen som startar hela programmet

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("studentView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        DataAccessLayer dataAccessLayer = new DataAccessLayer();
        try {
            double d = dataAccessLayer.percentageOfA("c00002", "A");
            System.out.println(d);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch();
    }
}