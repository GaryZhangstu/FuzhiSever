package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Objects;



@Service
@AllArgsConstructor
@Log4j2
public class CommunicationService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private String bucketName;
    private final Tika tika;

    @Value("${facialAnalysis.api_key}")
    private String api_key;

    @Value("${facialAnalysis.api_secret}")
    private String api_secret;

    @Value("${facialAnalysis.url}")
    private String url;

    public Object getFacialReport(MultipartFile image_file) throws IOException {
        String fileName = Objects.requireNonNullElse(image_file.getOriginalFilename(), "");
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("File name is null or empty");
        }

        String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (fileExtension.isEmpty()) {
            throw new IllegalArgumentException("File extension is missing");
        }


        String mimeType = tika.detect(image_file.getInputStream());
        MediaType mediaType = MediaType.parse(mimeType);
        if (mediaType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image_file", fileName, RequestBody.create(image_file.getBytes(), mediaType))
                .addFormDataPart("api_key", api_key)
                .addFormDataPart("api_secret", api_secret)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Unexpected code {}", response.code());
                throw new IOException("Unexpected code " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body is null");
            }

            return objectMapper.readValue(responseBody.string(), Object.class);
        }
    }

    public void uploadFileToS3(InputStream inputStream, String key) throws IOException {
        log.info("Checking if bucket exists: {}", bucketName);
        if (!bucketExists(bucketName)) {
            log.warn("Bucket {} does not exist", bucketName);

        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .contentDisposition("inline")
                .build();

        try (inputStream) {
            PutObjectResponse res = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, inputStream.available()));
            log.info("File uploaded successfully to S3 with key: {}", key);
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw e;
        }
    }

    private boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
}
