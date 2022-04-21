package com.example.vetologyconnector.service;


import com.example.vetologyconnector.exception.VetologyConnectorException;
import com.example.vetologyconnector.enums.AnalysisResponseCode;
import com.example.vetologyconnector.model.*;

import java.io.File;
import java.io.IOException;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class VetologyApiClientImpl implements VetologyApiClient {
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
    private final OkHttpClient client;
    public static final String APPLICATION_JSON_CHARSET_UTF_8 = "application/json; charset=utf-8";

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
    public String callNewCaseSlot() {
        String result = "";  //get메소드로 body값의 문자를 확인
        Request request = new Request.Builder()
                .addHeader("Authorization", BEARER + license)
                .url(baseUrl + newCaseSlotUrl)
                .build();
        logRequest(request);
        try {
            Response response = client.newCall(request)
                    .execute();
            String responseBody = response.body().string();
            logResponse(response,responseBody);

            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(responseBody);
            result = obj.get(CASE_SLOT_ID).toString();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new VetologyConnectorException(AnalysisResponseCode.INTERNAL_ERROR);
        }

        return result;
    }

    @Override
    public void callContactInformation(ContactInfo contactInfo) {
        Request request = new Request.Builder()
                .url(baseUrl + contactInformationUrl)
                .method(POST, RequestBody.create(MediaType.get(APPLICATION_JSON_CHARSET_UTF_8), contactInfo.toJson()))
                .addHeader(AUTHORIZATION, BEARER + license)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        logRequest(request, contactInfo);
        try {
            Response response = client.newCall(request).execute();
            logResponse(response, response.body().string());

            if (response.code() != HttpStatus.OK.value()) {
                throw new VetologyConnectorException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new VetologyConnectorException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
        }

    }

    @Override
    public String callNewFile(FileInfo fileInfo) {
        Request request = new Request.Builder()
                .url(baseUrl + newFileUrl)
                .method(POST, RequestBody.create(MediaType.get(APPLICATION_JSON_CHARSET_UTF_8), fileInfo.toJson()))
                .addHeader(AUTHORIZATION, BEARER + license)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        logRequest(request, fileInfo);

        String result = "";
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            logResponse(response, responseBody);

            if (response.code() != HttpStatus.OK.value()) {
                throw new VetologyConnectorException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
            }
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(responseBody);
            result = obj.get(TRANSFER_ID).toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new VetologyConnectorException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
        }
        return result;
    }

    @Override
    public void callUploadChunks(ChunkFileInfo chunkFileInfo){
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(CHUNK, chunkFileInfo.getChunk().getName(),
                        RequestBody.create(MediaType.parse(APPLICATION_OCTET_STREAM),
                                new File(chunkFileInfo.getChunk().getAbsolutePath())))
                .addFormDataPart(CHUNK_NUMBER, String.valueOf(chunkFileInfo.getChunkNumber()))
                .addFormDataPart(TRANSFER_ID, chunkFileInfo.getTransferId())
                .build();

        Request request = new Request.Builder()
                .url(baseUrl + uploadChunkUrl)
                .method(POST, body)
                .addHeader(AUTHORIZATION, BEARER + license)
                .addHeader(CONTENT_TYPE, MULTIPART_FORM_DATA)
                .build();
        logRequest(request, chunkFileInfo);

        try {
            Response response = client.newCall(request).execute();
            logResponse(response, response.body().string());

            if (response.code() != HttpStatus.OK.value()) {
                throw new VetologyConnectorException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new VetologyConnectorException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
        }

    }

    private void logRequest(Request request, Jsonable jsonable) {
        log.info("\n[REQUEST - {}] \n-header\n {} \n-body : {}",request.url(), request.headers(), jsonable.toJson());
    }

    private void logRequest(Request request) {
        log.info("\n[REQUEST - {}] \n-header\n {}",request.url(), request.headers());
    }

    private void logResponse(Response response, String responseBody) throws IOException {
        log.info("\n[RESPONSE] \n-status : {} \n-header : {} \n-body : {}", response.code(), response.headers(), responseBody);
    }



}
