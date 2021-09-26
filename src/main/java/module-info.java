module com.sqlservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;


    opens com.sqlservice to javafx.fxml;
    exports com.sqlservice;
}