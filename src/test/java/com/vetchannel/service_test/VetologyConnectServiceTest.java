package com.vetchannel.service_test;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.FileInfo;
import com.example.vetologyconnector.service.VetologyApiClient;
import com.example.vetologyconnector.service.VetologyConnectService;
import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;


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

  @Test
  public void testCallContactInfo(){
    final String caseSlotId = "test-case-slot-id";
    vetologyConnectService.sendContactInfo(
            new ContactInfo(new AnalysisRequest(
                    "nick",
                    "nick",
                    "test_nick@gmail.com",
                    "nick",
                    "nick",
                    null), caseSlotId), caseSlotId);
  }

  @Test
  public void testCallNewFile(){
    final String caseSlotId = "test-case-slot-id";
    FileInfo fileInfo = new FileInfo(new File("src/test/java/com/vetchannel/service_test/sample.DCM"), caseSlotId, 1, 1);
    String transferId = vetologyConnectService.getTransferIdAfterSendingFileInfo(fileInfo);
    Assert.isTrue(StringUtils.isNotBlank(transferId), "success");
  }


}
