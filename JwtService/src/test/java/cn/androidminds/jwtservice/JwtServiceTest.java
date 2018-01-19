package cn.androidminds.jwtservice;

import cn.androidminds.jwtserviceapi.domain.JwtInfo;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.domain.UserState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.ribbon.proxy.annotation.Http;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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


    String login(String name, String password) throws Exception{
        String url = "/login";

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
        String url = "/refresh";
        JwtInfo jwtInfo = new JwtInfo("root");
        ObjectMapper mapper = new ObjectMapper();

        MvcResult result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(jwtInfo)))
                //判断返回值
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void testGetPubKey() throws Exception{
        String url = "/public-key";
        MvcResult result = mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isOk())
                .andReturn();
        String key = result.getResponse().getContentAsString();
        assert(key.length() > 200);
    }
}
