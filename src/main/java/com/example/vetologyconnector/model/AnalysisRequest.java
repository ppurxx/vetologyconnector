package com.example.vetologyconnector.model;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AnalysisRequest implements Jsonable{

  @NotBlank(message = "병원 id는 필수값입니다.")
  String subclinicId;

  @NotBlank(message = "병원 이름은 필수값입니다.")
  String subclinicName;

  @NotBlank(message = "병원 email은 필수값입니다.")
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "병원 이메일을 양식이 옳바르지 않습니다.")
  String subclinicEmail;

  @NotBlank(message = "환자의 이름은 필수값입니다.")
  String patientFirstName;

  @NotBlank(message = "보호자 이름은 필수값입니다.") //vetology사에서는 환자의 성으로 사용하나, gbiotech에서는 보호자이름으로 입력받도록 요청받음.
  String patientLastName;

  @NotEmpty(message = "분석 대상 파일은 1개이상 등록되어야 합니다.")
  List<MultipartFile> inputFileList;

  // @FormDataParam and @FormData is not working. the solution I choose was Constructor with parameter names.
  public AnalysisRequest(String subclinic_id, String subclinic_name, String subclinic_email, String patient_first_name, String patient_last_name, List<MultipartFile> input_file_list) {
    this.subclinicId = subclinic_id;
    this.subclinicName = subclinic_name;
    this.subclinicEmail = subclinic_email;
    this.patientFirstName = patient_first_name;
    this.patientLastName = patient_last_name;
    this.inputFileList = input_file_list;
  }
}
