package com.likelion.sbstudy.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.likelion.sbstudy.global.config.S3Config;
import com.likelion.sbstudy.global.exception.CustomException;
import com.likelion.sbstudy.global.s3.dto.S3Response;
import com.likelion.sbstudy.global.s3.entity.PathName;
import com.likelion.sbstudy.global.s3.exception.S3ErrorCode;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

  private final AmazonS3 amazonS3;
  private final S3Config s3Config;

  public S3Response uploadImage(PathName pathName, MultipartFile file) {

    String imgUrl = uploadFile(pathName, file);

    return S3Response.builder().imageUrl(imgUrl).build();
  }

  public String uploadFile(PathName pathName, MultipartFile file) {

    validateFile(file);

    String keyName = createKeyName(pathName);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(file.getSize());
    metadata.setContentType(file.getContentType());

    try {
      amazonS3.putObject(
          new PutObjectRequest(s3Config.getBucket(), keyName, file.getInputStream(), metadata));
      return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    } catch (Exception e) {
      log.error("S3 upload 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  public String base64UploadFile(PathName pathName, String base64Url) {
    if (!validateBase64(base64Url)) {
      throw new CustomException(S3ErrorCode.INVALID_BASE64);
    }

    String base64Data = base64Url;
    String contentType = "image/png";

    if (base64Url.contains(",")) {
      String[] parts = base64Url.split(",");
      if (parts[0].contains("data:") && parts[0].contains(";base64")) {
        contentType = parts[0].substring(5, parts[0].indexOf(";"));
      }
      base64Data = parts[1];
    }

    if (contentType.isEmpty()) {
      contentType = "image/png";
    }

    byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
    String keyName = createKeyName(pathName);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(decodedBytes.length);
    metadata.setContentType(contentType);

    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(decodedBytes)) {
      amazonS3.putObject(
          new PutObjectRequest(s3Config.getBucket(), keyName, inputStream, metadata));
      return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    } catch (Exception e) {
      log.error("S3 upload 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  public String createKeyName(PathName pathName) {

    return switch (pathName) {
      case FOLDER1 -> s3Config.getFolder1Path();
      case FOLDER2 -> s3Config.getFolder2Path();
    }
        + '/'
        + UUID.randomUUID();
  }

  public String getFileUrl(String keyName) {
    existFile(keyName);

    try {
      return amazonS3.getUrl(s3Config.getBucket(), keyName).toString();
    } catch (Exception e) {
      log.error("S3 upload 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  public void deleteFile(String keyName) {
    existFile(keyName);

    try {
      amazonS3.deleteObject(new DeleteObjectRequest(s3Config.getBucket(), keyName));
    } catch (Exception e) {
      log.error("S3 upload 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  public List<String> getAllFiles(PathName pathName) {
    String prefix = switch (pathName) {
      case FOLDER1 -> s3Config.getFolder1Path();
      case FOLDER2 -> s3Config.getFolder2Path();
    };

    try {
      return amazonS3
          .listObjectsV2(
              new ListObjectsV2Request().withBucketName(s3Config.getBucket()).withPrefix(prefix))
          .getObjectSummaries()
          .stream()
          .map(obj -> amazonS3.getUrl(s3Config.getBucket(), obj.getKey()).toString())
          .collect(Collectors.toList());
    } catch (Exception e) {
      log.error("S3 파일 목록 조회 중 오류 발생", e);
      throw new CustomException(S3ErrorCode.FILE_SERVER_ERROR);
    }
  }

  public void deleteFile(PathName pathName, String fileName) {
    String prefix = switch (pathName) {
      case FOLDER1 -> s3Config.getFolder1Path();
      case FOLDER2 -> s3Config.getFolder2Path();
    };
    String keyName = prefix + "/" + fileName;
    deleteFile(keyName);
  }

  private void existFile(String keyName) {
    if (!amazonS3.doesObjectExist(s3Config.getBucket(), keyName)) {
      throw new CustomException(S3ErrorCode.FILE_NOT_FOUND);
    }
  }

  private void validateFile(MultipartFile file) {
    if (file.getSize() > 5 * 1024 * 1024) {
      throw new CustomException(S3ErrorCode.FILE_SIZE_INVALID);
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new CustomException(S3ErrorCode.FILE_TYPE_INVALID);
    }
  }

  private boolean validateBase64(String base64Data) {
    if (base64Data == null || base64Data.trim().isEmpty()) {
      return false;
    }
    try {
      Base64.getDecoder().decode(base64Data.contains(",") ? base64Data.split(",")[1] : base64Data);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}