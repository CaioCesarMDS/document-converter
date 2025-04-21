package com.caiocesarmds.documentconverter.controller;

import com.caiocesarmds.documentconverter.exceptions.system.ConversionFailedException;
import com.caiocesarmds.documentconverter.exceptions.validation.InvalidFormatException;

import com.caiocesarmds.documentconverter.model.request.ConversionRequest;
import com.caiocesarmds.documentconverter.model.FileFormat;
import com.caiocesarmds.documentconverter.model.response.ConversionResponse;
import com.caiocesarmds.documentconverter.service.ConversionService;

import com.caiocesarmds.documentconverter.service.converter.ImageToPdfConverter;
import com.caiocesarmds.documentconverter.service.converter.PdfToImageConverter;

import com.caiocesarmds.documentconverter.utils.NotificationUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import org.apache.logging.log4j.*;

import static com.caiocesarmds.documentconverter.utils.FileUtils.getExtension;

public class DocumentConverterController {
    private static final Logger logger = LogManager.getLogger(DocumentConverterController.class);

    private final ConversionService conversionService = new ConversionService(List.of(new PdfToImageConverter(), new ImageToPdfConverter()));

    private Stage stage;
    private Path selectedFile;
    private Path outputDirectory;

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

        DocumentConverterUIInitializer initializer = new DocumentConverterUIInitializer(fileInput, directoryInput, formatComboBox, path -> selectedFile = path, path -> outputDirectory = path);
        outputDirectory = initializer.configureInitialSetup();
        initializer.setupListeners();
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
            validateConversionInputs();

            String selectedFormat = getSelectedFormat();
            String fileExtension = getExtension(selectedFile);

            ConversionRequest request = new ConversionRequest(selectedFile, outputDirectory, selectedFormat, fileExtension);
            ConversionResponse response = conversionService.convert(request);

            logger.info("Successfully converted file: {}", selectedFile);
            Platform.runLater(() -> NotificationUtils.showPopup(stage, response.getUserMessage(), 5));

        } catch (InvalidFormatException e) {
            logger.warn("Validation error during file conversion: {}", e.getMessage());
            Platform.runLater(() -> NotificationUtils.showPopup(stage, e.getMessage(), 4));
        } catch (ConversionFailedException e) {
            logger.error("Conversion failed for file '{}': {}", selectedFile.getFileName(), e.getMessage());
            Platform.runLater(() -> NotificationUtils.showPopup(stage, e.getMessage()));
        }
    }

    private void validateConversionInputs() throws InvalidFormatException {
        if (selectedFile == null) {
            throw new InvalidFormatException("No file selected for conversion.");
        }

        if (outputDirectory == null) {
            throw new InvalidFormatException("No output directory selected.");
        }

        if (formatComboBox.getSelectionModel().getSelectedItem() == null) {
            throw new InvalidFormatException("No format selected for conversion.");
        }
    }

    private String getSelectedFormat() {
        return formatComboBox.getSelectionModel().getSelectedItem().getExtension();
    }
}