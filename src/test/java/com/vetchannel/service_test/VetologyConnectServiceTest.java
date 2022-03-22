package com.vetchannel.service_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.vetologyconnector.model.DicomFileInfo;
import com.example.vetologyconnector.service.VetologyApiClient;
import com.example.vetologyconnector.service.VetologyConnectService;
import java.io.File;
import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;


@Ignore
@SpringBootTest(classes =com.example.vetologyconnector.VetologyconnectorApplication.class )
@ActiveProfiles("stage")
//@EnableAutoConfiguration
@ComponentScan(basePackages = "com.vetchannel")
public class VetologyConnectServiceTest {
  @Autowired
  VetologyApiClient client;
  @Autowired
  VetologyConnectService vetologyConnectService;
  @Test
  @DisplayName("case slot id 전달받는 테스트")
  public void testSplitFileToChunk(){
    File f= new File("src/test/java/com/vetchannel/service_test/sample.DCM");
    DicomFileInfo dicomFileInfo = new DicomFileInfo(f);

    assertThat(vetologyConnectService.getCaseSlotId()).isNotEmpty();
  }


  /*
  File dicomFile1;
  File dicomFile2;
  File dicomFile3;
  File dicomFile4;
  File dicomFile5;
  String subClinicId;
  String subClinicName;
  String subClinicEmail;
  String patientEmail;
  String patientPhone;
  String patientFirstname;
  String patientLastname;
   */
  @Test
  @DisplayName("dicom file 분석 vetology call 통합 테스트")
  public void vetologyServiceCallTest(){
    File f= new File("src/test/java/com/vetchannel/service_test/sample.DCM");

//    vetologyConnectService.sendAnalysisRequestToVetology(
//        new AnalysisRequest( "11", "test", "ppurxx@gmail.com",
//            "ppurxx@gmail.com","sanghyun","kim"));

  }


}
