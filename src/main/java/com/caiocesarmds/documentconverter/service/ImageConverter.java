package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.controller.Controller;
import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import javax.imageio.ImageIO;

public class ImageConverter {
    private static final Logger logger = LogManager.getLogger(ImageConverter.class);
    public static void toPDF(File selectedFile, Path outputDirectoryPath) throws ConversionFailedException {
        if (!Files.exists(outputDirectoryPath)) {
            logger.warn("Output directory does not exist");
            throw new ConversionFailedException("Output directory does not exist");
        }

        if (!Files.isDirectory(outputDirectoryPath)) {
            logger.warn("Output path must be a directory");
            throw new ConversionFailedException("Output path must be a directory");
        }

        try {
            if (isImageValid(selectedFile)) {

                String outputFileName = removeExtensionFromFileName(selectedFile) + ".pdf";
                Path outputFilePath = outputDirectoryPath.resolve(outputFileName);

                try (PDDocument document = new PDDocument()) {
                    PDImageXObject pdImage = PDImageXObject.createFromFile(selectedFile.getAbsolutePath(), document);

                    PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
                    }

                    document.save(outputFilePath.toFile());

                } catch (IOException e) {
                    logger.error("Failed to convert Image to PDF: " , e);
                    throw new ConversionFailedException("Failed to convert Image to PDF: " + e.getMessage());
                } catch (Exception e) {
                    throw new ConversionFailedException("Unexpected error during conversion: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            logger.error("The image selected by the user is invalid: " , e);
            throw new ConversionFailedException("The selected image is invalid: " + e.getMessage());
        } catch (Exception e) {
            throw new ConversionFailedException("Unexpected error during conversion: " + e.getMessage());
        }
    }

    public static boolean isImageValid(File selectedFile) throws IOException {
        return ImageIO.read(selectedFile) != null;
    }

    public static String removeExtensionFromFileName(File selectedFile) {
        String fileName = selectedFile.getName();

        int lastDotIndex = fileName.lastIndexOf(".");

        if (lastDotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }
}
