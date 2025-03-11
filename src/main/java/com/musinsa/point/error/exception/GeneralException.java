package com.musinsa.point.error.exception;


import com.musinsa.point.error.code.ErrorCode;
import com.musinsa.point.error.code.ResponseEnumType;

public class GeneralException extends AbstractGlobalException{
    public GeneralException(ResponseEnumType code) {
        super(code.getMessage(), code);
    }

    public GeneralException(String message, ResponseEnumType code) {
        super(message, code);
    }

    public GeneralException(String message, ErrorCode code, Throwable cause) {
        super(message, code, cause);
    }

    public GeneralException(ErrorCode code, Throwable cause) {
        super(code, cause);
    }
}
