package com.caiocesarmds.documentconverter.utils;

import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;

import java.io.File;
import java.nio.file.Files;

public class ValidationUtils {
    public static void validateFile(File selectedFile) throws PathSelectionException {
        if (selectedFile == null || !selectedFile.exists()) {
            throw new PathSelectionException("No file selected for conversion.");
        }
    }

    public static void validatePath(File outputDirectory) throws PathSelectionException {
        if (outputDirectory == null) {
            throw new PathSelectionException("No directory selected for conversion.");
        }

        if (!Files.exists(outputDirectory.toPath())) {
            throw new PathSelectionException("Output directory does not exist: " + outputDirectory);
        }

        if (!Files.isDirectory(outputDirectory.toPath())) {
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
