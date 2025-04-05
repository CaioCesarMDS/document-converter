package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;
import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;

import java.io.IOException;
import java.nio.file.Path;

import static com.caiocesarmds.documentconverter.utils.FileUtils.getExtension;
import static com.caiocesarmds.documentconverter.utils.ValidationUtils.*;

public class ConversionService {

    public void convertFile(Path selectedFile, Path outputDirectory, String selectedFormat) throws ConversionFailedException, IOException, InvalidFormatException, PathSelectionException {
        validateFile(selectedFile);
        validatePath(outputDirectory);

        String fileExtension = getExtension(selectedFile);

        validateFormat(selectedFormat, fileExtension);

        switch (fileExtension) {
            case "pdf":
                handlePdfConversion(selectedFile, outputDirectory, selectedFormat);
                break;
            case "jpg", "png":
                handleImageConversion(selectedFile, outputDirectory, selectedFormat);
                break;
            default:
                throw new InvalidFormatException("Unsupported file type: " + fileExtension);
        }
    }

    private void handlePdfConversion(Path selectedFile, Path outputDirectory, String selectedFormat) throws ConversionFailedException, IOException {
        if (selectedFormat.equals("png") || selectedFormat.equals("jpg")) {
            PDFConverter.toImage(selectedFile, outputDirectory, selectedFormat);
        } else if (selectedFormat.equals("docx")) {
            PDFConverter.toDocx(selectedFile, outputDirectory);
        }
    }

    private void handleImageConversion(Path selectedFile, Path outputDirectory, String selectedFormat) throws ConversionFailedException, IOException {
        if (selectedFormat.equals("pdf")) {
            ImageConverter.handleConvert(selectedFile, outputDirectory);
        }
    }
}
