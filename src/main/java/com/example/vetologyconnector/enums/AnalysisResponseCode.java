package com.example.vetologyconnector.enums;

import org.springframework.http.HttpStatus;

public enum AnalysisResponseCode {
  OK("파일 전송이 완료되었습니다.\nDicom파일 분석은 최대 10분까지 소요됩니다. \n아래 입력한 email로 리포트가 전송될 예정입니다.",HttpStatus.OK),
  INVALID_REQUEST("잘못된 요청입니다.",HttpStatus.BAD_REQUEST),
  INTERNAL_ERROR("AI분석 도중 에러가 발생했습니다.\n잠시후 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
  EMPTY_FILES("파일이 입력되지 않았습니다.",HttpStatus.BAD_REQUEST),
  FILE_SPLITTING_ERROR("파일 분해 중 에러가 발생했습니다. 다시 시도해주세요.",HttpStatus.INTERNAL_SERVER_ERROR),
  CONTACT_INFO_API_CALL_ERROR("연락처정보를 전달할 수 없습니다. 다시 시도해주세요.",HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_INFO_TRANSFER_FAILED("파일 정보 전송 중 오류가 발생했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),
  FILE_TRANSFER_FAILED("파일 전송 중 오류가 발생했습니다. 다시 시도해주세요.",HttpStatus.INTERNAL_SERVER_ERROR);



  String message;
  HttpStatus httpStatus;

  AnalysisResponseCode(String message, HttpStatus httpStatus){
    this.message = message;
    this.httpStatus = httpStatus;
  }

  public String getMessage(){
    return this.message;
  }

  public HttpStatus getHttpStatus(){
    return this.httpStatus;
  }
}
