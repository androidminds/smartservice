package cn.androidminds.userservice;

import cn.androidminds.userservice.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class userControllerTest {

    private TestRestTemplate template = new TestRestTemplate();
    private int port = 9200;

    @Test
    public void testInfo(){
        String url = "http://localhost:"+port+"/user/info";
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("identity", "admin");

        User result = template.getForObject(url+"/{identity}", User.class, "admin");
        assert(result != null);
        assert(result.getPassword() == null);
        assert("admin".equals(result.getName()));
    }

}
