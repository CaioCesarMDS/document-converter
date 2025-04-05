package com.caiocesarmds.documentconverter.controller;

import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;
import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import com.caiocesarmds.documentconverter.model.FileFormat;
import com.caiocesarmds.documentconverter.service.ConversionService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.util.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DocumentConverterController {
    private static final Logger logger = LogManager.getLogger(DocumentConverterController.class);
    private static final int DEFAULT_POPUP_DURATION = 3;
    private final ConversionService conversionService = new ConversionService();

    private Stage stage;
    private Path selectedFile;
    private Path outputDirectory;

    private final Popup popup = new Popup();
    private final Label popupLabel = new Label();

    @FXML
    private TextField fileInput;
    @FXML
    private TextField directoryInput;
    @FXML
    private ComboBox<FileFormat> formatComboBox;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void initialize() {
        logger.info("Initializing Document Converter");

        configureInitialSetup();
        setupListeners();
        setupPopup();
    }

    private void configureInitialSetup() {
        Platform.runLater(() -> fileInput.getParent().requestFocus());

        String userHome = System.getProperty("user.home");
        outputDirectory = Paths.get(userHome, "Downloads");
        directoryInput.setText(outputDirectory.toString());

        formatComboBox.getItems().addAll(FileFormat.values());
    }

    private void setupListeners() {
        fileInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                selectedFile = Paths.get(fileInput.getText());
            }
        });

        directoryInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                outputDirectory = Paths.get(directoryInput.getText());
            }
        });
    }

    private void setupPopup() {
        popup.getContent().add(popupLabel);
        popupLabel.getStyleClass().add("popup-notification");
        popup.setAutoHide(true);
    }

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            selectedFile = file.toPath();
            fileInput.setText(selectedFile.toString());
        }
    }

    @FXML
    public void selectOutputDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            outputDirectory = selectedDirectory.toPath();
            directoryInput.setText(outputDirectory.toString());
        }
    }

    @FXML
    public void handleFileConversion() {
        try {
            String selectedFormat = getSelectedFormat();

            conversionService.convertFile(selectedFile, outputDirectory, selectedFormat);
            showPopupNotification("Conversion completed successfully!", DEFAULT_POPUP_DURATION);
        } catch (PathSelectionException | InvalidFormatException e) {
            logger.warn("Validation error during file conversion: {}", e.getMessage());
            showPopupNotification(e.getMessage(), DEFAULT_POPUP_DURATION + 1);
        } catch (IOException e) {
            logger.error("I/O error during file conversion: {}", e.getMessage());
            showPopupNotification(e.getMessage(), DEFAULT_POPUP_DURATION);
        } catch (ConversionFailedException e) {
            logger.error("Conversion failed for file '{}': {}", selectedFile.getFileName(), e.getMessage());
            showPopupNotification(e.getMessage(), DEFAULT_POPUP_DURATION);
        }
    }

    private String getSelectedFormat() throws InvalidFormatException {
        String selectedFormat = formatComboBox.getSelectionModel().getSelectedItem().getExtension();
        if (selectedFormat == null) {
            throw new InvalidFormatException("No format selected for conversion.");
        }
        return selectedFormat;
    }

    private void showPopupNotification(String message, int seconds) {
        popupLabel.setText(message);
        popup.show(stage);

        setPopupPosition();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> popup.hide()));
        timeline.setCycleCount(1);
        timeline.play();
    }

    private void setPopupPosition() {
        double popupWidth = popupLabel.getWidth();
        double popupHeight = popupLabel.getHeight();

        double x = stage.getX() + (stage.getWidth() - popupWidth) / 2;
        double y = stage.getY() + (stage.getHeight() - popupHeight) / 4;

        popup.setX(x);
        popup.setY(y);
    }
}
