package com.barber.api.dto;

import lombok.Data;

@Data
public class UploadFileDto {
    private String filePath;
    private String fileName;
    private String ext;
}
