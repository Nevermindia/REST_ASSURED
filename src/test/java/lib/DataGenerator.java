package lib;

import com.github.javafaker.Faker;

import java.util.HashMap;
import java.util.Map;

public class DataGenerator {
    public static Map<String, String> getRegistrationData() {
        Faker faker = new Faker();
        Map<String, String> defaultUserData = new HashMap<>();
        defaultUserData.put("username", "learnqa");
        defaultUserData.put("firstName", "123");
        defaultUserData.put("lastName", "learnqa");
        defaultUserData.put("email", faker.internet().emailAddress());
        defaultUserData.put("password", "learnqa");
        return defaultUserData;
    }

    public static Map<String, String> getRegistrationData(Map<String, String> nonDefaultValues) {
        Faker faker = new Faker();
        Map<String, String> defaultValues = DataGenerator.getRegistrationData();
        Map<String, String> userData = new HashMap<>();
        String[] keys = {"username", "firstName", "lastName", "email", "password"};
        for (String key :
                keys) {
            if (nonDefaultValues.containsKey(key)) {
                userData.put(key, nonDefaultValues.get(key));
            } else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }
}
