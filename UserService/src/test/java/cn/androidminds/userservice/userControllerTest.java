package cn.androidminds.userservice;

import cn.androidminds.userservice.domain.Role;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.domain.UserState;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.WebApplicationContext;


import java.util.ArrayList;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class userControllerTest {

    private AsyncRestTemplate asyncTemplate = new AsyncRestTemplate();
    private TestRestTemplate template = new TestRestTemplate();
    private int port = 9200;

    private String nameBase = "user";
    private String password = "123456";

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    String rootToken;

    @Before
    public void setupMockMvc() {
        //mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        login();
    }

    public void login(){
        String url = "http://localhost:9300/auth/login";
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("identity", "root");
        map.add("password", "123456");
        ResponseEntity<String> result = template.postForEntity(url, map, String.class);
        assert(result.getStatusCode() == HttpStatus.OK);
        rootToken = result.getBody().toString();
    }

    void addUser(int count) throws Exception{
        String url = "http://localhost:"+port+"/users";

        for(int i = 0; i < count; i++) {
            HttpHeaders headers = new HttpHeaders();
            MediaType type2 = MediaType.parseMediaType("application/json; charset=UTF-8");
            headers.setContentType(type2);
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());

            JSONObject jsonObj = new JSONObject();

            try {
                jsonObj.put("id", 0);
                jsonObj.put("name", nameBase+i);
                jsonObj.put("password", password);
                jsonObj.put("email", nameBase+i+"@test.com");
                jsonObj.put("phoneNumber", "136914326"+(100+i));
                jsonObj.put("state", UserState.ACTIVED);
                jsonObj.put("role", Role.NORMAL);
                jsonObj.put("creatorToken", rootToken);
            } catch (JSONException e) {
                assert (false);
                return;
            }
            HttpEntity<String> formEntity = new HttpEntity<String>(jsonObj.toString(), headers);

            ResponseEntity<UserInfo> result = template.postForEntity(url, formEntity, UserInfo.class);
            assert(result.getStatusCode() == HttpStatus.OK);
            assert(result.getBody().getId() == i+2);
        }
    }


    @Test
    public void testAddUser() throws Exception
    {
        addUser(10);
    }

    @Test
    public void testListUser() throws Exception{
        int count = 3;
        addUser(count);

        String url = "http://localhost:"+port+"/users?start={start}&count={count}";

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authentication", rootToken);
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<UserInfo[]> response = template.exchange(url, HttpMethod.GET, requestEntity, UserInfo[].class, 1, count);

        assert(response.getStatusCode() == HttpStatus.OK);
        UserInfo[] userInfos = response.getBody();
        assert(userInfos != null);
        for(int i = 0; i < userInfos.length; i++) {
            assert(userInfos[i].getId() == i+1);
        }

    }

    @Test
    public void testListUser1() throws Exception{
        int count = 1;
        addUser(count);

        String url = "http://localhost:"+port+"/users";
        LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("start", "1");
        multiValueMap.add("count", (new Integer(count)).toString());
        multiValueMap.add("creator-token", rootToken);

        MvcResult result = mockMvc.perform(get(url)
                .params(multiValueMap))
                //判断返回值
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper=new ObjectMapper();
        JSONObject jsonObj = new JSONObject(result.getResponse().getContentAsString());
        JSONArray jsonArray = jsonObj.getJSONArray("data");
        assert(jsonArray.length() == count);
        for(int i = 0; i < count; i++) {
            JSONObject object = (JSONObject) jsonArray.get(i);
            UserInfo userInfo = objectMapper.readValue(object.toString(), UserInfo.class);
            assert(userInfo.getId() == i+1);
        }
    }

    @Test
    public void testAddUserTransaction(){
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
    public void testGetInfo() throws Exception{
        int count = 3;
        addUser(count);

        String url = "http://localhost:"+port+"/users/{id}";

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get(url, i+1)
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.data.id", is(i+1)));
        }
    }

    @Test
    public void testModify() throws Exception{
        int count = 3;
        addUser(count);

        String url = "http://localhost:"+port+"/users/{id}";

        for (int i = 0; i < count; i++) {
            UserInfo userInfo = new UserInfo(0L, nameBase+i, "222222", nameBase+i+"@abc.com", "136914326"+(100+i), Role.NORMAL, UserState.ACTIVED, null);
            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(put(url, i+1)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(userInfo)))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));


            mockMvc.perform(get(url, i+1)
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.data.id", is(i+1)))
                    .andExpect(jsonPath("$.data.email", is(nameBase+i+"@abc.com")));
        }
    }

    @Test
    public void testDelete() throws Exception{
        int count = 3;
        addUser(count);

        String url = "http://localhost:"+port+"/users/{id}";
        int id = 2;
        mockMvc.perform(delete(url, id)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

        mockMvc.perform(get(url, id)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void testVerify() throws Exception{
        //int count = 3;
        //addUser(count);

        String url = "http://localhost:"+port+"/verify";

        for (int i = 0; i < 1; i++) {
            LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("identity", "root");
            multiValueMap.add("password", "123456"); //right password

            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .params(multiValueMap))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }

        for (int i = 0; i < 1; i++) {
            LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("identity", nameBase+i);
            multiValueMap.add("password", "12345"); //wrong password

            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .params(multiValueMap))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
        }
    }
}
