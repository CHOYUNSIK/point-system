package com.musinsa.point.error.exception;


import com.musinsa.point.error.code.ResponseEnumType;

public class GeneralException extends AbstractGlobalException{
    public GeneralException(ResponseEnumType code) {
        super(code, code.getMessage());
    }

    public GeneralException(ResponseEnumType code, String message) {
        super(code, message);
    }

    public GeneralException(ResponseEnumType code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public GeneralException(ResponseEnumType code, Throwable cause) {
        super(code, cause);
    }
}
