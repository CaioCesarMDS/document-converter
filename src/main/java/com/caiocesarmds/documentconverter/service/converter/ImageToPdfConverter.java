package com.caiocesarmds.documentconverter.service.converter;

import com.caiocesarmds.documentconverter.exceptions.system.ConversionFailedException;

import com.caiocesarmds.documentconverter.model.request.ConversionRequest;
import com.caiocesarmds.documentconverter.model.response.ConversionResponse;
import com.caiocesarmds.documentconverter.model.response.SingleFileConversionResponse;
import com.caiocesarmds.documentconverter.service.interfaces.SpecificConverter;

import static com.caiocesarmds.documentconverter.utils.FileUtils.generateOutputPath;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

public class ImageToPdfConverter implements SpecificConverter {

    @Override
    public boolean supports(ConversionRequest request) {
        String fileExtension = request.getFileExtension();
        String targetFormat = request.getTargetFormat();
        return ((fileExtension.equals("jpg") || fileExtension.equals("png")) && targetFormat.equals("pdf"));
    }

    @Override
    public ConversionResponse convert(ConversionRequest request) throws ConversionFailedException, IOException {
        System.out.println(request.getFileExtension());
        Path imageFile = request.getSelectedFile();
        Path outputPdfPath = generateOutputPath(imageFile, request.getOutputDirectory(), request.getFileExtension());

        validateImage(imageFile);
        createPdfFromImage(imageFile, outputPdfPath);

        return new SingleFileConversionResponse(outputPdfPath, Files.size(outputPdfPath), true);
    }

    private void createPdfFromImage(Path imageFile, Path outputPdfPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDImageXObject pdImage = PDImageXObject.createFromFile(imageFile.toString(), document);

            PDPage page = new PDPage(new PDRectangle(pdImage.getWidth(), pdImage.getHeight()));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
            }

            document.save(outputPdfPath.toFile());
        }
    }

    private void validateImage(Path selectedFile) throws ConversionFailedException, IOException {
        try {
            BufferedImage img = ImageIO.read(selectedFile.toFile());
            if (img == null || img.getWidth() < 1 || img.getHeight() < 1) {
                throw new ConversionFailedException("The image " + selectedFile.getFileName() + " is corrupted or unreadable.");
            }
        } catch (IOException e) {
            throw new ConversionFailedException("Failed to read image file: " + selectedFile.getFileName());
        }

    }
}