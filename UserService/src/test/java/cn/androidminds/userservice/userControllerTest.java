package cn.androidminds.userservice;

import cn.androidminds.userservice.domain.User;
import cn.androidminds.userserviceapi.domain.UserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static jdk.nashorn.internal.objects.Global.getJSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class userControllerTest {

    private AsyncRestTemplate asyncTemplate = new AsyncRestTemplate();
    private TestRestTemplate template = new TestRestTemplate();
    private int port = 9200;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testAddUser() throws Exception
    {
        String url = "http://localhost:"+port+"/users";

        UserInfo userInfo = new UserInfo(0L, "test", "123456", "test@test.com", "13691432631");
        ObjectMapper mapper = new ObjectMapper();

        //调用接口，传入添加的用户参数
        ResultActions mvcAction = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(mapper.writeValueAsString(userInfo)));
                //判断返回值

        MvcResult mvcResult = mvcAction.andReturn();


        String strResult = mvcResult.getResponse().getContentAsString();
        JSONObject jsonObject = null;
        try {
          jsonObject = new JSONObject(strResult);
        } catch (JSONException e1) {
          e1.printStackTrace();
        }

        mvcAction.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
                //使用jsonPath解析返回值，判断具体的内容
                //.andExpect(jsonPath("$.uid", not(0)));
        System.out.println("");
    }

    @Test
    public void testCreateUser() {

        String url = "http://localhost:"+port+"/users";

        for(int i = 0; i < 10; i++) {
            HttpHeaders headers = new HttpHeaders();
            MediaType type2 = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type2);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            JSONObject jsonObj = new JSONObject();

            try {
                jsonObj.put("id", 0);
                jsonObj.put("name", "user"+i);
                jsonObj.put("password", "123456");
                jsonObj.put("email", "user"+i+"@test.com");
                jsonObj.put("phoneNumber", "13691421387"+(100+i));
            } catch (JSONException e) {
                assert (false);
                return;
            }
            HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);

            Long result = template.postForObject(url, formEntity, Long.class);
            assert (result != null);
            assert (result.intValue() > 0);
        }
    }

    @Test
    public void testCreateUserTransaction(){
        String url = "http://localhost:"+port+"/users";

        for(int i = 0; i < 50; i++) {
            HttpHeaders headers = new HttpHeaders();
            MediaType type2 = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type2);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            JSONObject jsonObj = new JSONObject();

            try {
                jsonObj.put("id", 0);
                jsonObj.put("name", "user");
                jsonObj.put("password", "123456");
                jsonObj.put("email", "user@test.com");
                jsonObj.put("phoneNumber", "13691421387");
            } catch (JSONException e) {
                assert (false);
                return;
            }
            HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);

            asyncTemplate.postForLocation(url, formEntity);
        }
        System.out.println("");

    }

    @Test
    public void testGetInfoById(){
        String url = "http://localhost:"+port+"/users/{id}";

        Map<String, Object> uriVariables = new HashMap<String, Object>();
        uriVariables.put("identity", "abc");

        UserInfo result = template.getForObject(url, UserInfo.class, uriVariables);
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
