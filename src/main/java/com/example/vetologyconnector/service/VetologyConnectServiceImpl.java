package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.ContactInfoRequest;
import com.example.vetologyconnector.model.DicomChunkFileInfo;
import com.example.vetologyconnector.model.DicomChunkFileInfoRequest;
import com.example.vetologyconnector.model.DicomFileInfo;
import com.example.vetologyconnector.model.DicomFileInfoRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class VetologyConnectServiceImpl implements VetologyConnectService{
  private final VetologyApiClient vetologyApiClient;

  @Override
  public void sendAnalysisRequestToVetology(AnalysisRequest request){
    log.info("sendAnalysisRequestToVetology 실행 {}",request.toJson());
    String caseSlotId = getCaseSlotId();
    sendContactInfo(new ContactInfo(request), caseSlotId);
    int numberOfDicomFiles = request.getDicomFileList().size();

    for(int i=1;i<=numberOfDicomFiles;i++){
      DicomFileInfo currentDicomFile = new DicomFileInfo(request.getDicomFileList().get(i-1));
      String transferId = getTransferIdAfterSendingFileInfo(caseSlotId, currentDicomFile, numberOfDicomFiles, i);

      sendChunkOfDicomFile(transferId, currentDicomFile.getChunkList());
      currentDicomFile.getFile().delete();
    }
  }

  @Override
  public String getCaseSlotId(){
    String caseSlotId = vetologyApiClient.callNewCaseSlot();
    log.info("getCaseSlotId request 진행 {}",caseSlotId);
    return caseSlotId;
  }

  @Override
  public void sendContactInfo(ContactInfo contactInfo, String caseSlotId){
    log.info("sendContactInfo 실행 {}, {}",contactInfo.toJson(), caseSlotId);
    ContactInfoRequest request = contactInfo.convertToRequest(caseSlotId);
    log.info("sendContactInfo request 진행 {}",request.toJson());
    vetologyApiClient.callContactInformation(request);
  }

  @Override
  public String getTransferIdAfterSendingFileInfo(String caseSlotId, DicomFileInfo dicomFileInfo, int numberOfDicomFiles, int index) {
    log.info("getTransferIdAfterSendingFileInfo 실행 {}, {}, {}, {}",caseSlotId, dicomFileInfo.toJson(), numberOfDicomFiles, index);
    DicomFileInfoRequest request = dicomFileInfo.convertToRequest(caseSlotId,numberOfDicomFiles,index);
    log.info("getTransferIdAfterSendingFileInfo request 진행 {}",request.toJson());
    return vetologyApiClient.callNewFile(request);
  }

  @Override
  public void sendChunkOfDicomFile(String transferId, List<DicomChunkFileInfo> chunkList) {
    log.info("sendChunkOfDicomFile 실행 {}, {}",transferId, chunkList);
    for(int i=1;i<=chunkList.size();i++){
      DicomChunkFileInfoRequest chunkRequest = chunkList.get(i-1).convertToRequest(i,transferId);
      log.info("sendChunkOfDicomFile request 진행 {}",chunkRequest.toJson());
      vetologyApiClient.callUploadChunks(chunkRequest);
      chunkRequest.getChunk().delete();
    }
  }
}
