package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DicomFileInfoRequest  implements Jsonable{
//  "case_slot_id":"{{case_slot_id}}",
//  "total_dicoms": 1,
//  "dicom_index": 1,
//  "total_chunks": 1,
//  "file_checksum":"12345",
//  "file_name":"PareshTest_22.dcm"

  @JsonProperty
  String case_slot_id;
  @JsonProperty
  int total_dicoms;
  @JsonProperty
  int dicom_index;
  @JsonProperty
  int total_chunks;
  @JsonProperty
  String file_checksum;
  @JsonProperty
  String file_name;
}
