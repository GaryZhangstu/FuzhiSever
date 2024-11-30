package com.fuzhi.fuzhisever.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fuzhi.fuzhisever.DTO.SkinAnalyzeRequest;
import lombok.AllArgsConstructor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

@Service
@AllArgsConstructor
public class CommunicationService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private String bucketName;


    public Object getFacialReport(String api_key, String api_secret, File image_file) throws IOException {
        SkinAnalyzeRequest requestData = new SkinAnalyzeRequest(api_key, api_secret);
        String jsonString = objectMapper.writeValueAsString(requestData);
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(RequestBody.create(jsonString, MediaType.get("application/json; charset=utf-8")))
                .build();
        return null;
    }
    public void uploadFileToS3(File file, String key) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        PutObjectResponse res = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromFile(file));

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
