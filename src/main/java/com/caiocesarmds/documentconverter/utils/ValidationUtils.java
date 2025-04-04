package com.caiocesarmds.documentconverter.utils;

import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;

import java.nio.file.Files;
import java.nio.file.Path;

public class ValidationUtils {
    public static void validateFile(Path selectedFile) throws PathSelectionException {
        if (selectedFile == null || !Files.exists(selectedFile)) {
            throw new PathSelectionException("The selected file does not exist.");
        }
    }

    public static void validatePath(Path outputDirectory) throws PathSelectionException {
        if (outputDirectory == null) {
            throw new PathSelectionException("Please select a valid output directory.");
        }

        if (!Files.exists(outputDirectory)) {
            throw new PathSelectionException("The selected output directory does not exist: " + outputDirectory);
        }

        if (!Files.isDirectory(outputDirectory)) {
            throw new PathSelectionException("The selected path must be a directory.");
        }
    }

    public static void validateFormat(String selectedFormat, String fileExtension) throws InvalidFormatException {
        if (selectedFormat == null || selectedFormat.isEmpty()) {
            throw new InvalidFormatException("No output format was selected.");
        }

        if (selectedFormat.equalsIgnoreCase(fileExtension)) {
            throw new InvalidFormatException("The file is already in the selected format.");
        }
    }
}
