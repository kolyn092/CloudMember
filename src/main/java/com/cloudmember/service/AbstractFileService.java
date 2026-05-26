package com.cloudmember.service;

import com.cloudmember.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public abstract class AbstractFileService implements IFileService {

    protected void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("파일이 비어있습니다.");
        }
    }

    protected String generateFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    protected String generateKey(Long memberId, String fileName) {
        return "profiles/" + memberId + "/" + fileName;
    }
}
