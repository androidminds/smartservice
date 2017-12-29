package cn.androidminds.userservice;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userserviceapi.domain.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class userServiceTest {

    private TestRestTemplate template = new TestRestTemplate();
    private int port = 9200;

    @Test
    public void testGetInfoById(){
        String url = "http://localhost:"+port+"/user/1";

        Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("id", "1");

        UserInfo result = template.getForObject(url, UserInfo.class);
        assert(result != null);
        assert("admin".equals(result.getName()));
    }

    @Test
    public void testInfo(){
        String url = "http://localhost:"+port+"/user/info?identity={identity}";

        Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("identity", "admin");

        User result = template.getForObject(url, User.class, uriVariables);
        assert(result != null);
        assert(result.getPassword() == null);
        assert("admin".equals(result.getName()));
    }

    @Test
    public void testVerifySuccess(){
        String url = "http://localhost:"+port+"/user/verify?identity={identity}&password={password}";
        Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("identity", "admin");
        uriVariables.put("password", "123456");
        Boolean result = template.getForObject(url, Boolean.class, uriVariables);
        assert(result != null);
        assert(result == true);
    }

    @Test
    public void testVerifyFail(){
        String url = "http://localhost:"+port+"/user/verify?identity={identity}";
        Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("identity", "admin");
        Boolean result = template.getForObject(url, Boolean.class, uriVariables);
        assert(result != null);
        assert(result == false);
    }
}
