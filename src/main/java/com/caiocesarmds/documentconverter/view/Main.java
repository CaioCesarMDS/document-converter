package com.caiocesarmds.documentconverter.view;

import com.caiocesarmds.documentconverter.controller.DocumentConverterController;

import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        try {
            URL fxmlUrl = getClass().getResource("/layout.fxml");
            if (fxmlUrl == null) {
                throw new IOException("FXML layout not found at /layout.fxml");
            }
            FXMLLoader loader = new FXMLLoader(fxmlUrl);

            Parent root = loader.load();

            stage.getIcons().add(new Image("/images/pdf-icon.png"));
            stage.setTitle("PDF Converter");
            stage.setResizable(false);
            stage.setScene(createScene(root));

            DocumentConverterController controller = loader.getController();
            controller.setStage(stage);

            Font.loadFont(getClass().getResourceAsStream("/fonts/NotoSans.ttf"), 18);

            stage.show();
        } catch (IOException e) {
            System.err.println("Error loading Document Converter: " + e);
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private Scene createScene(Parent root) {
        Scene scene = new Scene(root, 900, 700);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        return scene;
    }
}