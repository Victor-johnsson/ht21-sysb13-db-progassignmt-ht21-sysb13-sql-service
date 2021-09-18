module com.sqlservice {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sqlservice to javafx.fxml;
    exports com.sqlservice;
}