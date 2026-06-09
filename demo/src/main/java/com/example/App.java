package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App - Main Entry Point
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("fxml/login"), 1000, 650);
        
        // Add global stylesheet
        scene.getStylesheets().add(App.class.getResource("css/styles.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("WisataKu - Sistem Informasi Wisata");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        
        // Optional: add icon if available
        // stage.getIcons().add(new Image(App.class.getResourceAsStream("images/icon.png")));
        
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML("fxml/" + fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}