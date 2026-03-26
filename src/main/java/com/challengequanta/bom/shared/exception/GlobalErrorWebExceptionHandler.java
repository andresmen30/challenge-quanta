package com.challengequanta.bom.shared.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalErrorWebExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = resolveStatus(ex);
        String message = resolveMessage(ex, status);
        ErrorResponse errorResponse = new ErrorResponse(message, status.value(), LocalDateTime.now());

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        byte[] payload = buildJson(errorResponse);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse().bufferFactory().wrap(payload)));
    }

    private HttpStatus resolveStatus(Throwable ex) {
        if (ex instanceof ApiException apiException) {
            return apiException.getStatus();
        }
        if (ex instanceof ResponseStatusException responseStatusException) {
            return HttpStatus.valueOf(responseStatusException.getStatusCode().value());
        }
        if (ex instanceof DecodingException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(Throwable ex, HttpStatus status) {
        if (status.is5xxServerError()) {
            return "Internal server error";
        }
        return ex.getMessage() == null || ex.getMessage().isBlank() ? "Invalid request" : ex.getMessage();
    }

    private byte[] buildJson(ErrorResponse errorResponse) {
        String json = "{\"message\":\"" + escapeJson(errorResponse.message()) + "\","
                + "\"status\":" + errorResponse.status() + ","
                + "\"timestamp\":\"" + errorResponse.timestamp() + "\"}";
        return json.getBytes(StandardCharsets.UTF_8);
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
