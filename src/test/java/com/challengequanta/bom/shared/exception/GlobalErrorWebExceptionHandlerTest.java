package com.challengequanta.bom.shared.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ResponseStatusException;
import reactor.test.StepVerifier;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalErrorWebExceptionHandlerTest {

    private GlobalErrorWebExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalErrorWebExceptionHandler();
    }

    @Test
    void shouldWriteBadRequestForBadRequestException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange, new BadRequestException("invalid input")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exchange.getResponse().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        String body = readBody(exchange);
        assertThat(body).contains("\"status\":400");
        assertThat(body).contains("\"message\":\"invalid input\"");
        assertThat(body).contains("\"timestamp\":");
    }

    @Test
    void shouldWriteNotFoundForNotFoundException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange, new NotFoundException("missing")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(readBody(exchange)).contains("\"status\":404");
    }

    @Test
    void shouldWriteConflictForConflictException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange, new ConflictException("duplicate")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(readBody(exchange)).contains("\"status\":409");
    }

    @Test
    void shouldMapResponseStatusExceptionToItsStatus() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange,
                        new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "nope")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(readBody(exchange)).contains("\"status\":415");
    }

    @Test
    void shouldMapDecodingExceptionToBadRequest() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange, new DecodingException("bad json")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(readBody(exchange)).contains("\"status\":400");
        assertThat(readBody(exchange)).contains("\"message\":\"bad json\"");
    }

    @Test
    void shouldHideInternalMessageForUnknownException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange, new RuntimeException("secret stack info")))
                .verifyComplete();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        String body = readBody(exchange);
        assertThat(body).contains("\"status\":500");
        assertThat(body).contains("\"message\":\"Internal server error\"");
        assertThat(body).doesNotContain("secret stack info");
    }

    @Test
    void shouldFallBackToInvalidRequestWhenMessageIsBlank() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange, new BadRequestException("")))
                .verifyComplete();

        assertThat(readBody(exchange)).contains("\"message\":\"Invalid request\"");
    }

    @Test
    void shouldEscapeSpecialCharactersInMessage() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

        StepVerifier.create(handler.handle(exchange,
                        new BadRequestException("bad \"payload\" with \\ slash")))
                .verifyComplete();

        assertThat(readBody(exchange))
                .contains("\"message\":\"bad \\\"payload\\\" with \\\\ slash\"");
    }

    @Test
    void shouldPropagateErrorWhenResponseAlreadyCommitted() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        exchange.getResponse().setComplete().block();
        RuntimeException cause = new RuntimeException("already committed");

        StepVerifier.create(handler.handle(exchange, cause))
                .expectErrorMatches(error -> error == cause)
                .verify();
    }

    private String readBody(MockServerWebExchange exchange) {
        DataBuffer buffer = exchange.getResponse().getBodyAsString()
                .map(value -> exchange.getResponse().bufferFactory().wrap(value.getBytes(StandardCharsets.UTF_8)))
                .block();
        if (buffer == null) {
            return "";
        }
        byte[] bytes = new byte[buffer.readableByteCount()];
        buffer.read(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
