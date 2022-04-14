package com.example.vetologyconnector.service;


import com.example.vetologyconnector.exception.VetologyConnectException;
import com.example.vetologyconnector.enums.AnalysisResponseCode;
import com.example.vetologyconnector.model.ContactInfoRequest;
import com.example.vetologyconnector.model.DicomChunkFileInfoRequest;
import com.example.vetologyconnector.model.DicomFileInfoRequest;
import com.example.vetologyconnector.model.Jsonable;
import com.example.vetologyconnector.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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

import javax.net.ssl.*;

@Service
@Slf4j
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

//    private final static OkHttpClient client = new OkHttpClient().newBuilder().build();
    private final static OkHttpClient client = getUnsafeOkHttpClient();
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
            throw new VetologyConnectException(AnalysisResponseCode.INTERNAL_ERROR);
        }

        return result;
    }

    @Override
    public void callContactInformation(ContactInfoRequest contactInfoRequest) {
        Request request = new Request.Builder()
                .url(baseUrl + contactInformationUrl)
                .method(POST, RequestBody.create(MediaType.get(APPLICATION_JSON_CHARSET_UTF_8), contactInfoRequest.toJson()))
                .addHeader(AUTHORIZATION, BEARER + license)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        logRequest(request, contactInfoRequest);
        try {
            Response response = client.newCall(request).execute();
            logResponse(response, response.body().string());

            if (response.code() != HttpStatus.OK.value()) {
                throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
        }

    }

    @Override
    public String callNewFile(DicomFileInfoRequest dicomFileInfoRequest) {
        Request request = new Request.Builder()
                .url(baseUrl + newFileUrl)
                .method(POST, RequestBody.create(MediaType.get(APPLICATION_JSON_CHARSET_UTF_8), dicomFileInfoRequest.toJson()))
                .addHeader(AUTHORIZATION, BEARER + license)
                .addHeader(CONTENT_TYPE, APPLICATION_JSON)
                .build();

        logRequest(request, dicomFileInfoRequest);

        String result = "";
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            logResponse(response, responseBody);

            if (response.code() != HttpStatus.OK.value()) {
                throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
            }
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(responseBody);
            result = obj.get(TRANSFER_ID).toString();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
        }
        return result;
    }

    @Override
    public void callUploadChunks(DicomChunkFileInfoRequest dicomChunkFileInfoRequest) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(CHUNK, dicomChunkFileInfoRequest.getChunk().getName(),
                        RequestBody.create(MediaType.parse(APPLICATION_OCTET_STREAM), dicomChunkFileInfoRequest.getChunk()))
                .addFormDataPart(CHUNK_NUMBER, dicomChunkFileInfoRequest.getChunk_number() + "")
                .addFormDataPart(TRANSFER_ID, dicomChunkFileInfoRequest.getTransfer_id())
                .build();

        Request request = new Request.Builder()
                .url(baseUrl + uploadChunkUrl)
                .method(POST, body)
                .addHeader(AUTHORIZATION, BEARER + license)
                .addHeader(CONTENT_TYPE, MULTIPART_FORM_DATA)
                .build();
        logRequest(request, dicomChunkFileInfoRequest);

        try {
            Response response = client.newCall(request).execute();
            logResponse(response, response.body().string());

            if (response.code() != HttpStatus.OK.value()) {
                throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new VetologyConnectException(AnalysisResponseCode.FILE_TRANSFER_FAILED);
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

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    }).build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
