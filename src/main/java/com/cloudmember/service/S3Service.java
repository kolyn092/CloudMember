package com.cloudmember.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CannedSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

    private static final long SIGNED_URL_EXPIRATION_DAYS = 7;

    private final S3Client s3Client;
    private final CloudFrontUtilities cloudFrontUtilities;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${spring.cloud.aws.cloudfront.key-pair-id}")
    private String keyPairId;

    @Value("${spring.cloud.aws.cloudfront.private-key}")
    private String privateKeyPem;

    private PrivateKey privateKey;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
        this.cloudFrontUtilities = CloudFrontUtilities.create();
    }

    public String uploadProfileImage(Long memberId, MultipartFile file) {
        try {
            String fileName = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            String key = "profiles/" + memberId + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize()
                    )
            );

            return key;

        } catch (IOException e) {
            // 적절한 커스텀 예외로 바꾸고, GlobalExceptionHandler로 핸들링 필요
            throw new RuntimeException("파일 업로드 실패", e);
        }
    }

    public String generateSignedUrl(String key) {
        try {
            Path privateKeyPath = Files.createTempFile("cf-key", ".pem");
            Files.writeString(privateKeyPath, key);
            privateKeyPath.toFile().deleteOnExit();

            String resourceUrl = "https://" + cloudFrontDomain + "/" + key;

            CannedSignerRequest signerRequest = CannedSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(privateKeyPath)
                    .keyPairId(keyPairId)
                    .expirationDate(
                            Instant.now().plus(SIGNED_URL_EXPIRATION_DAYS, ChronoUnit.DAYS)
                    )
                    .build();

            SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(signerRequest);

            return signedUrl.url();

        } catch (Exception e) {
            throw new RuntimeException("CloudFront Signed URL 생성 실패", e);
        }
    }
}
