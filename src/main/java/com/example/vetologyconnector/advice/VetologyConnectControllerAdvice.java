package com.example.vetologyconnector.advice;

import com.example.vetologyconnector.exception.VetologyConnectorException;
import com.example.vetologyconnector.model.AnalysisResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VetologyConnectControllerAdvice {
  @ExceptionHandler(VetologyConnectorException.class)
  public AnalysisResponse handleVetologyConnectorException(VetologyConnectorException e){
    e.printStackTrace();
    return AnalysisResponse.builder()
        .status(e.getResponseCode().getHttpStatus().value())
        .message(e.getMessage())
        .build();
  }

  @ExceptionHandler(RuntimeException.class)
  public AnalysisResponse handleRuntimeException(RuntimeException e){
    e.printStackTrace();
    return AnalysisResponse.builder()
            .status(500)
            .message(e.getMessage())
            .build();
  }

}
