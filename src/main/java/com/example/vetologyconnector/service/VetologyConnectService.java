package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.DicomChunkFileInfo;
import com.example.vetologyconnector.model.DicomFileInfo;
import java.util.List;

public interface VetologyConnectService {
  void sendAnalysisRequestToVetology(AnalysisRequest request);
  String getCaseSlotId();
  void sendContactInfo(ContactInfo contactInfo, String caseSlotId);
  String getTransferIdAfterSendingFileInfo(String caseSlotId, DicomFileInfo dicomFileInfo, int numberOfDicomFiles, int index);
  void sendChunkOfDicomFile(String transferId, List<DicomChunkFileInfo> chunkList);
}
