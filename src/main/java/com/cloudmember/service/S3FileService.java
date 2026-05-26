package com.cloudmember.service;

import com.cloudmember.exception.BadRequestException;
import com.cloudmember.exception.FileUploadException;
import com.cloudmember.exception.SignedUrlException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
@Service
@Profile("prod")
public class S3FileService extends AbstractFileService {

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

    public S3FileService(S3Client s3Client) {
        this.s3Client = s3Client;
        this.cloudFrontUtilities = CloudFrontUtilities.create();
    }

    @Override
    public String uploadProfileImage(Long memberId, MultipartFile file) {

        validateFile(file);

        try {
            String fileName = generateFileName(file.getOriginalFilename());
            String key = generateKey(memberId, fileName);

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
            throw new FileUploadException("파일 업로드 실패", e);
        }
    }

    @Override
    public String generateSignedUrl(String key) {
        try {
            Path privateKeyPath = Files.createTempFile("cf-key", ".pem");
            Files.writeString(privateKeyPath, privateKeyPem);
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
            throw new SignedUrlException("CloudFront Signed URL 생성 실패", e);
        }
    }
}
