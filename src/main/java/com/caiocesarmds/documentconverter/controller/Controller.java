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
import java.io.IOException;
import java.nio.file.Files;
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

public class Controller {
    private static final Logger logger = LogManager.getLogger(Controller.class);
    private static final int DEFAULT_POPUP_DURATION = 3;

    private Stage stage;
    private Path selectedFile;
    private Path outputDirectory;

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
        Platform.runLater(() -> fileInput.getParent().requestFocus());

        String userHome = System.getProperty("user.home");
        outputDirectory = Paths.get(userHome, "Downloads");
        directoryInput.setText(outputDirectory.toString());

        formatComboBox.getItems().addAll("PDF", "DOCX", "JPG", "PNG");
    }

    private void setupPopup() {
        popup.getContent().add(popupLabel);
        popupLabel.getStyleClass().add("popup-notification");
        popup.setAutoHide(true);
    }

    private void setupListeners() {
        fileInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                Path filePath = Paths.get(fileInput.getText());
                if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                    showPopupNotification("The selected file does not exist.", DEFAULT_POPUP_DURATION);
                } else {
                    selectedFile = filePath;
                }
            }
        });

        directoryInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                Path dirPath = Paths.get(directoryInput.getText());
                if (!Files.exists(dirPath) || !Files.isDirectory(dirPath)) {
                    showPopupNotification("Please select a valid output directory.", DEFAULT_POPUP_DURATION);
                } else {
                    outputDirectory = dirPath;
                }
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
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
            validateFile(selectedFile);
            validatePath(outputDirectory);

            String selectedFormat = getSelectedFormat();
            String fileExtension = getExtension(selectedFile);

            validateFormat(selectedFormat, fileExtension);

            convertFile(selectedFile, outputDirectory, selectedFormat, fileExtension);

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

    private void convertFile(Path selectedFile, Path outputDirectoryPath, String selectedFormat, String fileExtension) throws ConversionFailedException, IOException, InvalidFormatException {
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

    private void handlePdfConversion(Path selectedFile, Path outputDirectoryPath, String selectedFormat) throws ConversionFailedException, IOException {
        if (selectedFormat.equals("png") || selectedFormat.equals("jpg")) {
            PDFConverter.toImage(selectedFile, outputDirectoryPath, selectedFormat);
        } else if (selectedFormat.equals("docx")) {
            PDFConverter.toDocx(selectedFile, outputDirectoryPath);
        }

        showPopupNotification("PDF converted successfully!", DEFAULT_POPUP_DURATION);
    }

    private void handleImageConversion(String selectedFormat, Path outputDirectoryPath) throws ConversionFailedException, IOException {
        if (selectedFormat.equals("pdf")) {
            ImageConverter.handleConvert(selectedFile, outputDirectoryPath);
            showPopupNotification("Image converted successfully!", DEFAULT_POPUP_DURATION);
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
