package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private HttpRequest request;
    private HttpResponse<String> response;
    @LocalServerPort
    private int port;

    @Test
    public void postShouldNotAcceptNullBlankOrSpaceForName() throws IOException, InterruptedException {
        String jsonUser = "{\"login\":null}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(400, response.statusCode());

        jsonUser = "{\"login\":\"\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(400, response.statusCode());

        jsonUser = "{\"login\":\"Login login\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void postShouldNotAcceptIncorrectEmail() throws IOException, InterruptedException {
        String jsonUser = "{\"email\":\"email\", \"login\":\"Login\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void ifNameIsNullOrBlankUseLoginForName() throws IOException, InterruptedException {
        String jsonUser = "{\"email\":\"email@email.ru\", \"login\":\"Login\", \"name\":null}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("\"name\":\"Login\""));
    }

    @Test
    public void birthdateShouldNotInTheFuture() throws IOException, InterruptedException {
        String jsonUser = "{\"email\":\"email@email.ru\", \"login\":\"Login\", \"birthday\":\"2026-01-01\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void postShouldCreateUserAndReturn200() throws IOException, InterruptedException {
        String jsonUser = "{\"email\":\"email@email.ru\", " +
                "\"login\":\"Login\", " +
                "\"name\":\"name\", " +
                "\"birthday\":\"1990-01-01\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        assertEquals(200, response.statusCode());

        String expectedBody = "{\"id\":1," +
                "\"email\":\"email@email.ru\"," +
                "\"login\":\"Login\"," +
                "\"name\":\"name\"," +
                "\"birthday\":\"1990-01-01\"}";

        assertEquals(expectedBody, response.body());
    }

    @Test
    public void putShouldNotApplyIfIdIsIncorrect() throws IOException, InterruptedException {
        String jsonUser = "{\"email\":\"email@email.ru\", \"login\":\"Login\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        String jsonUserToChange = "{\"email\":\"newemail@email.ru\", \"login\":\"Login\"}";
        sendRequest(jsonUserToChange, RequestMethod.PUT);

        jsonUserToChange = "{\"id\":2, \"email\":\"newemail@email.ru\", \"login\":\"Login\"}";
        sendRequest(jsonUserToChange, RequestMethod.PUT);

        sendRequest(RequestMethod.GET);

        String expectedBody = "[{\"id\":1," +
                "\"email\":\"email@email.ru\"," +
                "\"login\":\"Login\"," +
                "\"name\":\"Login\"," +
                "\"birthday\":null}]";

        assertEquals(expectedBody, response.body());
    }

    @Test
    public void putShouldChangeParameters() throws IOException, InterruptedException {
        String jsonUser = "{\"email\":\"email@email.ru\", " +
                "\"login\":\"Login\", " +
                "\"name\":\"name\", " +
                "\"birthday\":\"1990-01-01\"}";
        sendRequest(jsonUser, RequestMethod.POST);

        String userToChange = "{\"id\":1," +
                "\"email\":\"newemail@email.ru\"," +
                "\"login\":\"newLogin\"," +
                "\"name\":\"newname\"," +
                "\"birthday\":\"2020-01-01\"}";
        sendRequest(userToChange, RequestMethod.PUT);

        assertEquals(userToChange, response.body());

        sendRequest(RequestMethod.GET);

        assertEquals("[" + userToChange + "]", response.body());
    }

    private void sendRequest(String jsonString, RequestMethod method) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + port + "/users");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(url)
                .header("Content-Type", "application/json");

        switch (method) {
            case POST -> requestBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonString));
            case PUT -> requestBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonString));
        }

        request = requestBuilder.build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void sendRequest(RequestMethod method) throws IOException, InterruptedException {
        if (method != RequestMethod.GET) {
            throw new IllegalArgumentException("Версия метода для GET-запроса");
        }
        URI url = URI.create("http://localhost:" + port + "/users");

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
