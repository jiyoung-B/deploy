package com.example.project3.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.project3.exception.NotImageFileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;
    private static final String DIR_SNS = "sns";
    private static final String DIR_PROFILE_IMAGE = "ProfileImage";


    private final AmazonS3Client amazonS3Client;

    public String uploadProfileImage(MultipartFile file) throws IOException {
        if (isImageFile(file)) {
            return upload1(file, DIR_PROFILE_IMAGE);
        } else {
            log.error("이미지 파일이 아닙니다.");
            throw new NotImageFileException("Unsupported file type");
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    public List<String> upload(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            log.info("파일이 비어있습니다.");
            return Collections.emptyList();
        }
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null) {
                try {
                    //String fileUrl = upload(file);
                    String fileUrl = upload1(file, DIR_SNS);
                    fileUrls.add(fileUrl);

                } catch (Exception e) {
                    log.error("S3 업로드 중 오류 발생", e);
                    e.printStackTrace();
                    return Collections.emptyList();
                }
            }
        }
        log.info("fileUrls" + fileUrls);
        return fileUrls;
    }
    private String upload1(MultipartFile multipartFile, String dirName) throws IOException {
        if (multipartFile == null) {
            throw new IllegalArgumentException("파일이 null입니다.");
        }
        UUID uuid = UUID.randomUUID();
        String originName = multipartFile.getOriginalFilename();
        // String extension = getFileExtension(originName);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String fileName = dirName + "/" + timestamp + "_" + uuid.toString().substring(0, 8) + "_" + originName;


        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName,
                multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }


    private String upload(MultipartFile multipartFile) throws IOException {
        UUID uuid = UUID.randomUUID();
        String originName = multipartFile.getOriginalFilename();
        String fileName = uuid + "_" + originName + "/"+ multipartFile.getContentType();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName,
                multipartFile.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        amazonS3Client.putObject(putObjectRequest);

        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }


//    public String putS3(File uploadFile, String dirName, String originName) {
//        UUID uuid = UUID.randomUUID();
//        String fileName = dirName + "/" + uuid + "_" + originName;
//        amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
//        return amazonS3Client.getUrl(bucketName, fileName).toString();
//    }
//
//    private void removeNewFile(File targetFile) {
//        if (targetFile.delete()) {
//            log.info("파일이 삭제되었습니다.");
//        } else {
//            log.info("파일이 삭제되지 못했습니다.");
//        }
//    }
//
//    public Optional<File> convert(MultipartFile file) {
//
//        try {
//            File convertedFile = File.createTempFile("temp", null);
//            file.transferTo(convertedFile);
//            return Optional.of(convertedFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return Optional.empty();
//        }
//    }

    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }


    public void delete(String s3FileName) {

        String dirName;
        if (s3FileName.contains(DIR_PROFILE_IMAGE)) {
            dirName = DIR_PROFILE_IMAGE;
        } else if (s3FileName.contains(DIR_SNS)) {
            dirName = DIR_SNS;
        } else {
            throw new IllegalArgumentException("Invalid S3 file name: " + s3FileName);
        }

        String bucketPath = dirName + "/";
        String fileName = extractFileNameFromUrl(s3FileName);
        String filePath = bucketPath + fileName;

        boolean isObjectExist = amazonS3Client.doesObjectExist(bucketName, filePath);
        if (isObjectExist) {
            amazonS3Client.deleteObject(bucketName, filePath);
            //amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            //amazonS3Client.deleteObject(new DeleteObjectRequest(bucketName, filePath));
        } else {
            throw new IllegalStateException("File not found: " + filePath);
        }
    }

    public String extractFileNameFromUrl(String url) {
        try {
            URI uri = new URI(url);
            String path = uri.getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to extract file name from URL", e);
        }
    }



}


