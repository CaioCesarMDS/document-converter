package com.caiocesarmds.documentconverter.controller;

import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;
import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import com.caiocesarmds.documentconverter.service.ImageConverter;
import com.caiocesarmds.documentconverter.service.PDFConverter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);

    private Stage stage;
    private File selectedFile;
    private File outputDirectory;

    private final Popup popup = new Popup();
    private final Label popupLabel = new Label();

    @FXML
    private ComboBox<String> formatComboBox;
    @FXML
    private TextField fileInput;
    @FXML
    private TextField directoryInput;

    public void initialize() {
        logger.info("Initializing Document Converter");

        formatComboBox.getItems().addAll("PDF", "DOCX", "JPG", "PNG");

        String userHome = System.getProperty("user.home");
        outputDirectory = new File(userHome, File.separator + "Downloads");
        directoryInput.setText(outputDirectory.toString());

        popup.getContent().add(popupLabel);
        popupLabel.getStyleClass().add("popup-notification");
        popup.setAutoHide(true);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a file");

        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            fileInput.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void selectOutputDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder");
        File selectedDirectory = directoryChooser.showDialog(stage);

        if (selectedDirectory != null) {
            outputDirectory = selectedDirectory;
            directoryInput.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    public void handleFileConversion() {
        try {
            String selectedFormat = getSelectedFormat();
            String fileExtension = getExtension(selectedFile);

            validateInputs(selectedFormat, fileExtension);

            convertFile(selectedFile, outputDirectory.toPath(), selectedFormat, fileExtension);

        } catch (PathSelectionException | InvalidFormatException e) {
            logger.warn("User action error: {}", e.getMessage());
            showPopupNotification(e.getMessage(), 4);
        } catch (ConversionFailedException e) {
            logger.error("Unexpected error in conversion", e);
            showPopupNotification(e.getMessage(), 2);
        }
    }

    private void convertFile(File selectedFile, Path outputDirectoryPath, String selectedFormat, String fileExtension) throws ConversionFailedException {
        try {
            switch (fileExtension) {
                case "pdf":
                    if (selectedFormat.equals("png") || selectedFormat.equals("jpg")) {
                        PDFConverter.toImage(selectedFile, outputDirectoryPath, selectedFormat);
                        showPopupNotification("PDF converted successfully!", 2);
                    } else if (selectedFormat.equals("docx")) {
                        PDFConverter.toDocx(selectedFile, outputDirectoryPath);
                    } else {
                        logger.warn("Unsupported format: {}", selectedFormat);
                        showPopupNotification("File format not supported!", 2);
                    }
                    break;
                case "jpg", "png":
                    if (selectedFormat.equals("pdf")) {
                        ImageConverter.toPDF(selectedFile, outputDirectoryPath);
                        showPopupNotification("Image converted successfully!", 2);
                    }
                    break;
            }
        } catch (ConversionFailedException e) {
            logger.error("Unexpected error in conversion", e);
            showPopupNotification("Conversion Error: " + e.getMessage(), 4);
        }
    }

    private String getSelectedFormat() throws InvalidFormatException {
        String selectedFormat = formatComboBox.getSelectionModel().getSelectedItem();
        if (selectedFormat == null) {
            throw new InvalidFormatException("No format selected for conversion.");
        }
        return selectedFormat.toLowerCase();
    }

    private void validateInputs(String selectedFormat, String fileExtension) throws InvalidFormatException, PathSelectionException {
        if (selectedFile == null) {
            throw new PathSelectionException("No file selected for conversion.");
        }

        if (outputDirectory == null || !Files.exists(outputDirectory.toPath())) {
            throw new PathSelectionException("No directory selected for conversion.");
        }

        if (selectedFormat == null) {
            throw new InvalidFormatException("No format selected for conversion.");
        }

        if (fileExtension.equals(selectedFormat)) {
            logger.info("User tried to convert {} but it's already in the desired format.", selectedFile.getName());
            throw new InvalidFormatException("The file is already in the desired format.");
        }
    }

    private String getExtension(File file) throws InvalidFormatException {
        String fileName = file.getName();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new InvalidFormatException("Unsupported file format.");
        }

        return fileName.substring(lastDotIndex + 1);
    }

    private void showPopupNotification(String message, int seconds) {
        popupLabel.setText(message);
        popup.show(stage);

        double popupWidth = popupLabel.getWidth();
        double popupHeight = popupLabel.getHeight();

        double x = stage.getX() + (stage.getWidth() - popupWidth) / 2;
        double y = stage.getY() + (stage.getHeight() - popupHeight) / 4;

        popup.setX(x);
        popup.setY(y);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(seconds), e -> popup.hide()));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
