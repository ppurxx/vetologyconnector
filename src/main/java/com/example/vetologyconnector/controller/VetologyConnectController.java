package com.example.vetologyconnector.controller;

import com.example.vetologyconnector.enums.AnalysisResponseCode;
import com.example.vetologyconnector.model.AnalysisRequest;
import com.example.vetologyconnector.model.AnalysisResponse;
import com.example.vetologyconnector.service.VetologyConnectService;

import java.util.stream.Collectors;
import javax.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class VetologyConnectController {
  private final VetologyConnectService vetologyConnectService;


  @PostMapping(value = "/send-request" , consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  public AnalysisResponse sendRequestToVetology(
          @Valid AnalysisRequest request,
          BindingResult bindingResult){

    if(bindingResult.hasErrors()){
      return  AnalysisResponse.builder()
          .status(400)
          .message(bindingResult.getAllErrors().stream().map(
              DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList()).toString())
          .build();
    }

    vetologyConnectService.sendAnalysisRequestToVetology(request);

    return AnalysisResponse.builder()
        .status(200)
        .message(AnalysisResponseCode.OK.getMessage())
        .build();
  }

}
