package com.example.vetologyconnector.exception;

import com.example.vetologyconnector.model.AnalysisResponseCode;

public class VetologyConnectException extends RuntimeException{
  AnalysisResponseCode responseCode;

  public VetologyConnectException(AnalysisResponseCode responseCode){
    super(responseCode.getMessage());
    this.responseCode = responseCode;
  }

  public AnalysisResponseCode getResponseCode(){
    return this.responseCode;
  }
}
