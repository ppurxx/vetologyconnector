package com.example.vetologyconnector.controller;

import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.AnalysisResponse;
import com.example.vetologyconnector.model.AnalysisResponseCode;
import com.example.vetologyconnector.service.VetologyConnectService;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VetologyConnectController {
  private final VetologyConnectService vetologyConnectService;


  @PostMapping(value = "/send-request")
  public AnalysisResponse sendRequestToVetology(
      @RequestParam(name="dicomFile1", required = false) MultipartFile dicomFile1,
      @RequestParam(value="dicomFile2", required = false) MultipartFile dicomFile2,
      @RequestParam(value="dicomFile3", required = false) MultipartFile dicomFile3,
      @RequestParam(value="dicomFile4", required = false) MultipartFile dicomFile4,
      @RequestParam(value="dicomFile5", required = false) MultipartFile dicomFile5,
      @Validated AnalysisRequest request, BindingResult bindingResult){

    request.addIfExists.accept(dicomFile1,request.getDicomFileList());
    request.addIfExists.accept(dicomFile2,request.getDicomFileList());
    request.addIfExists.accept(dicomFile3,request.getDicomFileList());
    request.addIfExists.accept(dicomFile4,request.getDicomFileList());
    request.addIfExists.accept(dicomFile5,request.getDicomFileList());
    request.initDicomFileList();

    if(bindingResult.hasErrors()){
      AnalysisResponse response = AnalysisResponse.builder()
          .status(400)
          .message(bindingResult.getAllErrors().stream().map(e->e.getDefaultMessage()).collect(Collectors.toList()).toString())
          .build();
      return response;
    }

    vetologyConnectService.sendAnalysisRequestToVetology(request);
    AnalysisResponse response = AnalysisResponse.builder()
        .status(200)
        .message(AnalysisResponseCode.OK.getMessage())
        .build();
    return response;
  }

}
