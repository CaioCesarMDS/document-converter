package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import static org.apache.commons.io.FilenameUtils.getBaseName;

import java.awt.image.BufferedImage;
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

    public static void toImage(Path selectedFile, Path outputDirectoryPath, String selectedFormat) throws ConversionFailedException, IOException {
        String fileName = selectedFile.getFileName().toString();

        logger.info("Starting PDF to image conversion - File: {}, Format: {}", fileName, selectedFormat);

        try (PDDocument document = Loader.loadPDF(selectedFile.toFile())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int pageCount = document.getNumberOfPages();

            for (int page = 0; page < pageCount; ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DEFAULT_DPI, ImageType.RGB);

                String outputFileName = String.format("%s_%03d.%s",
                        getBaseName(fileName), page + 1, selectedFormat);

                Path outputPath = outputDirectoryPath.resolve(outputFileName);

                if (!ImageIO.write(bim, selectedFormat, outputPath.toFile())) {
                    String errorMsg = "Unsupported image format: " + selectedFormat;
                    logger.error(errorMsg);
                    throw new ConversionFailedException(errorMsg);
                }
            }
            logger.info("Conversion completed successfully. {} pages converted.", pageCount);
        }
    }

    public static void toDocx(Path selectedFile, Path outputDirectoryPath) throws ConversionFailedException {
//        try (PDDocument document = Loader.loadPDF(selectedFile)) {
//
//        } catch (IOException e) {
//            throw new ConversionFailedException("Failed to convert PDF to DOCX: " + e.getMessage());
//        }
    }
}
