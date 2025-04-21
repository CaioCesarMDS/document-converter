package com.caiocesarmds.documentconverter.model.response;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class MultiFileConversionResponse implements ConversionResponse {
    private final List<Path> files;
    private final long totalSize;
    private final boolean success;

    public MultiFileConversionResponse(List<Path> files, long totalSize, boolean success) {
        this.files = files;
        this.totalSize = totalSize;
        this.success = success;
    }

    public List<Path> getFiles() {
        return files;
    }

    @Override
    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public String getUserMessage() {
        String filesList = files.stream()
                .limit(5)
                .map(file -> "- " + file.getFileName())
                .collect(Collectors.joining("\n"));

        String extra = files.size() > 5
                ? String.format("\n... and %d more files", files.size() - 5)
                : "";

        return String.format("""
        Files converted successfully!
        Total files: %d
        Total size: %.2f KB

        Converted files:
        %s%s
        """, files.size(), totalSize / 1024.0, filesList, extra);
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

}