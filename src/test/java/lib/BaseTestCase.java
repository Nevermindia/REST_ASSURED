package lib;

import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTestCase {
    protected Header getHeader(Response response, String name) {
        assertTrue(response.getHeaders().hasHeaderWithName(name),
                "Response doesnt have header with name " + name);
        return response.getHeaders().get("x-csrf-token");
    }

    protected Cookie getCookie(Response response, String name) {
        assertTrue(response.getCookies().containsKey("auth_sid"),
                "Response doesnt have cookie with name " + name);
        return response.getDetailedCookie("auth_sid");
    }

    protected int getIntFromJson(Response response, String name) {
        response.then().assertThat().body("$", hasKey(name));
        return response.jsonPath().getInt(name);
    }
}
