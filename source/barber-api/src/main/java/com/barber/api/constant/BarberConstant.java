package com.barber.api.constant;


import java.util.Arrays;
import java.util.List;

public class BarberConstant {
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";


    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_EMPLOYEE = 2;
    public static final Integer USER_KIND_CUSTOMER = 3;

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_DELETE = -2;

    public static final Integer NATION_KIND_PROVINCE = 1;
    public static final Integer NATION_KIND_DISTRICT = 2;
    public static final Integer NATION_KIND_WARD = 3;

    public static final List<Integer> NATION_KINDS = List.of(NATION_KIND_PROVINCE, NATION_KIND_DISTRICT, NATION_KIND_WARD);

    public static final Integer EMPLOYEE_GENDER_MALE = 1;
    public static final Integer EMPLOYEE_GENDER_FEMALE = 2;
    public static final Integer EMPLOYEE_GENDER_OTHER = 3;

    public static final Integer GROUP_KIND_ADMIN = 1;
    public static final Integer GROUP_KIND_MANAGER = 2;
    public static final Integer GROUP_KIND_USER=3;

    public static final Integer MAX_OTP_INVALID_COUNT = 5;
    public static final int MAX_TIME_OTP_CREATED = 5 * 60 * 1000; //5 minutes
    public static final Integer MAX_ATTEMPT_LOGIN = 5;

    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,15}$";
    public static final String PHONE_PATTERN = "^0\\d{9}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    private BarberConstant(){
        throw new IllegalStateException("Utility class");
    }
}
