package com.caiocesarmds.documentconverter.service;

import com.caiocesarmds.documentconverter.exceptions.system.ConversionFailedException;
import com.caiocesarmds.documentconverter.exceptions.validation.InvalidFormatException;
import com.caiocesarmds.documentconverter.exceptions.validation.PathSelectionException;

import com.caiocesarmds.documentconverter.model.request.ConversionRequest;
import com.caiocesarmds.documentconverter.model.response.ConversionResponse;
import com.caiocesarmds.documentconverter.service.interfaces.DocumentConversionService;
import com.caiocesarmds.documentconverter.service.interfaces.SpecificConverter;

import java.io.IOException;
import java.util.List;

import static com.caiocesarmds.documentconverter.utils.ValidationUtils.*;

public class ConversionService implements DocumentConversionService {
    private final List<SpecificConverter> converters;

    public ConversionService(List<SpecificConverter> converters) {
        this.converters = converters;
    }

    @Override
    public ConversionResponse convert(ConversionRequest request) throws ConversionFailedException {

        try {
            validateInputs(request);

            return converters.stream()
                    .filter(converter -> converter.supports(request))
                    .findFirst()
                    .orElseThrow(() -> new ConversionFailedException("No suitable converter found for the given request."))
                    .convert(request);
        } catch (InvalidFormatException | PathSelectionException e) {
            throw new ConversionFailedException("Failed to convert file: " + e.getMessage());
        } catch (IOException e) {
            throw new ConversionFailedException();
        }
    }

    private void validateInputs(ConversionRequest request) throws InvalidFormatException, PathSelectionException {
        validateFile(request.getSelectedFile());
        validatePath(request.getOutputDirectory());
        validateFormat(request.getTargetFormat(), request.getFileExtension());
    }
}