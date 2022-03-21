package com.example.vetologyconnector.service;

import com.example.vetologyconnector.model.ContactInfoRequest;
import com.example.vetologyconnector.model.DicomChunkFileInfoRequest;
import com.example.vetologyconnector.model.DicomFileInfoRequest;

public interface VetologyApiClient {

  String callNewCaseSlot();
  void callContactInformation(ContactInfoRequest contactInfoRequest);
  String callNewFile(DicomFileInfoRequest dicomFileInfoRequest);
  void callUploadChunks(DicomChunkFileInfoRequest dicomChunkFileInfoRequest);
}
