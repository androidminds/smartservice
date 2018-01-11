package cn.androidminds.jwtservice;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtServiceTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private TestRestTemplate template = new TestRestTemplate();
    private int port = 9300;

    @Value("${server.port}")
    int pt;

    String login(String name, String password) throws Exception{
        String url = "http://localhost:"+port+"/auth/login";

        LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("identity", "root");
        multiValueMap.add("password", "123456");

        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .params(multiValueMap))
                //判断返回值
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getContentAsString();
    }
    @Test
    public void testLogin() throws Exception{
       login("root", "123456");
    }

    @Test
    public void testRefresh() throws Exception{
        String token = login("root", "123456");

        String url = "http://localhost:"+port+"/auth/refresh";
        LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("old-token", token);

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .params(multiValueMap))
                //判断返回值
                .andExpect(status().isOk());
    }

    @Test
    public void testGetPubKey() throws Exception{
        String url = "http://localhost:"+port+"/auth/public-key";
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isOk());
    }
}
