import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RestAssuredTest {
    @Test
    public void testRestAssured() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John");
        JsonPath response = RestAssured.given().queryParams(params).get("https://playground.learnqa.ru/api/hello").jsonPath();
        String answer = response.get("answer2");
    }

    @Test
    public void testRestAssuredCheckType() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John");
        Response response = RestAssured.given().queryParam("param1", "value").queryParam("param2", "value").get("https://playground.learnqa.ru/api/check_type").andReturn();
        response.prettyPrint();
        System.out.println(response.statusCode());
    }

    /*
    В рамках этой задачи нужно создать тест, который будет делать GET-запрос
    на адрес https://playground.learnqa.ru/api/get_json_homework
Полученный JSON необходимо распечатать и изучить. Мы увидим, что это данные с сообщениями и временем,
когда они были написаны. Наша задача вывести текст второго сообщения.
     */
    @Test
    public void testGetJsonHw() {
        JsonPath response = RestAssured.get("https://playground.learnqa.ru/api/get_json_homework").jsonPath();
        response.prettyPrint();
        String secondMessage = response.get("messages.message[1]");
        System.out.println(secondMessage);
    }


    /*
    Необходимо написать тест, который создает GET-запрос на адрес: https://playground.learnqa.ru/api/long_redirect
С этого адреса должен происходит редирект на другой адрес.
Наша задача — распечатать адрес, на который редиректит указанные URL.
     */
    @Test
    public void testGetUrlForRedirect() {
        Response response = RestAssured.given().redirects().follow(false).get("https://playground.learnqa.ru/api/long_redirect").andReturn();
        String locationHeader = response.getHeader("Location");
        System.out.println(locationHeader);
    }

    /*
    Необходимо написать тест, который создает GET-запрос на адрес из предыдущего задания: https://playground.learnqa.ru/api/long_redirect
На самом деле этот URL ведет на другой, который мы должны были узнать на предыдущем занятии. Но этот другой URL тоже куда-то редиректит.
И так далее. Мы не знаем заранее количество всех редиректов и итоговый адрес.
Наша задача — написать цикл, которая будет создавать запросы в цикле, каждый раз читая URL для редиректа из нужного заголовка.
И так, пока мы не дойдем до ответа с кодом 200.
     */
    @Test
    public void testLongRedirect() {
        Response response;
        String location = "https://playground.learnqa.ru/api/long_redirect";
        do {
            response = RestAssured.given().redirects().follow(false).get(location).andReturn();
            location = response.getHeader("Location");
            System.out.println("Наапрвление редиректа: " + location + " " + "Код ответа: " + response.getStatusCode());
        } while (response.getStatusCode() != 200);
    }

    /*
    Иногда API-метод выполняет такую долгую задачу, что за один HTTP-запрос от него нельзя сразу получить готовый ответ.
    Это может быть подсчет каких-то сложных вычислений или необходимость собрать информацию по разным источникам.
В этом случае на первый запрос API начинает выполнения задачи, а на последующие ЛИБО говорит, что задача еще не готова, ЛИБО выдает результат.
Сегодня я предлагаю протестировать такой метод.

Сам API-метод находится по следующему URL: https://playground.learnqa.ru/ajax/api/longtime_job
Если мы вызываем его БЕЗ GET-параметра token, метод заводит новую задачу, а в ответ выдает нам JSON со следующими полями:
* seconds - количество секунд, через сколько задача будет выполнена
* token - тот самый токен, по которому можно получить результат выполнения нашей задачи
Если же вызвать API-метод, УКАЗАВ GET-параметром token, то мы получим следующий JSON:
* error - будет только в случае, если передать token, для которого не создавалась задача. В этом случае в ответе будет следующая надпись - No job linked to this token
* status - если задача еще не готова, будет надпись Job is NOT ready, если же готова - будет надпись Job is ready
* result - будет только в случае, если задача готова, это поле будет содержать результат

Наша задача - написать тест, который сделал бы следующее:
1) создавал задачу
2) делал один запрос с token ДО того, как задача готова, убеждался в правильности поля status
3) ждал нужное количество секунд с помощью функции Thread.sleep() - для этого надо сделать import time
4) делал бы один запрос c token ПОСЛЕ того, как задача готова, убеждался в правильности поля status и наличии поля result
Как всегда, код нашей программы выкладываем ссылкой на коммит.
     */
    @Test
    public void testLongTimeJob() throws InterruptedException {
        JsonPath response = RestAssured //создание задачи
                .get("https://playground.learnqa.ru/ajax/api/longtime_job").jsonPath();
        response.prettyPrint();
        String token = response.get("token");
        int time = response.get("seconds");

        JsonPath responseBeforeJobIsDone = RestAssured //делаем запрос с токеном до того, как задача готова
                .given().queryParam("token", token).when().get("https://playground.learnqa.ru/ajax/api/longtime_job").jsonPath();
        responseBeforeJobIsDone.prettyPrint();
        String messageBeforeJobIsDone = responseBeforeJobIsDone.get("status");
        assertEquals("Job is NOT ready", messageBeforeJobIsDone); //проверка статуса

        Thread.sleep(time * 1000); //ждем вермя, указанное в time

        JsonPath responseAfterJobIsDone = RestAssured //делаем запрос, когда задача уже готова
                .given().queryParam("token", token).when().get("https://playground.learnqa.ru/ajax/api/longtime_job").jsonPath();
        responseAfterJobIsDone.prettyPrint();
        String messageAfterJobIsDone = responseAfterJobIsDone.get("status");
        assertEquals("Job is ready", messageAfterJobIsDone); //проверяем статус задачи
    }


    /*
    Сегодня к нам пришел наш коллега и сказал, что забыл свой пароль от важного сервиса. Он просит нас помочь ему написать программу, которая подберет его пароль.
Условие следующее. Есть метод: https://playground.learnqa.ru/ajax/api/get_secret_password_homework
Его необходимо вызывать POST-запросом с двумя параметрами: login и password
Если вызвать метод без поля login или указать несуществующий login, метод вернет 500
Если login указан и существует, метод вернет нам авторизационную cookie с названием auth_cookie и каким-то значением.
У метода существует защита от перебора. Если верно указано поле login, но передан неправильный password, то авторизационная cookie все равно вернется. НО с "неправильным" значением,
которое на самом деле не позволит создавать авторизованные запросы. Только если и login, и password указаны верно, вернется cookie с "правильным" значением.
Таким образом используя только метод get_secret_password_homework невозможно узнать, передали ли мы верный пароль или нет.
По этой причине нам потребуется второй метод, который проверяет правильность нашей авторизованной cookie: https://playground.learnqa.ru/ajax/api/check_auth_cookie
Если вызвать его без cookie с именем auth_cookie или с cookie, у которой выставлено "неправильное" значение, метод вернет фразу "You are NOT authorized".
Если значение cookie “правильное”, метод вернет: “You are authorized”
Коллега говорит, что точно помнит свой login - это значение super_admin
А вот пароль забыл, но точно помнит, что выбрал его из списка самых популярных паролей на Википедии (вот тебе и супер админ...).
Ссылка: https://en.wikipedia.org/wiki/List_of_the_most_common_passwords
Искать его нужно среди списка Top 25 most common passwords by year according to SplashData - список паролей можно скопировать в ваш тест вручную или придумать более хитрый способ, если сможете.

Итак, наша задача - написать тест и указать в нем login нашего коллеги и все пароли из Википедии в виде списка. Программа должна делать следующее:
1. Брать очередной пароль и вместе с логином коллеги вызывать первый метод get_secret_password_homework. В ответ метод будет возвращать авторизационную cookie с именем auth_cookie и каким-то значением.
2. Далее эту cookie мы должна передать во второй метод check_auth_cookie. Если в ответ вернулась фраза "You are NOT authorized", значит пароль неправильный. В этом случае берем следующий пароль и все заново.
Если же вернулась другая фраза - нужно, чтобы программа вывела верный пароль и эту фразу.
Ответом к задаче должен быть верный пароль и ссылка на коммит со скриптом.
     */
    @Test
    public void test() { //не решила asString
        String login = "super_admin";
        String[] passwords = {"123456", "123456789", "qwerty", "password", "1234567", "12345678", "12345", "iloveyou", "111111",
                "123123", "abc123", "qwerty123", "1q2w3e4r", "admin", "qwertyuiop", "654321", "555555", "lovely", "7777777", "welcome", "888888", "princess", "dragon", "password1", "123qwe"};
        String responseMessage = "You are NOT authorized";
        for (int i = 0; i < passwords.length; i++) {
            Map<String, String> data = new HashMap<>();
            data.put("login", login);
            data.put("password", passwords[i]);

            Response response = RestAssured
                    .given()
                    .body(data)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String cookie = response.getCookie("auth_cookie");
            Map<String, String> cookies = new HashMap<>();
            cookies.put("auth_cookie", cookie);
            System.out.println(data.get("login") + " " + data.get("password"));
            System.out.println(cookie);
            Response responseAuthCookie = RestAssured
                    .given()
                    .cookies(cookies)
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "Andrew", "Cat"})
    public void helloWithParameters(String name) {
        Map<String, String> params = new HashMap<>();
        if (name != null) {
            params.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/ajax/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0) ? name : "someone";
        assertEquals("Hello, " + expectedName, answer, "The message is not expected");
    }

    /*
    В рамках этой задачи с помощью JUnit необходимо написать тест, который проверяет длину какое-то переменной
    типа String с помощью любого выбранного Вами метода assert.
Если текст длиннее 15 символов, то тест должен проходить успешно. Иначе падать с ошибкой.
     */
    @Test
    public void checkStringLength() {
        String testLine = "kjfhgjrhtrgnffff";
        assertTrue(testLine.length() > 15, "Length of the line is less then 15");
    }

    /*
    Необходимо написать тест, который делает запрос на метод: https://playground.learnqa.ru/api/homework_cookie
Этот метод возвращает какую-то cookie с каким-то значением. Необходимо понять что за cookie
и с каким значением, и зафиксировать это поведение с помощью assert.
     */
    @Test
    public void getCookie() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        assertTrue(response.getCookies().containsKey("HomeWork"), "Response doesnt contains cookie with name HomeWork");
    }

    /*
    Необходимо написать тест, который делает запрос на метод: https://playground.learnqa.ru/api/homework_header
Этот метод возвращает headers с каким-то значением. Необходимо понять что за headers и с каким значением,
и зафиксировать это поведение с помощью assert
     */

    @Test
    public void getHeaders() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        assertTrue(response.getHeaders().hasHeaderWithName("x-secret-homework-header"), "Response doesnt contains header with name x-secret-homework-header");
    }

    /*
    User Agent - это один из заголовков, позволяющий серверу узнавать, с какого девайса и браузера пришел запрос. Он формируется автоматически клиентом, например браузером.
    Определив, с какого девайса или браузера пришел к нам пользователь мы сможем отдать ему только тот контент, который ему нужен.
Наш разработчик написал метод: https://playground.learnqa.ru/ajax/api/user_agent_check
Метод определяет по строке заголовка User Agent следующие параметры:
device - iOS или Android
browser - Chrome, Firefox или другой браузер
platform - мобильное приложение или веб
Если метод не может определить какой-то из параметров, он выставляет значение Unknown.
Наша задача написать параметризированный тест. Этот тест должен брать из дата-провайдера User Agent и ожидаемые значения,
GET-делать запрос с этим User Agent и убеждаться, что результат работы нашего метода правильный - т.е. в ответе ожидаемое значение всех трех полей.
Список User Agent и ожидаемых значений можно найти по этой ссылке: https://gist.github.com/KotovVitaliy/138894aa5b6fa442163561b5db6e2e26
На самом деле метод не всегда работает правильно. Ответом к задаче должен быть список из тех User Agent,
которые вернули неправильным хотя бы один параметр, с указанием того, какой именно параметр неправильный.
     */
    @ParameterizedTest
    @CsvSource(delimiter = '|', textBlock = """
            Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30|Mobile|No|Android
            Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1|Mobile|Chrome|iOS
            Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)|Googlebot|Unknown|Unknown
            Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0|Web|Chrome|No
            Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1|Mobile|No|iPhone
            """
    )
    public void getUserAgent(String value, String expectedPlatform, String expectedBrowser, String expectedDevice) {
        Header userAgent = new Header("User-Agent", value);
        JsonPath response = RestAssured
                .given()
                .header(userAgent)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();
        response.prettyPrint();
        System.out.println(userAgent);
        String actualPlatform = response.get("platform");
        String actualBrowser = response.get("browser");
        String actualDevice = response.get("device");
        assertEquals(expectedPlatform, actualPlatform, "Platform is not expected");
        assertEquals(expectedBrowser, actualBrowser, "Browser is not expected");
        assertEquals(expectedDevice, actualDevice, "Device is not expected");
    }
}



