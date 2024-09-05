package tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Test
    @DisplayName("Get user data without authorization")
    @Description("Test check getting user data without authorization")
    public void testGetUserDataNotAuth() {
        Response response = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/2");
        Assertions.assertResponseJsonHasField(response, "username");
        Assertions.assertResponseJsonHasNotField(response, "firstName");
        Assertions.assertResponseJsonHasNotField(response, "lastName");
        Assertions.assertResponseJsonHasNotField(response, "email");
    }

    @Test
    @DisplayName("Get user data with authorization")
    @Description("Test check getting user data with authorization")
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String > userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com" );
        userData.put("password", "1234");
        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData);
        Cookie authCookie = this.getCookie(responseLogin, "auth_sid");
        Header authHeader = this.getHeader(responseLogin, "x-csrf-token");
        Response responseUserData =apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/2",
                        authHeader, authCookie);
        String [] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertResponseJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @DisplayName("Get user data with authorization but not the same user")
    @Description("Test check getting user data with authorization by other user")
    public void testGetUserDetailsAuthAsOtherUser(){
        Map<String, String > userData = new HashMap<>();
        userData.put("email", "vinkotov@example.com" );
        userData.put("password", "1234");
        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", userData);
        Cookie authCookie = this.getCookie(responseLogin, "auth_sid");
        Header authHeader = this.getHeader(responseLogin, "x-csrf-token");
        Response responseUserData =apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/1",
                        authHeader, authCookie);
        Assertions.assertResponseCodeEquals(responseUserData, 200);
        Assertions.assertResponseJsonHasField(responseUserData, "username");
        String [] unExpectedFields = {"firstName", "lastName", "email", "password"};
        Assertions.assertResponseJsonHasNotFields(responseUserData, unExpectedFields);
    }

}
