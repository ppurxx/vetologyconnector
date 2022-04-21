package com.example.vetologyconnector.model;

import com.example.vetologyconnector.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AnalysisRequest implements Jsonable{

  @NotBlank(message = "병원 id는 필수값입니다.")
  String subClinicId;

  @NotBlank(message = "병원 이름은 필수값입니다.")
  String subClinicName;

  @NotBlank(message = "병원 email은 필수값입니다.")
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "병원 이메일을 양식이 옳바르지 않습니다.")
  String subClinicEmail;

  @NotBlank(message = "환자의 이름은 필수값입니다.")
  String patientFirstName;

  @NotBlank(message = "환자의 성은 필수값입니다.")
  String patientLastName;

  @NotEmpty(message = "분석 대상 파일은 1개이상 등록되어야 합니다.")
  List<MultipartFile> inputFileList;

  public AnalysisRequest(String subClinicId, String subClinicName, String subClinicEmail, String patientFirstName, String patientLastName, List<MultipartFile> inputFileList) {
    this.subClinicId = subClinicId;
    this.subClinicName = subClinicName;
    this.subClinicEmail = subClinicEmail;
    this.patientFirstName = patientFirstName;
    this.patientLastName = patientLastName;
    this.inputFileList = inputFileList;
  }
}
