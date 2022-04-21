package com.example.vetologyconnector.model;

import com.example.vetologyconnector.enums.AnalysisResponseCode;
import com.example.vetologyconnector.exception.VetologyConnectorException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.DigestUtils;

@Getter
@Slf4j
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FileInfo implements Jsonable{
  public static final int SIZE_OF_CHUNK = 1048576; //1024 * 1024 = 1MB
  @JsonProperty
  File file;
  @JsonProperty
  String caseSlotId;
  @JsonProperty
  int totalDicoms;
  @JsonProperty
  int dicomIndex;
  @JsonProperty
  int totalChunks;
  @JsonProperty
  String fileChecksum;
  @JsonProperty
  String fileName;


  public List<ChunkFileInfo> splitFileToChunk(String transferId){
    int chunkNumber = 1;
    List<ChunkFileInfo> files = new ArrayList<>();

    byte[] buffer = new byte[SIZE_OF_CHUNK];

    try {
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(this.file));
      String name = this.file.getName().replaceAll("\\.","");

      int tmp = 0;
      while ((tmp = bis.read(buffer)) > 0) {
        File newFile = new File(this.file.getParent(), name + "_chunk_"+chunkNumber+".gz");
        try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(newFile))) {
          out.write(buffer, 0, tmp);
        }
        ChunkFileInfo newChunkFile = new ChunkFileInfo(newFile.getAbsoluteFile(), chunkNumber, transferId);
        chunkNumber++;
        files.add(newChunkFile);
      }
    }catch (Exception e){
      e.printStackTrace();
    }

    return files;
  }

  public String getFileChecksum(){
    try {
      InputStream is = new FileInputStream(this.getFile());
      return DigestUtils.md5DigestAsHex(is);
    } catch (IOException e) {
      e.printStackTrace();
      throw new VetologyConnectorException(AnalysisResponseCode.INTERNAL_ERROR);
    }
  }

  private int getNumberOfChunks(){
    var fileSize = this.file.length();
    var mod = fileSize % SIZE_OF_CHUNK;
    return (int) (mod==0?fileSize/(SIZE_OF_CHUNK):fileSize/(SIZE_OF_CHUNK)+1);
  }

  public FileInfo(File file, String caseSlotId, int numberOfFiles, int fileIndex){
    this.file = file;
    this.caseSlotId = caseSlotId;
    this.totalDicoms = numberOfFiles;
    this.dicomIndex = fileIndex;
    this.totalChunks = this.getNumberOfChunks();
    this.fileChecksum = this.getFileChecksum();
    this.fileName = this.getFile().getName();
  }

}
