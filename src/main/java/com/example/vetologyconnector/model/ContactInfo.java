package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContactInfo  implements Jsonable{
//  "case_slot_id": "{{case_slot_id}}",
//      "subclinic_id": "11",
//      "subclinic_name": "Paresh Test Clinic",
//      "subclinic_email": "kparesh1234@gmail.com",
//      "patient_id": "01",
//      "patient_firstname": "first",
//      "patient_lastname": "last",
//      "patient_birthdate": "1614100586",
//      "patient_sex": "F",
//      "patient_species": "canine",
//      "patient_breed": "raton",
//      "patient_weight": "80",
//      "patient_weight_type": "lb",
//      "patient_email": "ppurxx@gmail.com",
//      "patient_phone": "9099099000",
//      "patient_cellphone": "9099090909",
//      "responsible_name": "Test Responsible"


  @JsonProperty
  String subClinicId;
  @JsonProperty
  String subClinicName;
  @JsonProperty
  String subClinicEmail;
  @JsonProperty
  String patientEmail;

  @JsonProperty
  String patientFirstname;
  @JsonProperty
  String patientLastname;

  public ContactInfo(AnalysisRequest request){
    this.subClinicId = request.getSubClinicId();
    this.subClinicEmail = request.getSubClinicEmail();
    this.subClinicName = request.getSubClinicName();
    this.patientEmail = request.getPatientEmail();
    this.patientFirstname = request.getPatientFirstName();
    this.patientLastname = request.getPatientLastName();
  }


  public ContactInfoRequest convertToRequest(String caseSlotId){
    return ContactInfoRequest.builder()
        .case_slot_id(caseSlotId)
        .subclinic_id(this.subClinicId)
        .subclinic_name(this.subClinicName)
        .subclinic_email(this.subClinicEmail)
        .patient_firstname(this.patientFirstname)
        .patient_lastname(this.patientLastname)
        .patient_email(this.patientEmail)
        .build();
  }
}
