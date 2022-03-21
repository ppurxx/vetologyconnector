package com.example.vetologyconnector.service;

import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpBodyPublisherConverter {
  public static BodyPublisher toBodyPublisherFromMap(Map<String, Object> data){
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, Object> entry : data.entrySet()) {
      if (builder.length() > 0) {
        builder.append("&");
      }
      builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
      builder.append("=");
      builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
    }
    return HttpRequest.BodyPublishers.ofString(builder.toString());
  }
}
