package com.caiocesarmds.documentconverter.utils;

import com.caiocesarmds.documentconverter.exceptions.validation.InvalidFormatException;

import java.nio.file.Path;

public class FileUtils {

    public static String getBaseName(Path selectedFile) {
        String fileName = (selectedFile.getFileName()).toString();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }

    public static String getExtension(Path selectedFile) throws InvalidFormatException {
        String fileName = (selectedFile.getFileName()).toString();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new InvalidFormatException("No file extension found. Please select a file with a valid extension.");
        }

        return fileName.substring(lastDotIndex + 1);
    }

    public static Path generateOutputPath(Path inputFile, Path outputDirectory, String newExtension) {
        String fileName = getBaseName(inputFile) + "." + newExtension;
        return outputDirectory.resolve(fileName);
    }
}