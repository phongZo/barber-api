package com.barber.api.service;

import com.barber.api.dto.ApiMessageDto;
import com.barber.api.dto.ErrorCode;
import com.barber.api.dto.UploadFileDto;
import com.barber.api.exception.BadRequestException;
import com.barber.api.form.UploadFileForm;
import com.barber.api.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BarberApiService {
    @Value("${file.upload-dir}")
    private String rootDirectory;

    @Autowired
    BarberOTPService barberOTPService;

    @Autowired
    CommonAsyncService commonAsyncService;

    private Map<String, Long> storeQRCodeRandom = new ConcurrentHashMap<>();

    static final String[] UPLOAD_TYPES = new String[]{"LOGO", "AVATAR", "IMAGE"};

    static final String[] AVATAR_EXTENSION = new String[]{"jpeg", "jpg", "gif", "bmp", "png"};

    public ApiMessageDto<UploadFileDto> storeFile(UploadFileForm uploadFileForm) {
        ApiMessageDto<UploadFileDto> apiMessageDto = new ApiMessageDto<>();
        try {
            boolean contains = Arrays.stream(UPLOAD_TYPES).anyMatch(uploadFileForm.getType()::equalsIgnoreCase);
            if (!contains) {
                throw new BadRequestException("Type is required in LOGO, AVATAR or IMAGE", ErrorCode.FILE_ERROR_UPLOAD_TYPE_INVALID);
            }
            String fileName = StringUtils.cleanPath(uploadFileForm.getFile().getOriginalFilename());
            String extension = FilenameUtils.getExtension(fileName);
            if (uploadFileForm.getType().equals("AVATAR") && !Arrays.stream(AVATAR_EXTENSION).anyMatch(extension::equalsIgnoreCase)) {
                throw new BadRequestException("ERROR-FILE-FORMAT-INVALID", "File format is invalid");
            }
            //upload to uploadFolder/TYPE/id
            String finalFile = uploadFileForm.getType() + "_" + RandomStringUtils.randomAlphanumeric(10) + "." + extension;
            String typeFolder = File.separator + uploadFileForm.getType();

            Path fileStorageLocation = Paths.get(rootDirectory + typeFolder).toAbsolutePath().normalize();
            Files.createDirectories(fileStorageLocation);
            Path targetLocation = fileStorageLocation.resolve(finalFile);
            Files.copy(uploadFileForm.getFile().getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            UploadFileDto uploadFileDto = new UploadFileDto();
            uploadFileDto.setFilePath(typeFolder + File.separator + finalFile);
            apiMessageDto.setData(uploadFileDto);
            apiMessageDto.setMessage("Upload file success");

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            apiMessageDto.setResult(false);
            apiMessageDto.setMessage("" + e.getMessage());
        }
        return apiMessageDto;
    }

    public Resource loadFileAsResource(String folder, String fileName) {
        System.out.println("User.home: " + System.getProperty("spring.config.location"));
        System.out.println("get file: " + folder + "/" + fileName + ", path: " + rootDirectory);
        try {
            Path fileStorageLocation = Paths.get(rootDirectory + File.separator + folder).toAbsolutePath().normalize();
            Path fP = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(fP.toUri());
            if (resource.exists()) {
                return resource;
            }
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void deleteFile(String filePath) {
        File file = new File(rootDirectory + filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    public String getOTP(){
        return barberOTPService.generate(6);
    }

    public synchronized Long getOrderHash(){
        return Long.parseLong(barberOTPService.generate(9))+System.currentTimeMillis();
    }


    public void sendEmail(String email, String msg, String subject, boolean html){
        commonAsyncService.sendEmail(email,msg,subject,html);
    }


    public String convertGroupToUri(List<Permission> permissions){
        if(permissions!=null){
            StringBuilder builderPermission = new StringBuilder();
            for(Permission p : permissions){
                builderPermission.append(p.getAction().trim().replace("/v1","")+",");
            }
            return  builderPermission.toString();
        }
        return null;
    }

    public String getOrderStt(Long storeId){
        return barberOTPService.orderStt(storeId);
    }


    public synchronized boolean checkCodeValid(String code){
        //delelete key has valule > 60s
        Set<String> keys = storeQRCodeRandom.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Long value = storeQRCodeRandom.get(key);
            if((System.currentTimeMillis() - value) > 60000){
                storeQRCodeRandom.remove(key);
            }
        }

        if(storeQRCodeRandom.containsKey(code)){
            return false;
        }
        storeQRCodeRandom.put(code,System.currentTimeMillis());
        return true;
    }
}
