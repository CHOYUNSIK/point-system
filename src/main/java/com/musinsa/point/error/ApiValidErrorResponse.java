package com.musinsa.point.error;


import com.musinsa.point.error.code.ResponseEnumType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.validation.Errors;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class ApiValidErrorResponse extends ErrorResponse {

    private final Errors error;

    private ApiValidErrorResponse(Errors error, ResponseEnumType errorType) {
        super(errorType, errorType.getMessage());
        this.error = error;
    }

    public static ApiValidErrorResponse of(Errors error, ResponseEnumType errorType) {
        return new ApiValidErrorResponse(error, errorType);
    }


}
