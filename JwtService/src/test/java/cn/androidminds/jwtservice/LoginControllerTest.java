package cn.androidminds.jwtservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginControllerTest {

    private TestRestTemplate template = new TestRestTemplate();
    private int port = 9300;

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
