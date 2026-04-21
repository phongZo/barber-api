package com.barber.api.constant;


import java.time.ZoneId;
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

    public static final Integer CATEGORY_KIND_SYSTEM = 0;
    public static final Integer CATEGORY_KIND_SERVICE_GROUP = 1;
    public static final Integer CATEGORY_KIND_SERVICE = 2;
    public static final Integer CATEGORY_KIND_OPTION = 3;

    public static final List<Integer> CATEGORY_KINDS = List.of(CATEGORY_KIND_SYSTEM, CATEGORY_KIND_SERVICE_GROUP, CATEGORY_KIND_SERVICE, CATEGORY_KIND_OPTION);

    public static final Integer BOOKING_STATUS_BOOKING = 1;
    public static final Integer BOOKING_STATUS_COMPLETED = 2;
    public static final Integer BOOKING_STATUS_CANCELED = 3;
    public static final List<Integer> BOOKING_STATUS = List.of(BOOKING_STATUS_BOOKING, BOOKING_STATUS_COMPLETED, BOOKING_STATUS_CANCELED);


    public static final Integer GROUP_KIND_ADMIN = 1;
    public static final Integer GROUP_KIND_MANAGER = 2;
    public static final Integer GROUP_KIND_USER=3;

    public static final Integer MAX_OTP_INVALID_COUNT = 5;
    public static final int MAX_TIME_OTP_CREATED = 5 * 60 * 1000; //5 minutes
    public static final Integer MAX_ATTEMPT_LOGIN = 5;

    public static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,15}$";
    public static final String PHONE_PATTERN = "^0\\d{9}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String HOUR_PATTERN = "^([01][0-9]|2[0-3]):[0-5][0-9]$";
    public static final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{4}";

    public static final Integer SLOT_INTERVAL = 20;
    public static final ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

    private BarberConstant(){
        throw new IllegalStateException("Utility class");
    }
}
