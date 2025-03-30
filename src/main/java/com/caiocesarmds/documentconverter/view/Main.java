package com.caiocesarmds.documentconverter.view;


import com.caiocesarmds.documentconverter.controller.Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}