package appcode.bachelorprogrammeapp;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene; import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
public class MainApplication extends Application {
    Controller controller;
    private int totalEcts = 0;
    private Label totalEctsLabel = new Label("Total ECTS: 0");

    private int sessionID;

    @Override
    public void init(){
        controller = new Controller();
        sessionID = new Random().nextInt(10000,99999);
    }
    @Override
    public void start(Stage stage) {
        HBox mainLayout = new HBox(10);

        List<String> programs = controller.getProgrammes();
        VBox programColumn = createProgramColumn(programs);
        VBox subject1Column = createSubjectColumn("Subject 1", controller.getSubMods());
        VBox subject2Column = createSubjectColumn("Subject 2", controller.getSubMods());
        VBox electiveColumn = createElectiveColumn(programs);

        controller.createBachProgram(sessionID);

        mainLayout.getChildren().addAll(programColumn, subject1Column, subject2Column, electiveColumn);
        VBox appLayout = new VBox(mainLayout, totalEctsLabel);
        Scene scene = new Scene(appLayout, 1050, 600);
        stage.setTitle("Bachelor program planner");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createProgramColumn(List<String> programs) {
        return createSelectableColumn("Program", programs, controller::getProgramCourses,controller::getProgramProjects);
    }

    private VBox createSubjectColumn(String label, List<String> subjects) {
        return createSelectableColumn(label, subjects, controller::getSubModCourses,controller::getSubmodProjects);
    }

    private VBox createSelectableColumn(String label, List<String> items, Function<String, List<String>> coursesFunction,Function<String,List<String>> projectsFunction) {
        Label columnLabel = new Label(label);
        ComboBox<String> categoryComboBox = new ComboBox<>(FXCollections.observableArrayList(items));
        ComboBox<String> courseComboBox = new ComboBox<>();
        ListView<String> listView = new ListView<>();
        Button addButton = new Button("Add");
        Button selectButton = new Button("Select");
        Label ectsLabel = new Label("course ECTS: 0");

        categoryComboBox.setDisable(false);
        courseComboBox.setDisable(true);
        addButton.setDisable(true);

        selectButton.setOnAction(event -> {
            String selectedCategory = categoryComboBox.getValue();
            if (selectedCategory != null) {
                courseComboBox.setItems(FXCollections.observableArrayList(coursesFunction.apply(selectedCategory)));
                List<String> projects = projectsFunction.apply(selectedCategory);
                for(String project : projects){
                    listView.getItems().add(project);
                    controller.putProjectInBachProgram(sessionID,project);
                }
                categoryComboBox.setDisable(true);
                courseComboBox.setDisable(false);
                addButton.setDisable(false);
            }
        });

        addButton.setOnAction(event -> {
            String selectedCourse = courseComboBox.getValue();
            if (selectedCourse != null && !listView.getItems().contains(selectedCourse)) {
                listView.getItems().add(selectedCourse);
                controller.putCourseInBachProgram(sessionID,selectedCourse);
                ectsLabel.setText("course ECTS: " + controller.getCoursesWeight(listView.getItems()));
                totalEcts = controller.getTotalBachProgramECTs(sessionID);
                totalEctsLabel.setText("Total ECTS: " + totalEcts);
            }
        });

        VBox column = new VBox(5, columnLabel, categoryComboBox, selectButton, courseComboBox, addButton, listView, ectsLabel);
        return column;
    }

    private VBox createElectiveColumn(List<String> programs) {
        Label electiveLabel = new Label("Elective");
        ComboBox<String> electiveComboBox = new ComboBox<>();
        ListView<String> listView = new ListView<>();
        Button addButton = new Button("Add");
        Label ectsLabel = new Label("ECTS: 0");

        ArrayList<String> programCourses = new ArrayList<>();
        for(String program : programs){
            programCourses.addAll(controller.getProgramCourses(program));
        }
        List<String> electiveCourses = programCourses.stream()
                .filter(course -> !course.startsWith("BK"))
                .collect(Collectors.toList());
        electiveComboBox.setItems(FXCollections.observableArrayList(electiveCourses));

        addButton.setOnAction(event -> {
            String selectedCourse = electiveComboBox.getValue();
            if (selectedCourse != null && !listView.getItems().contains(selectedCourse)) {
                listView.getItems().add(selectedCourse);
                controller.putCourseInBachProgram(sessionID,selectedCourse);
                ectsLabel.setText("ECTS: " + controller.getCoursesWeight(listView.getItems()));
                totalEcts = controller.getTotalBachProgramECTs(sessionID);
                totalEctsLabel.setText("Total ECTS: " + totalEcts);
            }
        });

        VBox column = new VBox(5, electiveLabel, electiveComboBox, addButton, listView, ectsLabel);
        return column;
    }

    public static void main(String[] args) {
        launch(args);
    }
}