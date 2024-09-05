package tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {
    Faker faker = new Faker();
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @DisplayName("Creating user with existing email")
    @Description("This test checks creating user with email that already exists")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        Assertions.assertResponseTextEquals(response, "Users with email '" + email + "' already exists");
        Assertions.assertResponseCodeEquals(response, 400);
    }

    @Test
    @DisplayName("Successful creating user")
    @Description("This test checks successful creating a new user")
    public void testCreateUserSuccessfully() {
        String email = faker.internet().emailAddress();
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);
        Assertions.assertResponseJsonHasField(response, "id");
        Assertions.assertResponseCodeEquals(response, 200);
    }

    @Test
    @DisplayName("Creating user with incorrect email")
    @Description("This test check creating user with incorrect emailL without  '@'")
    public void testCreatingUserWithIncorrectEmail() {
        String email = faker.internet().emailAddress().replace("@", "");
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        Assertions.assertResponseTextEquals(response, "Invalid email format");
        Assertions.assertResponseCodeEquals(response, 400);
    }

    @ParameterizedTest(name = "Creating user without {0}")
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"} )
    @DisplayName("Creating user without one of the necessary fields")
    @Description("This test check creating user without one of the fields")
    public void testCreatingUserWithoutOneOfTheFields(String missingField) {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.remove(missingField);
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        Assertions.assertResponseTextEquals(response, "The following required params are missed: " + missingField);
        Assertions.assertResponseCodeEquals(response, 400);
    }

    @Test
    @DisplayName("Creating user with very short name")
    @Description("This test check creating user with too short name: 1 character")
    public void testCreatingUserWithTooShortName() {
        String firstName = faker.name().firstName().substring(0,1);
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData = DataGenerator.getRegistrationData(userData);
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too short");
        Assertions.assertResponseCodeEquals(response, 400);
    }

    @Test
    @DisplayName("Creating user with very short name")
    @Description("This test check creating user with too short name: 1 character")
    public void testCreatingUserWithTooLongName() {
        String firstName = RandomStringUtils.random(251, true, false);
        Map<String, String> userData = new HashMap<>();
        userData.put("firstName", firstName);
        userData = DataGenerator.getRegistrationData(userData);
        Response response = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/",
                        userData);
        Assertions.assertResponseTextEquals(response, "The value of 'firstName' field is too long");
        Assertions.assertResponseCodeEquals(response, 400);
    }
}
