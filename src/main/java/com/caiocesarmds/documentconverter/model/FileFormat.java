package com.caiocesarmds.documentconverter.model;

public enum FileFormat {
    PDF("pdf"), JPG("jpg"), PNG("png"), DOCX("docx");

    private final String extension;

    FileFormat(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
