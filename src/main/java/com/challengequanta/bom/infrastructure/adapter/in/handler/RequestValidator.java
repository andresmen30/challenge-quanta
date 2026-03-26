package com.challengequanta.bom.infrastructure.adapter.in.handler;

import com.challengequanta.bom.shared.exception.BadRequestException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T target) {
        Set<ConstraintViolation<T>> violations = validator.validate(target);
        if (violations.isEmpty()) {
            return Mono.just(target);
        }

        String message = violations.stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));

        return Mono.error(new BadRequestException(message));
    }
}
