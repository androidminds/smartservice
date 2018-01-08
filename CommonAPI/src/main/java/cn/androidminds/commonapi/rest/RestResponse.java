package cn.androidminds.commonapi.rest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RestResponse<T> extends StatusResponse{
    T  data;

    public RestResponse (T data, int status) {
        this.data = data;
        this.statusCode = status;
    }

    public RestResponse<T> setData(T data) {
        this.data = data;
        return this;
    }

    public RestResponse<T> setStatusCode(int code) {
        super.setStatusCode(code);
        return this;
    }
}
