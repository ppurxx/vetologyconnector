package com.example.vetologyconnector.model;

import com.example.vetologyconnector.exception.VetologyConnectException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

@Getter
@Slf4j
public class DicomFileInfo  implements Jsonable{
  @JsonProperty
  File file;
  @JsonIgnore
  List<DicomChunkFileInfo> chunkList;

  public List<DicomChunkFileInfo> splitFileToChunk() throws IOException {
      int counter = 1;
      List<DicomChunkFileInfo> files = new ArrayList<>();

      int sizeOfChunk = 1024 * 1024;
      byte[] buffer = new byte[sizeOfChunk];

      try {
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        String name = file.getName();

        int tmp = 0;
        while ((tmp = bis.read(buffer)) > 0) {
          File newFile = new File(file.getParent(), String.format("%03d", counter++) + name);
          DicomChunkFileInfo newChunkFile = new DicomChunkFileInfo(newFile);
          try (FileOutputStream out = new FileOutputStream(newFile)) {
            out.write(buffer, 0, tmp);
          }

          files.add(newChunkFile);
        }
      }catch (Exception e){
        e.printStackTrace();
      }

      return files;
  }

  public int getFileChecksum(){
    byte[] res = null;
    try {
      InputStream is = new FileInputStream(this.getFile());
      res = DigestUtils.md5Digest(is);
    } catch (IOException e) {
      e.printStackTrace();
      throw new VetologyConnectException(AnalysisResponseCode.INTERNAL_ERROR);
    }
    return ByteBuffer.wrap(res).getInt();
  }

  public int getNumberOfChunks(){
    return this.chunkList.size();
  }

  public DicomFileInfo(File file){
    this.file = file;
    try {
      this.chunkList = splitFileToChunk();
    } catch (IOException e) {
      throw new VetologyConnectException(AnalysisResponseCode.FILE_SPLITTING_ERROR);
    }
  }

  public DicomFileInfoRequest convertToRequest(String caseSlotId, int numberOfDicomFiles, int dicomIndex){
    return DicomFileInfoRequest.builder()
        .case_slot_id(caseSlotId)
        .total_dicoms(numberOfDicomFiles)
        .dicom_index(dicomIndex)
        .total_chunks(this.getNumberOfChunks())
        .file_checksum(this.getFileChecksum())
        .file_name(this.getFile().getName())
        .build();
  }
}
