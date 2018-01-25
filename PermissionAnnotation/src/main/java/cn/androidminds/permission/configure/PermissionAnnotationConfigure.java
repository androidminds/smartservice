package cn.androidminds.permission.configure;

import cn.androidminds.permission.PermissionScanListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class PermissionAnnotationConfigure {

    @Bean
    public PermissionScanListener getPermissionScanListener() {
        return new PermissionScanListener();
    }
}
