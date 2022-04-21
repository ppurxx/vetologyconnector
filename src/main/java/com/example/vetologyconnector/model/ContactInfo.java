package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContactInfo  implements Jsonable{
  @JsonProperty
  String subclinicId;
  @JsonProperty
  String subclinicName;
  @JsonProperty
  String subclinicEmail;

  @JsonProperty
  String patientFirstname;
  @JsonProperty
  String patientLastname;

  @JsonProperty
  String caseSlotId;

  public ContactInfo(AnalysisRequest request, String caseSlotId){
    this.subclinicId = request.getSubClinicId();
    this.subclinicEmail = request.getSubClinicEmail();
    this.subclinicName = request.getSubClinicName();
    this.patientFirstname = request.getPatientFirstName();
    this.patientLastname = request.getPatientLastName();
    this.caseSlotId = caseSlotId;
  }
}
