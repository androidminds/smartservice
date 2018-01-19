package cn.androidminds.zuulservice;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@PropertySource(value ={"classpath:application.yml"})
public class AuthControllerTest {

    private TestRestTemplate template = new TestRestTemplate();

    //@Value("${server.port}")
    private int port = 9090;


    public String login(){
        String url = "http://localhost:"+port+"/auth/login";
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("identity", "root");
        map.add("password", "123456");
        ResponseEntity<String> response = template.postForEntity(url, map, String.class);
        assert(response.getStatusCode() == HttpStatus.OK);
        return response.getBody().toString();
    }

    @Test
    public void testLogin(){
        login();
    }

    @Test
    public void testRefresh() throws Exception {
        String token = login();
        Thread.sleep(1000); // sleep a while to let system can generate a different token.

        String url = "http://localhost:"+port+"/auth/refresh";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", token);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> response = template.postForEntity(url, requestEntity, String.class );
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody() == null);
        String newToken = response.getHeaders().getFirst("authorization");
        assert(newToken != null);
        assert(!newToken.equals(token));

        response = template.postForEntity(url, null, String.class);
        assert(response.getStatusCode() == HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void test1() {
        String url = "http://localhost:"+port+"/auth/test";

        ResponseEntity<String> response = template.getForEntity(url, String.class);
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equalsIgnoreCase("hello"));
    }
}
