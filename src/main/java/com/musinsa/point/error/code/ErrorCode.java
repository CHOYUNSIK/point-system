package com.musinsa.point.error.code;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ResponseEnumType {

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD REQUEST"),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "VALIDATION ERRORS"),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL ERRORS");

    private final HttpStatus status;
    private final String message;

    @Override
    public String getCode() {
        return this.name();
    }

}
