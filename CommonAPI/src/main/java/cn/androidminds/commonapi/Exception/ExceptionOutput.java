package cn.androidminds.commonapi.Exception;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionOutput {
    public static String toString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw, true));
        return sw.toString();
    }
}
