package com.cloudmember.service;

import jakarta.annotation.PostConstruct;
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
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
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

    @PostConstruct
    public void init() {
        try {
            this.privateKey = loadPrivateKey(privateKeyPem);
            log.info("CloudFront PrivateKey 로딩 성공");
        } catch (Exception e) {
            log.error("CloudFront PrivateKey 로딩 실패", e);
            throw new RuntimeException(e);
        }
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
            String resourceUrl = "https://" + cloudFrontDomain + "/" + key;
            Instant expirationTime = Instant.now().plus(SIGNED_URL_EXPIRATION_DAYS, ChronoUnit.DAYS);

            CannedSignerRequest signerRequest = CannedSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(privateKey)
                    .keyPairId(keyPairId)
                    .expirationDate(expirationTime)
                    .build();

            SignedUrl signedUrl = cloudFrontUtilities.getSignedUrlWithCannedPolicy(signerRequest);

            return signedUrl.url();
        } catch (Exception e) {
            throw new RuntimeException("CloudFront Signed URL 생성 실패", e);
        }
    }

    private PrivateKey loadPrivateKey(String pem) throws Exception {

        log.info("pem key : {}", pem);

        byte[] decoded = Base64.getDecoder().decode(pem);

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

        return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
    }
}
