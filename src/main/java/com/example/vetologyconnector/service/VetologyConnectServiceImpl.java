package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.ContactInfoRequest;
import com.example.vetologyconnector.model.DicomChunkFileInfo;
import com.example.vetologyconnector.model.DicomChunkFileInfoRequest;
import com.example.vetologyconnector.model.DicomFileInfo;
import com.example.vetologyconnector.model.DicomFileInfoRequest;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VetologyConnectServiceImpl implements VetologyConnectService{
  private final VetologyApiClient vetologyApiClient;

  @Override
  public void sendAnalysisRequestToVetology(AnalysisRequest request){
    String caseSlotId = getCaseSlotId();
    sendContactInfo(new ContactInfo(request), caseSlotId);
    int numberOfDicomFiles = request.getDicomFileList().size();

    AtomicInteger i = new AtomicInteger(1);

    List<CompletableFuture<Void>> futureList = request.getDicomFileList().stream().map(
        dicomFile -> CompletableFuture.runAsync(()->{
          DicomFileInfo currentDicomFile = new DicomFileInfo(request.getDicomFileList().get(i.get()-1));
          String transferId = getTransferIdAfterSendingFileInfo(caseSlotId, currentDicomFile, numberOfDicomFiles, i.getAndIncrement());

          sendChunkOfDicomFile(transferId, currentDicomFile.getChunkList());
        })
    ).collect(Collectors.toList());

    futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());

    request.getDicomFileList().forEach(File::delete);
  }

  @Override
  public String getCaseSlotId(){
    String caseSlotId = vetologyApiClient.callNewCaseSlot();
    return caseSlotId;
  }

  @Override
  public void sendContactInfo(ContactInfo contactInfo, String caseSlotId){
    ContactInfoRequest request = contactInfo.convertToRequest(caseSlotId);
    vetologyApiClient.callContactInformation(request);
  }

  @Override
  public String getTransferIdAfterSendingFileInfo(String caseSlotId, DicomFileInfo dicomFileInfo, int numberOfDicomFiles, int index) {
    DicomFileInfoRequest request = dicomFileInfo.convertToRequest(caseSlotId,numberOfDicomFiles,index);
    return vetologyApiClient.callNewFile(request);
  }

  @Override
  public void sendChunkOfDicomFile(String transferId, List<DicomChunkFileInfo> chunkList) {
    AtomicInteger i = new AtomicInteger(1);
    List<CompletableFuture<Void>> futureList = chunkList.stream().map(
        chunk -> CompletableFuture.runAsync(()->{
          DicomChunkFileInfoRequest chunkRequest = chunkList.get(i.get()-1).convertToRequest(i.getAndIncrement(),transferId);
          vetologyApiClient.callUploadChunks(chunkRequest);
        })
    ).collect(Collectors.toList());

    futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
    chunkList.forEach(chunk->chunk.getChunk().delete());
  }
}
