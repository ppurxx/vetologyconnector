package com.example.vetologyconnector.service;

import com.example.vetologyconnector.exception.VetologyConnectException;
import com.example.vetologyconnector.model.AnalysisResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import org.json.simple.JSONObject;
import org.springframework.web.multipart.MultipartFile;

public class Utils {
  public static ObjectMapper om = new ObjectMapper();
  public static JSONObject toBodyPublisher(Object o) {
    Map<String, Object> data = om.convertValue(o,Map.class);
    return new JSONObject(data);
  }

  public static String toJson(Object o){
    String result = "";
    try {
      result =  om.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static File toFileFromMultipartFile(MultipartFile multipartFile){

    File file = new File(UUID.randomUUID().toString()+ "_" + multipartFile.getOriginalFilename());
    Path savePath = Paths.get(file.getName());
    try {
      multipartFile.transferTo(savePath);
    } catch (IOException e) {
      throw new VetologyConnectException(AnalysisResponseCode.INTERNAL_ERROR);
    }
    return file;
  }

}
