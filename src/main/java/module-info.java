module appcode.bachelorprogrammeapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens appcode.bachelorprogrammeapp to javafx.fxml;
    exports appcode.bachelorprogrammeapp;
}