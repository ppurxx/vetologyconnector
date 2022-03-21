package com.example.vetologyconnector.service;


import com.example.vetologyconnector.exception.VetologyConnectException;
import com.example.vetologyconnector.model.AnalysisResponseCode;
import com.example.vetologyconnector.model.ContactInfoRequest;
import com.example.vetologyconnector.model.DicomChunkFileInfoRequest;
import com.example.vetologyconnector.model.DicomFileInfoRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VetologyApiClientImpl implements VetologyApiClient{
  public static final String POST = "POST";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";
  public static final String CONTENT_TYPE = "Content-Type";
  public static final String APPLICATION_JSON = "application/json";
  public static final String CASE_SLOT_ID = "case_slot_id";
  public static final String TRANSFER_ID = "transfer_id";
  public static final String CHUNK = "chunk";
  public static final String APPLICATION_OCTET_STREAM = "application/octet-stream";
  public static final String CHUNK_NUMBER = "chunk_number";
  public static final String MULTIPART_FORM_DATA = "multipart/form-data";

  private final static HttpClient client = HttpClient.newHttpClient();

  @Value("${vetology.url.base}")
  String baseUrl;

  @Value("${vetology.url.new-case-slot}")
  String newCaseSlotUrl;

  @Value("${vetology.url.contact-information}")
  String contactInformationUrl;

  @Value("${vetology.url.new-file}")
  String newFileUrl;

  @Value("${vetology.url.upload-chunk}")
  String uploadChunkUrl;

  @Value("${vetology.license}")
  String license;


  @Override
  public String callNewCaseSlot(){
    String result = "";  //get메소드로 body값의 문자를 확인
    try {
      result = client.sendAsync(
          HttpRequest.newBuilder(
              new URI(baseUrl + newCaseSlotUrl))
              .GET()
              .header(AUTHORIZATION, BEARER + license)
              .build(),  //GET방식 요청
          HttpResponse.BodyHandlers.ofString())  //응답은 문자형태
          .thenApply(HttpResponse::body)  //thenApply메소드로 응답body값만 받기
          .get();

      JSONParser parser = new JSONParser();
      JSONObject obj = (JSONObject)parser.parse(result);
      result = obj.get(CASE_SLOT_ID).toString();

    } catch (InterruptedException | ExecutionException |  URISyntaxException | ParseException e) {
      throw new VetologyConnectException(AnalysisResponseCode.INTERNAL_ERROR);
    }

    return result;
  }

  @Override
  public void callContactInformation(ContactInfoRequest contactInfoRequest) {
    HttpRequest request = HttpRequest.newBuilder()
        .POST(BodyPublishers.ofString(Utils.toBodyPublisher(contactInfoRequest).toJSONString()))
        .uri(URI.create(baseUrl+contactInformationUrl))
        .setHeader(CONTENT_TYPE, APPLICATION_JSON) // add request header
        .header(AUTHORIZATION, BEARER +license)
        .build();

    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if(response.statusCode()!= HttpStatus.OK.value()){
        throw new VetologyConnectException(AnalysisResponseCode.CONTACT_INFO_API_CALL_ERROR);
      }
    } catch (IOException | InterruptedException e) {
      throw new VetologyConnectException(AnalysisResponseCode.CONTACT_INFO_API_CALL_ERROR);
    }
  }

  @Override
  public String callNewFile(DicomFileInfoRequest dicomFileInfoRequest) {
    HttpRequest request = HttpRequest.newBuilder()
        .POST(BodyPublishers.ofString(Utils.toBodyPublisher(dicomFileInfoRequest).toJSONString()))
        .uri(URI.create(baseUrl+newFileUrl))
        .setHeader(CONTENT_TYPE, APPLICATION_JSON) // add request header
        .header(AUTHORIZATION, BEARER +license)
        .build();

    String result="";
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if(response.statusCode()!= HttpStatus.OK.value() || response.body().isEmpty()){
        throw new VetologyConnectException(AnalysisResponseCode.FILE_INFO_TRANSFER_FAILED);
      }
      result = response.body();
      JSONParser parser = new JSONParser();
      JSONObject obj = (JSONObject)parser.parse(result);
      result = obj.get(TRANSFER_ID).toString();
    } catch (IOException | InterruptedException | ParseException e) {
      throw new VetologyConnectException(AnalysisResponseCode.FILE_INFO_TRANSFER_FAILED);
    }

    return result;
  }

  //upload-chunk api는 필드에 media-type을 지정해줘야 하는데, 다른 api들은 이런 처리가 필요가 없어서 비교적 간단한 구조의 java11.httpClient를 사용했고,
  //upload-chunk는 위의 목적때문에 okhttp3 library를 사용했음.
  @Override
  public void callUploadChunks(DicomChunkFileInfoRequest dicomChunkFileInfoRequest) {
    OkHttpClient client = new OkHttpClient().newBuilder()
        .build();

    RequestBody body = new MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(CHUNK,dicomChunkFileInfoRequest.getChunk().getName(),
            RequestBody.create(MediaType.parse(APPLICATION_OCTET_STREAM), dicomChunkFileInfoRequest.getChunk()))
        .addFormDataPart(CHUNK_NUMBER,dicomChunkFileInfoRequest.getChunk_number()+"")
        .addFormDataPart(TRANSFER_ID,dicomChunkFileInfoRequest.getTransfer_id())
        .build();

    Request request = new Request.Builder()
        .url(baseUrl+uploadChunkUrl)
        .method(POST, body)
        .addHeader(AUTHORIZATION, BEARER +license)
        .addHeader(CONTENT_TYPE, MULTIPART_FORM_DATA)
        .build();

    try {
      Response response = client.newCall(request).execute();
      if(response.code()!= HttpStatus.OK.value()){
        throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
      }
    } catch (IOException e) {
      throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
    }
  }

}
