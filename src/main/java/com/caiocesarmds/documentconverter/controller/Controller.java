package com.caiocesarmds.documentconverter.controller;

import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;
import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import static com.caiocesarmds.documentconverter.utils.FileUtils.getExtension;
import static com.caiocesarmds.documentconverter.utils.ValidationUtils.validateFile;
import static com.caiocesarmds.documentconverter.utils.ValidationUtils.validateFormat;
import static com.caiocesarmds.documentconverter.utils.ValidationUtils.validatePath;

import com.caiocesarmds.documentconverter.service.ImageConverter;
import com.caiocesarmds.documentconverter.service.PDFConverter;

import java.io.File;
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
    private static final int DEFAULT_POPUP_DURATION = 3;

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

        configureInitialSetup();
        setupPopup();
        setupListeners();
    }

    private void configureInitialSetup() {
        formatComboBox.getItems().addAll("PDF", "DOCX", "JPG", "PNG");

        String userHome = System.getProperty("user.home");
        outputDirectory = new File(userHome, File.separator + "Downloads");
        directoryInput.setText(outputDirectory.toString());
    }

    private void setupPopup() {
        popup.getContent().add(popupLabel);
        popupLabel.getStyleClass().add("popup-notification");
        popup.setAutoHide(true);
    }

    private void setupListeners() {
        fileInput.textProperty().addListener(((observableValue, oldValue, newValue) -> selectedFile = new File(fileInput.getText())));

        directoryInput.textProperty().addListener(((observableValue, oldValue, newValue) -> outputDirectory = new File(directoryInput.getText())));
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
            validateFile(selectedFile);
            validatePath(outputDirectory);

            String selectedFormat = getSelectedFormat();
            String fileExtension = getExtension(selectedFile);

            validateFormat(selectedFormat, fileExtension);

            convertFile(selectedFile, outputDirectory.toPath(), selectedFormat, fileExtension);

        } catch (PathSelectionException | InvalidFormatException e) {
            logger.warn("User action error: {}", e.getMessage(), e);
            showPopupNotification(e.getMessage(), 4);
        } catch (ConversionFailedException e) {
            logger.error("Unexpected error in conversion: {} ", e.getMessage(), e);
            showPopupNotification(e.getMessage(), 2);
        }
    }

    private void convertFile(File selectedFile, Path outputDirectoryPath, String selectedFormat, String fileExtension) throws ConversionFailedException, InvalidFormatException {
        switch (fileExtension) {
            case "pdf":
                handlePdfConversion(selectedFile, outputDirectoryPath, selectedFormat);
                break;
            case "jpg", "png":
                handleImageConversion(selectedFormat, outputDirectoryPath);
                break;
            default:
                throw new InvalidFormatException("Unsupported file type: " + fileExtension);
        }
    }

    private void handlePdfConversion(File selectedFile, Path outputDirectoryPath, String selectedFormat) throws ConversionFailedException {
        if (selectedFormat.equals("png") || selectedFormat.equals("jpg")) {
            PDFConverter.toImage(selectedFile, outputDirectoryPath, selectedFormat);
        } else if (selectedFormat.equals("docx")) {
            PDFConverter.toDocx(selectedFile, outputDirectoryPath);
        }

        showPopupNotification("PDF converted successfully!", 2);
    }

    private void handleImageConversion(String selectedFormat, Path outputDirectoryPath) throws ConversionFailedException {
        if (selectedFormat.equals("pdf")) {
            ImageConverter.handleConvert(selectedFile, outputDirectoryPath);
            showPopupNotification("Image converted successfully!", 2);
        }
    }

    private String getSelectedFormat() throws InvalidFormatException {
        String selectedFormat = formatComboBox.getSelectionModel().getSelectedItem();
        if (selectedFormat == null) {
            throw new InvalidFormatException("No format selected for conversion.");
        }
        return selectedFormat.toLowerCase();
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
