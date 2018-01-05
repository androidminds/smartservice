package cn.androidminds.userserviceapi.Error;

import cn.androidminds.commonapi.rest.BaseErrorCode;

public class ErrorCode extends BaseErrorCode {

    public final static int ERR_PARAM_NAME_NULL = 100;
    public final static int ERR_PARAM_PASSWORD_NULL = 101;
    public final static int ERR_PARAM_PASSWORD_WEAK = 102;
    public final static int ERR_PARAM_NAME_EXISTED = 103;
    public final static int ERR_PARAM_EMAIL_EXISTED = 104;
    public final static int ERR_PARAM_PHONENUMBER_EXISTED = 105;
    public static final int ERR_PARAM_NAME_NOT_EXISTED = 106;
    public static final int ERR_PARAM_ID_NOT_EXISTED = 107;
    public static final int EXCCUTE_DELETE_FAIL = 108;
    public static final int EXECUTE_ADD_USER_FAIL = 109;
    public static final int PAGE_COUNT_EXCESS_LIMIT = 110;
}
