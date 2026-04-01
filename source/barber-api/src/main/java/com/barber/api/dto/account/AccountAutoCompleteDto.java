package com.barber.api.dto.account;

import lombok.Data;

@Data
public class AccountAutoCompleteDto {
    private long id;
    private String fullName;
    private String avatarPath;
}
