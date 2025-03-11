package com.musinsa.point.error.code;

import org.springframework.http.HttpStatus;

public interface ResponseEnumType{

    String getCode();

    HttpStatus getStatus();

    String getMessage();

}
