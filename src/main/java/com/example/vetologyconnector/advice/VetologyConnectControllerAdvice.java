package com.example.vetologyconnector.advice;

import com.example.vetologyconnector.exception.VetologyConnectException;
import com.example.vetologyconnector.model.AnalysisResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VetologyConnectControllerAdvice {
  @ExceptionHandler(VetologyConnectException.class)
  public AnalysisResponse handleConnectionException(VetologyConnectException e){
    e.printStackTrace();
    return AnalysisResponse.builder()
        .status(e.getResponseCode().getHttpStatus().value())
        .message(e.getMessage())
        .build();
  }

}
