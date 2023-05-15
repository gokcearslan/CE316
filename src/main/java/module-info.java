module com.example.ce316project10may {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.example.ce316project10may to javafx.fxml;
    exports com.example.ce316project10may;
}