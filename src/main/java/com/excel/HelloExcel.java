package com.excel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloExcel extends Application {
    //klassen som startar hela programmet

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("src/main/java/com/excel/excelView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Contoso University Administrator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }


}