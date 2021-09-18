module com.ht21sysb13dbprogassignmtht21sysb13sqlservice {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.sqlservice to javafx.fxml;
    exports com.sqlservice;
}