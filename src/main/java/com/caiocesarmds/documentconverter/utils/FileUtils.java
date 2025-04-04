package com.caiocesarmds.documentconverter.utils;

import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;

import java.io.File;

public class FileUtils {

    public static String getBaseName(File selectedFile) {
        String fileName = selectedFile.getName();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }

    public static String getExtension(File selectedFile) throws InvalidFormatException {
        String fileName = selectedFile.getName();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            throw new InvalidFormatException("No file extension found. Please select a file with a valid extension.");
        }

        return fileName.substring(lastDotIndex + 1);
    }
}
