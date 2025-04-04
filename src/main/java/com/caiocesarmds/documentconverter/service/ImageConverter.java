package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import static com.caiocesarmds.documentconverter.utils.FileUtils.getBaseName;

import java.io.IOException;
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

    public static void handleConvert(Path selectedFile, Path outputDirectoryPath) throws ConversionFailedException, IOException {
        if (isImageValid(selectedFile)) {
            String outputFileName = getBaseName(selectedFile) + ".pdf";
            Path outputFilePath = outputDirectoryPath.resolve(outputFileName);
            imageToPDF(selectedFile, outputFilePath);
        }
    }

    private static void imageToPDF(Path selectedFile, Path outputFilePath) throws IOException {
        String fileName = selectedFile.getFileName().toString();

        logger.info("Starting image to PDF conversion - File: {}",
                fileName);

        try (PDDocument document = new PDDocument()) {
            PDImageXObject pdImage = PDImageXObject.createFromFile(selectedFile.toString(), document);

            PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
            }

            document.save(outputFilePath.toFile());

            logger.info("Conversion completed successfully. - Path: {}", outputFilePath);
        }
    }

    private static boolean isImageValid(Path selectedFile) throws ConversionFailedException, IOException {
        if (ImageIO.read(selectedFile.toFile()) == null) {
            throw new ConversionFailedException("The image is not valid");
        }
        return true;
    }
}
