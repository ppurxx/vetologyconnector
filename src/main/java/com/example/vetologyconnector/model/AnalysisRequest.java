package com.example.vetologyconnector.model;

import com.example.vetologyconnector.exception.VetologyConnectException;
import com.example.vetologyconnector.service.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AnalysisRequest implements Jsonable{
//  @JsonIgnore
//  File dicomFile1;
//  @JsonIgnore
//  File dicomFile2;
//  @JsonIgnore
//  File dicomFile3;
//  @JsonIgnore
//  File dicomFile4;
//  @JsonIgnore
//  File dicomFile5;

  @NotBlank(message = "병원 id는 필수값입니다.")
  @JsonProperty
  String subClinicId;

  @NotBlank(message = "병원 이름은 필수값입니다.")
  @JsonProperty
  String subClinicName;

  @NotBlank(message = "병원 email은 필수값입니다.")
  @Email
  @JsonProperty
  String subClinicEmail;

  @NotBlank(message = "환자 email은 필수값입니다.")
  @Email
  @JsonProperty
  String patientEmail;

  @NotBlank(message = "환자의 성은 필수값입니다.")
  @JsonProperty
  String patientFirstName;

  @NotBlank(message = "환자의 이름은 필수값입니다.")
  @JsonProperty
  String patientLastName;

  @JsonIgnore
  List<File> dicomFileList;

  public AnalysisRequest(String subClinicId, String subClinicName, String subClinicEmail,
      String patientEmail, String patientFirstName, String patientLastName) {
    this.subClinicId = subClinicId;
    this.subClinicName = subClinicName;
    this.subClinicEmail = subClinicEmail;
    this.patientEmail = patientEmail;
    this.patientFirstName = patientFirstName;
    this.patientLastName = patientLastName;
    this.dicomFileList = new ArrayList<>();

  }

  public void initDicomFileList(){
    for (File file : this.dicomFileList) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

      if (dicomFileList.isEmpty()) {
        throw new VetologyConnectException(AnalysisResponseCode.EMPTY_FILES);
      }
  }

  public List<File> getDicomFileList(){
    return dicomFileList;
  }

  public BiConsumer<MultipartFile, List<File>> addIfExists = (file, fileList)->{
    if(file!=null){
      fileList.add(Utils.toFileFromMultipartFile(file));
    }
  };
}