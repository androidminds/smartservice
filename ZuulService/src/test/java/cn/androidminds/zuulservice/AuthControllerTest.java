package cn.androidminds.zuulservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthControllerTest {

    private TestRestTemplate template = new TestRestTemplate();
    private int port = 8080;

    @Test
    public void testLogin(){
        String url = "http://localhost:"+port+"/auth/login";
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("identity", "admin");
        map.add("password", "123456");
        String result = template.postForObject(url, map, String.class);
        System.out.println(result);
    }

}
