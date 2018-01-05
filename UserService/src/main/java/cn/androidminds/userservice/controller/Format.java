package cn.androidminds.userservice.controller;

import cn.androidminds.userserviceapi.Error.ErrorCode;

public class Format {
    public static int checkUserName(String name) {
        return ErrorCode.OK;
    }

    public static int checkPassword(String password) {
        return ErrorCode.OK;
    }

    public static int checkEmail(String email) {
        return ErrorCode.OK;
    }

    public static int checkPhoneNumber(String phonenumber) {
        return ErrorCode.OK;
    }


}
