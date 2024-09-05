package lib;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Assertions {
    @Step("Assert Json has name")
    public static void assertJsonByName(Response response, String name, int expectedValue) {
        response.then().assertThat().body("$", hasKey(name));
        int value = response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "Actual value is not equal to expected");
    }
    @Step("Assert Json has name")
    public static void assertJsonByName(Response response, String name, String expectedValue) {
        response.then().assertThat().body("$", hasKey(name));
        String value = response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "Actual value is not equal to expected");
    }

    @Step("Assert response text equals to actual text")
    public static void assertResponseTextEquals(Response response, String expectedText) {
        assertEquals(expectedText, response.asString(), "Response text is not expected");
    }

    @Step("Assert response code equals to expected code")
    public static void assertResponseCodeEquals(Response response, int expectedCode) {
        assertEquals(expectedCode, response.statusCode(), "Response code is not expected");
    }

    @Step("Assert Json has a field")
    public static void assertResponseJsonHasField(Response response, String expectedJsonField) {
        response.then().assertThat().body("$", hasKey(expectedJsonField));
    }

    @Step("Assert Json has not field")
    public static void assertResponseJsonHasNotField(Response response, String unexpectedJsonField) {
        response.then().assertThat().body("$", not(hasKey(unexpectedJsonField)));
    }

    @Step("Assert Json has fields")
    public static void assertResponseJsonHasFields(Response response, String[] expectedJsonFields) {
        for (String expectedField :
                expectedJsonFields) {
            response.then().assertThat().body("$", hasKey(expectedField));
        }
    }
    @Step("Assert Json doesn't have fields")
    public static void assertResponseJsonHasNotFields(Response response, String[] unExpectedJsonFields) {
        for (String unExpectedJsonField :
                unExpectedJsonFields) {
            response.then().assertThat().body("$", not(hasKey(unExpectedJsonField)));
        }
    }
}
