package com.xdyan.templates.filter;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Optional.ofNullable;

@Slf4j
@Provider
public class CorrelationFilter implements ContainerRequestFilter, WriterInterceptor {

  private static final String AGW_REQUEST_ID_KEY = "x-agw-request-id";
  private static final int MAX_LOG_LENGTH = 500;
  private static final String EMPTY = "";

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    var apiGwRequestId = ofNullable(requestContext.getHeaderString(AGW_REQUEST_ID_KEY)).orElse(EMPTY);
    var method = ofNullable(requestContext.getMethod()).orElse(EMPTY);
    var path = ofNullable(requestContext.getUriInfo().getRequestUri().toString()).orElse(EMPTY);

    MDC.put("requestId", apiGwRequestId);
    MDC.put("method", method);
    MDC.put("path", path);
    if (!requestContext.hasEntity()) {
      log.info("Request Body is empty");
    } else {
      try {
        String requestBody = new String(requestContext.getEntityStream().readAllBytes(), UTF_8);
        requestContext.setEntityStream(new ByteArrayInputStream(requestBody.getBytes(UTF_8)));

        if  (requestBody.length() > MAX_LOG_LENGTH) {
          log.info("Request Body (Truncated): {}", requestBody.substring(0, MAX_LOG_LENGTH));
        } else {
          log.info("Request Body: {}", requestBody);
        }
      } catch (IOException e) {
        log.error("Error reading request body", e);
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
    OutputStream originalStream = context.getOutputStream();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    context.setOutputStream(baos);

    try {
      context.proceed();
      byte[] responseBytes = baos.toByteArray();

      if (responseBytes.length == 0) {
        log.info("Response Body is empty");
      } else {
        String responseBody = new String(responseBytes, UTF_8);

        if (responseBody.length() > MAX_LOG_LENGTH) {
          String truncatedBody = responseBody.substring(0, MAX_LOG_LENGTH) + "...";
          log.info("Response Body (Truncated): {}", truncatedBody);
        } else {
          log.info("Response Body: {}", responseBody);
        }
      }
      originalStream.write(responseBytes);

    } finally {
      context.setOutputStream(originalStream);
      MDC.clear();
    }
  }
}
