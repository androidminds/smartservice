package cn.androidminds.commonapi.rest;

import lombok.Data;

@Data
public class StatusResponse {
    int statusCode;
    String statusMessage;

    public StatusResponse setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
