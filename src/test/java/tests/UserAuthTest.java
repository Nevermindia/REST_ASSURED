package tests;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;

@Epic("Authorization cases")
@Feature("Authorization")
public class UserAuthTest extends BaseTestCase {
    Cookie authCookie;
    Header authHeader;
    int userIdFromLogin;
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    @Description("Login in user with correct credentials")
    @DisplayName("Login in user")
    public void loginUser() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "vinkotov@example.com");
        credentials.put("password", "1234");
        Response responseLogin = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", credentials);
        this.authCookie = this.getCookie(responseLogin, "auth_sid");
        this.authHeader = this.getHeader(responseLogin, "x-csrf-token");
        this.userIdFromLogin = this.getIntFromJson(responseLogin, "user_id");
        assertTrue(this.userIdFromLogin > 0, "User Id should be greater then 0");
    }


    @Test
    @Description("This test successfully authorize by email and password")
    @DisplayName("Test positive auth user")
    public void successfulLoginTest() {
        Response responseAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.authHeader, this.authCookie);
        Assertions.assertJsonByName(responseAuth, "user_id", this.userIdFromLogin);

    }

    @ParameterizedTest
    @Description("This test checks authorization status w/o sending auth cookie or token")
    @DisplayName("Test negative auth user")
    @ValueSource(strings = {"cookie", "header"})
    public void testNegativeAuthUser(String condition) {
        if (condition.equals("cookie")) {
            Response responseAuth = apiCoreRequests
                    .makeGetRequestWithCookie("https://playground.learnqa.ru/api/user/auth",
                            this.authCookie);
            Assertions.assertJsonByName(responseAuth, "user_id", 0);
        } else if (condition.equals("header")) {
            Response responseAuth = apiCoreRequests
                    .makeGetRequestWithToken("https://playground.learnqa.ru/api/user/auth",
                            this.authHeader);
            Assertions.assertJsonByName(responseAuth, "user_id", 0);
        } else {
            throw new IllegalArgumentException("Wrong condition value");
        }
    }
}
