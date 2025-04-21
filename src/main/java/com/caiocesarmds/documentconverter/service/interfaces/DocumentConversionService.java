package com.caiocesarmds.documentconverter.service.interfaces;

import com.caiocesarmds.documentconverter.exceptions.system.ConversionFailedException;

import com.caiocesarmds.documentconverter.model.request.ConversionRequest;
import com.caiocesarmds.documentconverter.model.response.ConversionResponse;

public interface DocumentConversionService {
    ConversionResponse convert(ConversionRequest request) throws ConversionFailedException;
}