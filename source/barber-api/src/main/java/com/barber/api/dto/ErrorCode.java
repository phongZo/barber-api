package com.barber.api.dto;

public class ErrorCode {
    /**
     * Starting error code Account
     * */
    public static final String ACCOUNT_ERROR_UNKNOWN = "ERROR-ACCOUNT-0000";
    public static final String ACCOUNT_ERROR_EXIST = "ERROR-ACCOUNT-0001";
    public static final String ACCOUNT_ERROR_NOT_FOUND = "ERROR-ACCOUNT-0002";
    public static final String ACCOUNT_ERROR_WRONG_PASSWORD = "ERROR-ACCOUNT-0003";
    public static final String ACCOUNT_ERROR_LOCKED = "ERROR-ACCOUNT-0004";
    public static final String ACCOUNT_ERROR_OPT_INVALID = "ERROR-ACCOUNT-0005";
    public static final String ACCOUNT_ERROR_NOT_ALLOW_DELETE_SUPPER_ADMIN = "ERROR-ACCOUNT-006";
    public static final String ACCOUNT_ERROR_INCORRECT_HASH_VERIFICATION = "ERROR-ACCOUNT-007";
    public static final String ACCOUNT_ERROR_VERIFY_FAILED = "ERROR-ACCOUNT-0008";
    public static final String ACCOUNT_ERROR_NOT_PENDING = "ERROR-ACCOUNT-0009";

    /**
     * Starting error code Customer
     * */
    public static final String CUSTOMER_ERROR_UNKNOWN = "ERROR-CUSTOMER-0000";
    public static final String CUSTOMER_ERROR_EXIST = "ERROR-CUSTOMER-0002";
    public static final String CUSTOMER_ERROR_UPDATE = "ERROR-CUSTOMER-0003";
    public static final String CUSTOMER_ERROR_NOT_FOUND = "ERROR-CUSTOMER-0004";


    /**
     * Starting error code NATION
     * */
    public static final String NATION_ERROR_NOT_FOUND = "ERROR-NATION-0000";
    public static final String NATION_ERROR_EXIST = "ERROR-NATION-0001";
    public static final String NATION_ERROR_NOT_PARENT = "ERROR-NATION-0002";
    public static final String NATION_ERROR_NOT_PARENT_PROVINCE = "ERROR-NATION-0003";
    public static final String NATION_ERROR_NOT_PARENT_DISTRICT = "ERROR-NATION-0004";
    public static final String NATION_ERROR_INVALID_KIND = "ERROR-NATION-0005";

    /**
     * Starting error code BRANCH
     * */
    public static final String BRANCH_ERROR_NOT_FOUND = "ERROR-BRANCH-0000";
    public static final String BRANCH_ERROR_EXIST = "ERROR-BRANCH-0001";

    /**
     * Starting error code GROUP
     * */
    public static final String GROUP_ERROR_NOT_FOUND = "ERROR-GROUP-0000";
    public static final String GROUP_ERROR_CANT_DELETE = "ERROR-GROUP-0001";

    /**
     * Starting error code EMPLOYEE
     * */
    public static final String EMPLOYEE_ERROR_NOT_FOUND = "ERROR-EMPLOYEE-0000";
    public static final String EMPLOYEE_ERROR_EXISTED = "ERROR-EMPLOYEE-0001";

    /**
     * Starting error code DATABASE_ERROR
     *
     */
    public static final String  ERROR_DB_QUERY = "ERROR-DB-QUERY-0000";

    /**
     * Starting error code FILE
     */
    public static final String FILE_ERROR_UPLOAD_TYPE_INVALID = "ERROR-FILE_0000";
}
