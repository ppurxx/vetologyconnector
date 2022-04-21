package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChunkFileInfo implements Jsonable{
  @JsonProperty
  File chunk;
  @JsonProperty
  int chunkNumber;
  @JsonProperty
  String transferId;

}
