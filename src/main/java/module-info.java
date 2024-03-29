module com.app.cardsapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.mongodb.driver.sync.client;
    requires lombok;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;

    opens com.app.cardsapplication to javafx.fxml;
    opens com.app.cardsapplication.controller to javafx.fxml;
    exports com.app.cardsapplication.models to org.mongodb.bson;
    exports com.app.cardsapplication;
}