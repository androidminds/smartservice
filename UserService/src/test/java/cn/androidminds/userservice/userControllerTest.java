package cn.androidminds.userservice;

import cn.androidminds.userservice.domain.Role;
import cn.androidminds.userserviceapi.domain.UserInfo;
import cn.androidminds.userserviceapi.domain.UserState;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.jayway.jsonpath.JsonPath;
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

    private String nameBase = "user";
    private String password = "123456";

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    void addUser(int count) throws Exception{
        String url = "/users";

        for(int i = 0; i < count; i++) {
            UserInfo userInfo = new UserInfo(0L, nameBase+i,
                    password, nameBase+i+"@test.com",
                    "136914326"+(100+i), Role.NORMAL,
                    UserState.ACTIVED, "root");
            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(userInfo)))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.id", is(i+2)));
        }
    }

    @Test
    public void testAddUser() throws Exception
    {
        addUser(5);
    }

    @Test
    public void testAddUserTransaction() throws Exception{
        AsyncRestTemplate asyncTemplate = new AsyncRestTemplate();

        int port = 9030;
        String url = "http://localhost:"+port+"/users";

        for(int i = 0; i < 50; i++) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            ObjectMapper objectMapper= new ObjectMapper();
            UserInfo userInfo = new UserInfo(0L, "tom",
                    "123456", "tom@test.com",
                    "13432346237", Role.NORMAL,
                    UserState.ACTIVED, "root");
            HttpEntity<String> formEntity = new HttpEntity<String>(objectMapper.writeValueAsString(userInfo), headers);
            asyncTemplate.postForLocation(url, formEntity);
        }
        System.out.println("");
    }
/*
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
*/
    @Test
    public void testListUser() throws Exception{
        int count = 1;
        addUser(count);

        String url = "/users";
        LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.add("page", "0");
        multiValueMap.add("page-count", "20");

        MvcResult result = mockMvc.perform(get(url)
                .params(multiValueMap))
                //判断返回值
                .andReturn();

        int status = result.getResponse().getStatus();
        assert(status == HttpStatus.OK.value() || status == HttpStatus.NO_CONTENT.value());
        if(status == HttpStatus.NO_CONTENT.value()) return;

        ObjectMapper objectMapper = new ObjectMapper();
        JSONArray jsonArray = new JSONArray(result.getResponse().getContentAsString());
        assert(jsonArray.length() == count+1);
        for(int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = (JSONObject) jsonArray.get(i);
            UserInfo userInfo = objectMapper.readValue(object.toString(), UserInfo.class);
            assert(userInfo.getId() == i+1);
        }
    }


    @Test
    public void testGetInfo() throws Exception{
        int count = 3;
        addUser(count);

        String url = "/users/{id}";

        for (int i = 0; i < count; i++) {
            mockMvc.perform(get(url, i+1)
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.id", is(i+1)));
        }
    }

    @Test
    public void testModify() throws Exception{
        int count = 3;
        addUser(count);

        String url = "/users/{id}";

        for (int i = 0; i < count; i++) {
            UserInfo userInfo = new UserInfo(0L, nameBase+i, "222222", nameBase+i+"@abc.com", "136914326"+(100+i), Role.NORMAL, UserState.ACTIVED, "root");
            ObjectMapper mapper = new ObjectMapper();

            mockMvc.perform(put(url, i+2)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(userInfo)))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));


            mockMvc.perform(get(url, i+2)
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    //判断返回值
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.id", is(i+2)))
                    .andExpect(jsonPath("$.email", is(nameBase+i+"@abc.com")));
        }
    }

    @Test
    public void testDelete() throws Exception{
        int count = 3;
        addUser(count);

        String url = "/users/{id}";
        int id = 2;
        mockMvc.perform(delete(url, id)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isOk());

        mockMvc.perform(get(url, id)
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                //判断返回值
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testVerify() throws Exception{
        int count = 3;
        addUser(count);

        String url = "/verify";

        for (int i = 0; i < count; i++) {
            LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("identity", nameBase+i);
            multiValueMap.add("password", password); //right password

            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .params(multiValueMap))
                    //判断返回值
                    .andExpect(status().isOk());
        }

        for (int i = 0; i < count; i++) {
            LinkedMultiValueMap<String,String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.add("identity", nameBase+i);
            multiValueMap.add("password", "12345"); //wrong password

            mockMvc.perform(get(url)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .params(multiValueMap))
                    //判断返回值
                    .andExpect(status().isBadRequest());
        }
    }
}
