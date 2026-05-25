package com.cloudmember.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    String uploadProfileImage(Long memberId, MultipartFile file);
    String generateSignedUrl(String key);
}
