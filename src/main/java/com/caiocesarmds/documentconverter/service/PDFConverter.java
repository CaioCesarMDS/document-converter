package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.exceptions.ConversionFailedException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.ImageType;

public class PDFConverter {
    public static void toImage(File file, Path directory, String format) throws ConversionFailedException {
        if (!Files.exists(directory)) {
            throw new ConversionFailedException("Output directory does not exist");
        }

        try (PDDocument document = Loader.loadPDF(file)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 400, ImageType.RGB);

                String outputPath = directory.resolve(
                        String.format(file.getName() + "_%03d.%s", page + 1, format)
                ).toString();

                if (!ImageIO.write(bim, format, new File(outputPath))) {
                    throw new ConversionFailedException(
                            "Image format not available: " + format);
                }
            }
        } catch (IOException e) {
            throw new ConversionFailedException("Failed to convert PDF to image: " + e.getMessage());
        } catch (Exception e) {
            throw new ConversionFailedException("Unexpected error during conversion: " + e.getMessage());
        }
    }

    public static void toDocx(File file, Path directory) throws ConversionFailedException {
        try (PDDocument document = Loader.loadPDF(file)) {

        } catch (IOException e) {
            throw new ConversionFailedException("Failed to convert PDF to DOCX: " + e.getMessage());
        }
    }
}
