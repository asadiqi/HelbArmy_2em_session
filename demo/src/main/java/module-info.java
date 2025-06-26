module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.example.demo.units;
    opens com.example.demo.units to javafx.fxml;
    exports com.example.demo.collectable;
    opens com.example.demo.collectable to javafx.fxml;
    exports com.example.demo.ressource;
    opens com.example.demo.ressource to javafx.fxml;
}