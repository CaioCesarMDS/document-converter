package com.caiocesarmds.documentconverter.view;


import com.caiocesarmds.documentconverter.controller.Controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

            stage.getIcons().add(new Image("/images/pdf-icon.png"));
            stage.setTitle("PDF Converter");
            stage.setResizable(false);
            stage.setScene(scene);

            Controller controller = loader.getController();
            controller.setStage(stage);

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Document Converter: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}