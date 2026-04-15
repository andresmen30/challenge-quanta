package com.challengequanta.bom.infrastructure.adapter.in.handler;

import com.challengequanta.bom.infrastructure.adapter.in.dto.AddMaterialRequest;
import com.challengequanta.bom.infrastructure.adapter.in.dto.CreateProductRequest;
import com.challengequanta.bom.shared.exception.BadRequestException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidatorTest {

    private ValidatorFactory validatorFactory;
    private RequestValidator requestValidator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        requestValidator = new RequestValidator(validator);
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldPassValidationForValidCreateProductRequest() {
        CreateProductRequest request = new CreateProductRequest("Zapato");

        StepVerifier.create(requestValidator.validate(request))
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationForBlankProductName() {
        CreateProductRequest request = new CreateProductRequest("   ");

        StepVerifier.create(requestValidator.validate(request))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BadRequestException.class);
                    assertThat(error.getMessage()).isEqualTo("Product name is required");
                })
                .verify();
    }

    @Test
    void shouldPassValidationForValidAddMaterialRequest() {
        AddMaterialRequest request = new AddMaterialRequest("Cuero", 2);

        StepVerifier.create(requestValidator.validate(request))
                .expectNext(request)
                .verifyComplete();
    }

    @Test
    void shouldFailValidationWhenQuantityIsNotPositive() {
        AddMaterialRequest request = new AddMaterialRequest("Cuero", 0);

        StepVerifier.create(requestValidator.validate(request))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BadRequestException.class);
                    assertThat(error.getMessage()).isEqualTo("quantity must be greater than 0");
                })
                .verify();
    }

    @Test
    void shouldConcatenateMessagesForMultipleViolationsSortedByPropertyPath() {
        AddMaterialRequest request = new AddMaterialRequest("", null);

        StepVerifier.create(requestValidator.validate(request))
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(BadRequestException.class);
                    assertThat(error.getMessage()).isEqualTo("Material is required, Quantity is required");
                })
                .verify();
    }
}
