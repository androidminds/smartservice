package cn.androidminds.jwttokenservice;

import io.jsonwebtoken.lang.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtTokenServiceTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testLogin() throws Exception{
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
        Assert.hasText(content().toString());
    }

    @Test
    public void testRefresh() throws Exception{
        String url = "/refresh-token";

        mockMvc.perform(post(url)
                .header("userName", "root")
                .header("userId", "1"))
                //判断返回值
                .andExpect(status().isOk())
                .andReturn();
        Assert.hasText(content().toString());
    }

    @Test
    public void testGetPubKey() throws Exception{
        String url = "/public-key";
        mockMvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isOk())
                .andReturn();
        Assert.hasText(content().toString());
    }
}
