package com.fuzhi.fuzhiServer.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
@AllArgsConstructor
@Log4j2
public class CommunicationService {
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final Tika tika;
    private String bucketName;

    @Value("${facialAnalysis.api_key}")
    private String api_key;

    @Value("${facialAnalysis.api_secret}")
    private String api_secret;

    @Value("${facialAnalysis.url}")
    private String url;

    /**
     * 获取面部分析报告。
     *
     * @param imageFile 包含图像的 MultipartFile 对象
     * @return 分析报告的 JSON 对象
     * @throws IOException 如果在处理文件或发送请求时发生错误
     */
    public Object getFacialReport(MultipartFile imageFile) throws IOException {
        // 获取文件名
        String fileName = Objects.requireNonNullElse(imageFile.getOriginalFilename(), "");
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("File name is null or empty");
        }

        // 获取文件扩展名
        String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();
        if (fileExtension.isEmpty()) {
            throw new IllegalArgumentException("File extension is missing");
        }

        // 检测文件的 MIME 类型
        String mimeType = tika.detect(imageFile.getInputStream());
        MediaType mediaType = MediaType.parse(mimeType);
        if (mediaType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Unsupported file type: " + fileExtension);
        }

        // 构建请求体
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image_file", fileName, RequestBody.create(imageFile.getBytes(), mediaType))
                .addFormDataPart("api_key", api_key)
                .addFormDataPart("api_secret", api_secret)
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 发送请求并处理响应
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Unexpected code {}", response.code());
                throw new IOException("Unexpected code " + response.code());
            }

            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Response body is null");
            }

            // 将响应体解析为 JSON 对象
            return objectMapper.readValue(responseBody.string(), Object.class);
        }
    }

    /**
     * 将文件上传到 S3 存储桶。
     *
     * @param inputStream 文件的输入流
     * @param key         文件在 S3 中的键
     * @throws IOException 如果在上传文件时发生错误
     */
    public void uploadFileToS3(InputStream inputStream, String key) throws IOException {
        log.info("Checking if bucket exists: {}", bucketName);
        if (!bucketExists(bucketName)) {
            log.warn("Bucket {} does not exist", bucketName);
        }

        // 构建 PutObject 请求
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType("image/png")
                .contentDisposition("inline")
                .build();

        // 上传文件到 S3
        try (inputStream) {
            PutObjectResponse res = s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(inputStream, inputStream.available()));
            log.info("File uploaded successfully to S3 with key: {}", key);
        } catch (IOException e) {
            log.error("Error uploading file to S3", e);
            throw e;
        }
    }

    /**
     * 检查指定的 S3 存储桶是否存在。
     *
     * @param bucketName 存储桶名称
     * @return 如果存储桶存在，返回 true；否则返回 false
     */
    private boolean bucketExists(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        }
    }
}
