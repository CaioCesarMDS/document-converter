package com.caiocesarmds.documentconverter.controller;

import com.caiocesarmds.documentconverter.model.FileFormat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DocumentConverterUIInitializer {
    private final TextField fileInput;
    private final TextField directoryInput;
    private final ComboBox<FileFormat> formatComboBox;
    private final Consumer<Path> onFileSelected;
    private final Consumer<Path> onDirectorySelected;

    public DocumentConverterUIInitializer(TextField fileInput, TextField directoryInput, ComboBox<FileFormat> formatComboBox,Consumer<Path> onFileSelected, Consumer<Path> onDirectorySelected) {
        this.fileInput = fileInput;
        this.directoryInput = directoryInput;
        this.formatComboBox = formatComboBox;
        this.onFileSelected = onFileSelected;
        this.onDirectorySelected = onDirectorySelected;
    }

    public Path configureInitialSetup() {
        Platform.runLater(() -> fileInput.getParent().requestFocus());

        String userHome = System.getProperty("user.home");
        Path defaultOutputDirectory = Paths.get(userHome, "Downloads");
        directoryInput.setText(defaultOutputDirectory.toString());

        formatComboBox.getItems().addAll(FileFormat.values());

        return defaultOutputDirectory;
    }

    public void setupListeners() {
        fileInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                onFileSelected.accept(Path.of(fileInput.getText()));
            }
        });

        directoryInput.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                onDirectorySelected.accept(Path.of(directoryInput.getText()));
            }
        });
    }
}