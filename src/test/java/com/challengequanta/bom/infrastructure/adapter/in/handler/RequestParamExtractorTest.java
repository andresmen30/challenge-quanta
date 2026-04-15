package com.challengequanta.bom.infrastructure.adapter.in.handler;

import com.challengequanta.bom.shared.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestParamExtractorTest {

    private RequestParamExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new RequestParamExtractor();
    }

    @Test
    void shouldParsePositiveLongPathVariable() {
        ServerRequest request = requestWithPathVariable("productId", "42");

        Long result = extractor.positiveLongPathVariable(request, "productId");

        assertThat(result).isEqualTo(42L);
    }

    @Test
    void shouldFailWhenPathVariableIsNotANumber() {
        ServerRequest request = requestWithPathVariable("productId", "abc");

        assertThatThrownBy(() -> extractor.positiveLongPathVariable(request, "productId"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("productId must be a valid number");
    }

    @Test
    void shouldFailWhenPathVariableIsZero() {
        ServerRequest request = requestWithPathVariable("productId", "0");

        assertThatThrownBy(() -> extractor.positiveLongPathVariable(request, "productId"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("productId must be greater than 0");
    }

    @Test
    void shouldFailWhenPathVariableIsNegative() {
        ServerRequest request = requestWithPathVariable("productId", "-5");

        assertThatThrownBy(() -> extractor.positiveLongPathVariable(request, "productId"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("productId must be greater than 0");
    }

    @Test
    void shouldParsePositiveIntegerQueryParam() {
        ServerRequest request = requestWithQueryParam("quantity", "5");

        Integer result = extractor.positiveIntegerQueryParam(request, "quantity");

        assertThat(result).isEqualTo(5);
    }

    @Test
    void shouldFailWhenIntegerQueryParamIsMissing() {
        ServerRequest request = requestWithoutQueryParams();

        assertThatThrownBy(() -> extractor.positiveIntegerQueryParam(request, "quantity"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("quantity is required");
    }

    @Test
    void shouldFailWhenIntegerQueryParamIsNotANumber() {
        ServerRequest request = requestWithQueryParam("quantity", "abc");

        assertThatThrownBy(() -> extractor.positiveIntegerQueryParam(request, "quantity"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("quantity must be a valid number");
    }

    @Test
    void shouldFailWhenIntegerQueryParamIsZero() {
        ServerRequest request = requestWithQueryParam("quantity", "0");

        assertThatThrownBy(() -> extractor.positiveIntegerQueryParam(request, "quantity"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("quantity must be greater than 0");
    }

    @Test
    void shouldParsePositiveLongQueryParam() {
        ServerRequest request = requestWithQueryParam("productId", "77");

        Long result = extractor.positiveLongQueryParam(request, "productId");

        assertThat(result).isEqualTo(77L);
    }

    @Test
    void shouldFailWhenLongQueryParamIsMissing() {
        ServerRequest request = requestWithoutQueryParams();

        assertThatThrownBy(() -> extractor.positiveLongQueryParam(request, "productId"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("productId is required");
    }

    @Test
    void shouldFailWhenLongQueryParamIsNotANumber() {
        ServerRequest request = requestWithQueryParam("productId", "xyz");

        assertThatThrownBy(() -> extractor.positiveLongQueryParam(request, "productId"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("productId must be a valid number");
    }

    @Test
    void shouldFailWhenLongQueryParamIsNegative() {
        ServerRequest request = requestWithQueryParam("productId", "-1");

        assertThatThrownBy(() -> extractor.positiveLongQueryParam(request, "productId"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("productId must be greater than 0");
    }

    private ServerRequest requestWithPathVariable(String name, String value) {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        exchange.getAttributes().put(
                RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                Map.of(name, value)
        );
        return ServerRequest.create(exchange, Collections.emptyList());
    }

    private ServerRequest requestWithQueryParam(String name, String value) {
        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("/?" + name + "=" + value)
        );
        return ServerRequest.create(exchange, Collections.emptyList());
    }

    private ServerRequest requestWithoutQueryParams() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));
        return ServerRequest.create(exchange, Collections.emptyList());
    }
}
