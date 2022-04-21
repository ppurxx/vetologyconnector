package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.ChunkFileInfo;
import com.example.vetologyconnector.model.FileInfo;
import java.util.List;

public interface VetologyConnectService {
  void sendAnalysisRequestToVetology(AnalysisRequest request);
  String getCaseSlotId();
  void sendContactInfo(ContactInfo contactInfo, String caseSlotId);
  String getTransferIdAfterSendingFileInfo(FileInfo fileInfo);
  void sendChunkListOfFile(FileInfo fileInfo, String transferId);
}
