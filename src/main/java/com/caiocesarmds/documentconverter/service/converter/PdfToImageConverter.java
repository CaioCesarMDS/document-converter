package com.caiocesarmds.documentconverter.service.converter;

import com.caiocesarmds.documentconverter.exceptions.system.ConversionFailedException;

import com.caiocesarmds.documentconverter.model.request.ConversionRequest;
import com.caiocesarmds.documentconverter.model.response.ConversionResponse;
import com.caiocesarmds.documentconverter.model.response.MultiFileConversionResponse;
import com.caiocesarmds.documentconverter.service.interfaces.SpecificConverter;

import static com.caiocesarmds.documentconverter.utils.FileUtils.getBaseName;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;

public class PdfToImageConverter implements SpecificConverter {
    private static final int DEFAULT_DPI = 300;

    @Override
    public boolean supports(ConversionRequest request) {
        return (request.getFileExtension().equals("pdf") && (request.getTargetFormat().equals("png") || request.getTargetFormat().equals("jpg")));
    }

    @Override
    public ConversionResponse convert(ConversionRequest request) throws ConversionFailedException, IOException {
        Path pdfFile = request.getSelectedFile();
        long totalSize = 0;

        try (PDDocument document = Loader.loadPDF(pdfFile.toFile())) {
            int pageCount = document.getNumberOfPages();

            if (pageCount == 0) {
                throw new ConversionFailedException("The PDF contains no pages.");
            }

            List<Path> convertedImages = renderPdfPagesAsImages(request, document, pdfFile);

            for (Path file : convertedImages) {
                try {
                    totalSize += Files.size(file);
                } catch (IOException e) {
                    throw new ConversionFailedException("Error getting file size: " + file.getFileName());
                }
            }

            return new MultiFileConversionResponse(convertedImages, totalSize, true);
        }
    }

    private List<Path> renderPdfPagesAsImages(ConversionRequest request, PDDocument document, Path pdfFile) throws IOException {
        String baseName = getBaseName(pdfFile);
        String targetFormat = request.getTargetFormat();

        PDFRenderer renderer = new PDFRenderer(document);

        List<Path> imagePaths = new ArrayList<>();

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            BufferedImage image = renderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
            Path imagePath = request.getOutputDirectory().resolve(baseName + "_page" + (i + 1) + "." + targetFormat);
            ImageIO.write(image, targetFormat, imagePath.toFile());
            imagePaths.add(imagePath);
        }

        return imagePaths;
    }
}