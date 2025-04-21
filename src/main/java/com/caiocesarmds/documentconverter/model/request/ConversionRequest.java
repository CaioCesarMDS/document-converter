package com.caiocesarmds.documentconverter.model.request;

import java.nio.file.Path;

public class ConversionRequest {
    private final Path selectedFile;
    private final Path outputDirectory;
    private final String targetFormat;
    private final String fileExtension;

    public ConversionRequest(Path selectedFile, Path outputDirectory, String targetFormat, String fileExtension) {
        this.selectedFile = selectedFile;
        this.outputDirectory = outputDirectory;
        this.targetFormat = targetFormat;
        this.fileExtension = fileExtension;
    }

    public Path getSelectedFile() {
        return selectedFile;
    }

    public Path getOutputDirectory() {
        return outputDirectory;
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public String getFileExtension() {
        return fileExtension;
    }
}