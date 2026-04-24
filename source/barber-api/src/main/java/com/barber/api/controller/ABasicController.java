package com.barber.api.controller;

import com.barber.api.constant.BarberConstant;
import com.barber.api.jwt.BarberJwt;
import com.barber.api.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;

import java.util.Objects;

public class ABasicController {
    @Autowired
    private UserServiceImpl userService;

    public Long getCurrentUser(){
        if (userService.getAddInfoFromToken() != null){
            BarberJwt barberJwt = userService.getAddInfoFromToken();
            return barberJwt.getAccountId();
        } else {
            return null;
        }
    }

    public long getCurrentDevice(){
        BarberJwt barberJwt = userService.getAddInfoFromToken();
        return barberJwt.getDeviceId();
    }

    public long getTokenId(){
        BarberJwt barberJwt = userService.getAddInfoFromToken();
        return barberJwt.getTokenId();
    }

    public BarberJwt getSessionFromToken(){
        return userService.getAddInfoFromToken();
    }

    public boolean isSuperAdmin(){
        BarberJwt barberJwt = userService.getAddInfoFromToken();
        if(barberJwt !=null){
            return barberJwt.getIsSuperAdmin();
        }
        return false;
    }

    public boolean isAdmin(){
        BarberJwt barberJwt = userService.getAddInfoFromToken();
        if(barberJwt != null){
            return Objects.equals(barberJwt.getUserKind(), BarberConstant.USER_KIND_ADMIN);
        }
        return false;
    }

    public String getCurrentToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            OAuth2AuthenticationDetails oauthDetails =
                    (OAuth2AuthenticationDetails) authentication.getDetails();
            if (oauthDetails != null) {
                return oauthDetails.getTokenValue();
            }
        }
        return null;
    }
}
