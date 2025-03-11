package com.musinsa.point.error;

import static lombok.AccessLevel.PROTECTED;

import com.musinsa.point.error.code.ResponseEnumType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = PROTECTED)
public class ErrorResponse {

    private final ResponseEnumType errorCode;
    private final String message;

    public static ErrorResponse of(ResponseEnumType errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }


}

