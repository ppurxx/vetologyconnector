package com.example.vetologyconnector.model;

import com.example.vetologyconnector.enums.AnalysisResponseCode;
import com.example.vetologyconnector.exception.VetologyConnectException;
import com.example.vetologyconnector.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "병원 이메일을 양식이 옳바르지 않습니다.")
  @JsonProperty
  String subClinicEmail;

  @NotBlank(message = "환자 email은 필수값입니다.")
  @Pattern(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$", message = "환자 이메일을 양식이 옳바르지 않습니다.")
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

  public void initDicomFileList(MultipartFile dicomFile1, MultipartFile dicomFile2, MultipartFile dicomFile3, MultipartFile dicomFile4, MultipartFile dicomFile5){
    addIfExists.accept(dicomFile1,getDicomFileList());
    addIfExists.accept(dicomFile2,getDicomFileList());
    addIfExists.accept(dicomFile3,getDicomFileList());
    addIfExists.accept(dicomFile4,getDicomFileList());
    addIfExists.accept(dicomFile5,getDicomFileList());

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
