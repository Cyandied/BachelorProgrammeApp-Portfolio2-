module appcode.bachelorprogrammeapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens appcode.bachelorprogrammeapp to javafx.fxml;
    exports appcode.bachelorprogrammeapp;
}