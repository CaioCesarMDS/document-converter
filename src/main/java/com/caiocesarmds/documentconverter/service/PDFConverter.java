package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;

public class PDFConverter {
    private static final Logger logger = LogManager.getLogger(PDFConverter.class);
    private static final int DEFAULT_DPI = 300;

    public static void toImage(File selectedFile, Path outputDirectoryPath, String selectedFormat) throws ConversionFailedException {

        logger.info("Starting PDF to image conversion - File: {}, Format: {}", selectedFile.getName(), selectedFormat);

        try (PDDocument document = Loader.loadPDF(selectedFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int pageCount = document.getNumberOfPages();

            for (int page = 0; page < pageCount; ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DEFAULT_DPI, ImageType.RGB);

                String outputFileName = String.format("%s_%03d.%s",
                        getBaseName(selectedFile.getName()), page + 1, selectedFormat);

                Path outputPath = outputDirectoryPath.resolve(outputFileName);

                if (!ImageIO.write(bim, selectedFormat, outputPath.toFile())) {
                    String errorMsg = "Unsupported image format: " + selectedFormat;
                    logger.error(errorMsg);
                    throw new ConversionFailedException(errorMsg);
                }
            }
            logger.info("Conversion completed successfully. {} pages converted.", pageCount);
        } catch (IOException e) {
            String errorMsg = "Failed to convert PDF: " + e.getMessage();
            logger.error(errorMsg);
            throw new ConversionFailedException(errorMsg);
        } catch (Exception e) {
            String errorMsg = "Unexpected error during conversion: " + e.getMessage();
            logger.error(errorMsg);
            throw new ConversionFailedException(errorMsg);
        }
    }

    public static void toDocx(File selectedFile, Path outputDirectoryPath) throws ConversionFailedException {
//        try (PDDocument document = Loader.loadPDF(selectedFile)) {
//
//        } catch (IOException e) {
//            throw new ConversionFailedException("Failed to convert PDF to DOCX: " + e.getMessage());
//        }
    }
}
