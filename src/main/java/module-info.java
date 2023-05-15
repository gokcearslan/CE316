module com.example.ce316team5project {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ce316team5project to javafx.fxml;
    exports com.example.ce316team5project;
}