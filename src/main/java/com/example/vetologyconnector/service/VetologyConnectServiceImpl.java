package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.ChunkFileInfo;
import com.example.vetologyconnector.model.FileInfo;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.example.vetologyconnector.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VetologyConnectServiceImpl implements VetologyConnectService{
  private final VetologyApiClient vetologyApiClient;

  @Override
  public void sendAnalysisRequestToVetology(AnalysisRequest request){
    String caseSlotId = getCaseSlotId();
    sendContactInfo(new ContactInfo(request, caseSlotId), caseSlotId);
    List<File> fileList = Utils.convertToFileList(request.getInputFileList());
    int numberOfFiles = fileList.size();
    AtomicInteger i = new AtomicInteger(1);

    List<CompletableFuture<Void>> futureList = fileList.stream().map(
        file -> CompletableFuture.runAsync(()->{
          FileInfo currentFile = new FileInfo(file, caseSlotId, numberOfFiles, i.getAndIncrement());
          String transferId = getTransferIdAfterSendingFileInfo(currentFile);
          sendChunkListOfFile(currentFile, transferId);
        })
    ).collect(Collectors.toList());

    futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());

    fileList.forEach(File::delete);
  }

  @Override
  public String getCaseSlotId(){
    return vetologyApiClient.callNewCaseSlot();
  }

  @Override
  public void sendContactInfo(ContactInfo contactInfo, String caseSlotId){
    vetologyApiClient.callContactInformation(contactInfo);
  }

  @Override
  public String getTransferIdAfterSendingFileInfo(FileInfo fileInfo) {
    return vetologyApiClient.callNewFile(fileInfo);
  }

  @Override
  public void sendChunkListOfFile(FileInfo currentFile, String transferId) {
    List<ChunkFileInfo> chunkList = currentFile.splitFileToChunk(transferId);
    AtomicInteger i = new AtomicInteger(1);
    List<CompletableFuture<Void>> futureList = chunkList.stream().map(
        chunk -> CompletableFuture.runAsync(()->{
          vetologyApiClient.callUploadChunks(chunk);
        })
    ).collect(Collectors.toList());

    futureList.stream().map(CompletableFuture::join).collect(Collectors.toList());
    chunkList.forEach(chunk->chunk.getChunk().delete());
  }
}
