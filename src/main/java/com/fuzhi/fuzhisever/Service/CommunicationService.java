package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Service
@AllArgsConstructor
public class CommunicationService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private String bucketName;

    @Value("${facialAnalysis.api_key}")
    private String api_key;

    @Value("${facialAnalysis.api_secret}")
    private String api_secret;

    @Value("${facialAnalysis.url}")
    private String url;
    public Object getFacialReport(MultipartFile image_file) throws IOException {
        String fileName = image_file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("File name is null or empty");
        }

        String fileExtension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

        // 根据文件扩展名确定 MIME 类型
        MediaType mediaType = MediaType.parse("image/" + fileExtension);
        if (mediaType == null) {
            throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        }

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image_file", fileName, RequestBody.create(image_file.getBytes(), mediaType))
                .addFormDataPart("api_key", api_key)
                .addFormDataPart("api_secret", api_secret)
                //.addFormDataPart("return_maps", "red_area,brown_area,texture_enhanced_pores,texture_enhanced_blackheads,texture_enhanced_oily_area,texture_enhanced_lines,water_area,rough_area,roi_outline_map,texture_enhanced_bw")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            ResponseBody responseBody = response.body();
            assert responseBody != null;

            return objectMapper.readValue(responseBody.string(), Object.class);
        }
    }


    public void uploadFileToS3(InputStream inputStream, String key) throws IOException {
        System.out.println("aws s3 "+bucketExists(bucketName)+bucketName);
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .contentDisposition("inline")
                .build();
        PutObjectResponse res = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, inputStream.available()));

    }
    private boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
    public String generatePresignedUrl(String key, Duration expiration) {
        try (S3Presigner s3Presigner = S3Presigner.create()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PresignedGetObjectRequest presignedGetObjectRequest = s3Presigner.presignGetObject(builder -> builder.getObjectRequest(getObjectRequest).signatureDuration(expiration));

            return presignedGetObjectRequest.url().toString();
        } catch (SdkException e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

}
