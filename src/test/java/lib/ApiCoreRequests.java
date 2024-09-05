package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {
    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, Header token, Cookie cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(token)
                .cookie(cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request")
    public Response makeGetRequest(String url) {
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, Cookie cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie(cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, Header token) {
        return given()
                .filter(new AllureRestAssured())
                .header(token)
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String, String> data) {
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-request with token and cookie")
    public Response makePutRequest(String url, Map<String, String> data, Header token, Cookie cookie) {
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .header(token)
                .cookie(cookie)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request with token and cookie")
    public Response makePutRequest(String url, Map<String, String> data) {
        return given()
                .filter(new AllureRestAssured())
                .body(data)
                .put(url)
                .andReturn();
    }
}
