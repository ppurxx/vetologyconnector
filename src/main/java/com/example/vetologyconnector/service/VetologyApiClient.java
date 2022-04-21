package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.ChunkFileInfo;
import com.example.vetologyconnector.model.ContactInfo;
import com.example.vetologyconnector.model.FileInfo;

public interface VetologyApiClient {

  String callNewCaseSlot();
  void callContactInformation(ContactInfo contactInfo);
  String callNewFile(FileInfo fileInfo);
  void callUploadChunks(ChunkFileInfo chunkFileInfo);
}
