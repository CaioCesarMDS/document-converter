package com.caiocesarmds.documentconverter.model.response;

public interface ConversionResponse {
    boolean isSuccess();
    long getTotalSize();
    String getUserMessage();
}