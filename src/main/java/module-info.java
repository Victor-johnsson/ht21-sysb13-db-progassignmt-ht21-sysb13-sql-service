module com.sqlservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.sqlservice to javafx.fxml;
    exports com.sqlservice;
}