package com.challengequanta.bom.infrastructure.adapter.in.handler;

import com.challengequanta.bom.shared.exception.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class RequestParamExtractor {

    public Long positiveLongPathVariable(ServerRequest request, String variableName) {
        String rawValue = request.pathVariable(variableName);
        return parsePositiveLong(rawValue, variableName);
    }

    public Integer positiveIntegerQueryParam(ServerRequest request, String paramName) {
        String rawValue = request.queryParam(paramName)
                .orElseThrow(() -> new BadRequestException(paramName + " is required"));
        return parsePositiveInt(rawValue, paramName);
    }

    public Long positiveLongQueryParam(ServerRequest request, String paramName) {
        String rawValue = request.queryParam(paramName)
                .orElseThrow(() -> new BadRequestException(paramName + " is required"));
        return parsePositiveLong(rawValue, paramName);
    }

    private Long parsePositiveLong(String rawValue, String fieldName) {
        try {
            long value = Long.parseLong(rawValue);
            if (value <= 0) {
                throw new BadRequestException(fieldName + " must be greater than 0");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new BadRequestException(fieldName + " must be a valid number");
        }
    }

    private Integer parsePositiveInt(String rawValue, String fieldName) {
        try {
            int value = Integer.parseInt(rawValue);
            if (value <= 0) {
                throw new BadRequestException(fieldName + " must be greater than 0");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new BadRequestException(fieldName + " must be a valid number");
        }
    }
}
