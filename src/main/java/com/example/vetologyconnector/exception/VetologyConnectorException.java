package com.example.vetologyconnector.exception;

import com.example.vetologyconnector.enums.AnalysisResponseCode;

public class VetologyConnectorException extends RuntimeException{
  AnalysisResponseCode responseCode;

  public VetologyConnectorException(AnalysisResponseCode responseCode){
    super(responseCode.getMessage());
    this.responseCode = responseCode;
  }

  public AnalysisResponseCode getResponseCode(){
    return this.responseCode;
  }
}
