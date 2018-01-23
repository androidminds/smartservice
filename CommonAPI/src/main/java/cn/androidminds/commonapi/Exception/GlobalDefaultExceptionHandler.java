package cn.androidminds.commonapi.Exception;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalDefaultExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(HttpServletRequest req, Exception e) {
    	logger.error("GlobalDefaultExceptionHandler: " + ExceptionOutput.toString(e));
    }
}
