package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DicomChunkFileInfo  implements Jsonable{
  @JsonProperty
  File chunk;

  public DicomChunkFileInfoRequest convertToRequest(int chunkNumber, String transferId){

      return DicomChunkFileInfoRequest.builder()
          .chunk(this.chunk)
          .chunk_number(chunkNumber)
          .transfer_id(transferId)
          .build();

  }
}
