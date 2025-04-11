package com.caiocesarmds.documentconverter.service.impl;

import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;
import com.caiocesarmds.documentconverter.exceptions.InvalidFormatException;
import com.caiocesarmds.documentconverter.exceptions.PathSelectionException;
import com.caiocesarmds.documentconverter.model.ConversionRequest;
import com.caiocesarmds.documentconverter.model.ConversionResponse;
import com.caiocesarmds.documentconverter.service.DocumentConversionService;

import java.io.IOException;
import java.nio.file.Path;

import static com.caiocesarmds.documentconverter.utils.FileUtils.getExtension;
import static com.caiocesarmds.documentconverter.utils.ValidationUtils.*;

public class ConversionService implements DocumentConversionService {

    @Override
    public ConversionResponse convert(ConversionRequest request) throws ConversionFailedException, {
        try {
            validateFile(request.getSelectedFile());
            validatePath(request.getOutputDirectory());

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
        } catch (InvalidFormatException | PathSelectionException e) {
            System.out.println("erro: " + e);
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
