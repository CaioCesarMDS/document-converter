package com.caiocesarmds.documentconverter.model.response;

import java.nio.file.Path;

public class SingleFileConversionResponse implements ConversionResponse {
    private final Path file;
    private final long size;
    private final boolean success;

    public SingleFileConversionResponse(Path file, long size, boolean success) {
        this.file = file;
        this.size = size;
        this.success = success;
    }

    public Path getFile() {
        return file;
    }

    @Override
    public long getTotalSize() {
        return size;
    }

    @Override
    public String getUserMessage() {
        return String.format(
                """
                        File converted successfully!
                        File name: %s
                        Size: %.2f KB""",
                file.getFileName(),
                size / 1024.0
        );

    }

    @Override
    public boolean isSuccess() {
        return success;
    }
}