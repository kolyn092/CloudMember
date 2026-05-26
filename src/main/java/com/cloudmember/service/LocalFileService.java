package com.cloudmember.service;

import com.cloudmember.exception.BadRequestException;
import com.cloudmember.exception.FileUploadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@Profile("local")
public class LocalFileService implements IFileService {

    @Override
    public String uploadProfileImage(Long memberId, MultipartFile file) {

        if (file.isEmpty()) {
            throw new BadRequestException("파일이 비어있습니다.");
        }

        try {
            Path uploadPath = Paths.get("uploads", "profiles", memberId.toString());
            Files.createDirectories(uploadPath);

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.write(filePath, file.getBytes());

            log.info("[LOCAL] 파일 저장 완료: {}", filePath);

            // uploads/ 이후의 상대 경로만 반환 (URL용)
            String key = "profiles/" + memberId + "/" + fileName;

            return key;
        } catch (IOException e) {
            throw new FileUploadException("로컬 파일 저장 실패", e);
        }
    }

    @Override
    public String generateSignedUrl(String key) {
        log.info("[LOCAL] Signed URL 생성 스킵 - key: {}", key);

        // key를 URL 경로 형식으로 변환 (Windows 경로 구분자 처리)
        String urlPath = key.replace("\\", "/");
        return "http://localhost:8080/files/" + urlPath;
    }
}
