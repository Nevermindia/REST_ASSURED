package tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    Faker faker = new Faker();

    @Test
    @DisplayName("Edit just created user")
    @Description("This test checks editing just created user")
    public void testEditJustCreatedUser(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);

        //Edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/"+userId,
                        editData,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

//        Response responseEditUser = RestAssured
//                .given()
//                .header(this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie(this.getCookie(responseGetAuth, "auth_sid"))
//                .body(editData)
//                .put("https://playground.learnqa.ru/api/user/"+userId)
//                .andReturn();
        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/"+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));
//        Response responseUserData = RestAssured
//                .given()
//                .header(this.getHeader(responseGetAuth, "x-csrf-token"))
//                .cookie(this.getCookie(responseGetAuth, "auth_sid"))
//                .get("https://playground.learnqa.ru/api/user/"+userId)
//                .andReturn();
        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @DisplayName("Edit user without authorize")
    @Description("This test checks editing user without authorizing")
    public void testEditJustUserNotAuthorize(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        String userId = responseCreateAuth.jsonPath().getString("id");


        //Edit
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/"+userId,
                        editData);
        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);
        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/"+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));
        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @DisplayName("Edit user email with incorrect")
    @Description("This test checks editing user email with incorrect one: without '@'")
    public void testChangeEmailWithIncorrectOne(){
        //Generate user
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //Login
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login",
                        authData);

        //Edit
        String newEmail = faker.internet().emailAddress().replace("@", "");
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);
        Response responseEditUser = apiCoreRequests
                .makePutRequest("https://playground.learnqa.ru/api/user/"+userId,
                        editData,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        //Get
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/"+userId,
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"));

        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }
}
