package com.caiocesarmds.documentconverter.utils;

import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;

import java.nio.file.Files;
import java.nio.file.Path;

public class ValidationUtils {
    public static void validateFile(Path selectedFile) throws PathSelectionException {
        if (selectedFile == null || !Files.exists(selectedFile)) {
            throw new PathSelectionException("No file selected for conversion.");
        }
    }

    public static void validatePath(Path outputDirectory) throws PathSelectionException {
        if (outputDirectory == null) {
            throw new PathSelectionException("No directory selected for conversion.");
        }

        if (!Files.exists(outputDirectory)) {
            throw new PathSelectionException("Output directory does not exist: " + outputDirectory);
        }

        if (!Files.isDirectory(outputDirectory)) {
            throw new PathSelectionException("Output path must be a directory");
        }
    }

    public static void validateFormat(String selectedFormat, String fileExtension) throws InvalidFormatException {
        if (selectedFormat == null || selectedFormat.isEmpty()) {
            throw new InvalidFormatException("No format selected for conversion.");
        }

        if (selectedFormat.equalsIgnoreCase(fileExtension)) {
            throw new InvalidFormatException("File already in the selected format.");
        }
    }
}
