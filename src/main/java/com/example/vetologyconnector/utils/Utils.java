package com.example.vetologyconnector.utils;

import com.example.vetologyconnector.exception.VetologyConnectorException;
import com.example.vetologyconnector.enums.AnalysisResponseCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
public class Utils {
  public static ObjectMapper om = new ObjectMapper();

  public static String toJson(Object o){
    String result = "";
    try {
      result =  om.writeValueAsString(o);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static File generateFile(MultipartFile multipartFile){

    File file = new File(generateUniqueFileName(multipartFile.getOriginalFilename()));
    Path savePath = Paths.get(file.getName());
    try {
      multipartFile.transferTo(savePath);
    } catch (IOException e) {
      throw new VetologyConnectorException(AnalysisResponseCode.INTERNAL_ERROR);
    }
    return file;
  }

  private static String generateUniqueFileName(String original){
    String converted = UUID.randomUUID()+getFileExpansion(original);
    log.info("file {} is renamed to {}",original,converted);

    return converted;
  }

  public static String getFileExpansion(String fileName){
    String[] split = fileName.split("\\.");
    return "."+split[split.length-1].toLowerCase();
  }

  public static List<File> convertToFileList(List<MultipartFile> inputFileList){
    if(inputFileList==null)return Collections.emptyList();
    List<File> result = new ArrayList<>();
    inputFileList.forEach(
            file -> {
              result.add(Utils.generateFile(file));
            }
    );

    return result;
  }

}
