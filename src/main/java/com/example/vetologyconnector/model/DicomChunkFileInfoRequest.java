package com.example.vetologyconnector.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.File;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DicomChunkFileInfoRequest  implements Jsonable{
    @JsonProperty
    File chunk;
    @JsonProperty
    int chunk_number;
    @JsonProperty
    String transfer_id;


}
