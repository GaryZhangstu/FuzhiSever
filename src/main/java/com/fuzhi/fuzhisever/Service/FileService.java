package com.fuzhi.fuzhisever.Service;

import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileService {
    private final Tika tika;
    public String getFileType(InputStream file) {
        if (file == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        try (InputStream inputStream = file) {
            return tika.detect(inputStream);
        } catch (IOException e) {
            // Log the exception or handle it as needed

            return "unknown";
        }
    }
}
