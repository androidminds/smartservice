package cn.androidminds.permission;

import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Configuration
@Component
public class PermissionScanListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("PermissionScanListenter ...");
    }
    public PermissionScanListener() {
        System.out.println("PermissionScanListenter ...");
    }

}