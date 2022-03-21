package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

  @Builder
  @Data
  public class ContactInfoRequest  implements Jsonable{
    @JsonProperty
    String case_slot_id;
    @JsonProperty
    String subclinic_id;
    @JsonProperty
    String subclinic_name;
    @JsonProperty
    String subclinic_email;
    @JsonProperty
    String patient_email;
    @JsonProperty
    String patient_firstname;
    @JsonProperty
    String patient_lastname;
  }