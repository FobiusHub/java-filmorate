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
public class FilmControllerTest {
    private final HttpClient client = HttpClient.newHttpClient();
    private HttpRequest request;
    private HttpResponse<String> response;
    @LocalServerPort
    private int port;

    @Test
    public void postShouldNotAcceptNullOrBlankForName() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\": null}";
        sendRequest(jsonFilm, RequestMethod.POST);

        assertEquals(400, response.statusCode());

        jsonFilm = "{\"name\": \"\"}";
        sendRequest(jsonFilm, RequestMethod.POST);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void postShouldNotAccept200LengthsDescription() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\":\"Зеленая миля\", \"description\":" +
                "\"Пол Эджкомб — начальник блока смертников в тюрьме «Холодная гора», " +
                "каждый из узников которого однажды проходит «зеленую милю» по пути к месту казни. " +
                "Пол повидал много заключённых и надзирателей за время работы. " +
                "Однако гигант Джон Коффи, обвинённый в страшном преступлении, " +
                "стал одним из самых необычных обитателей блока.\"}";
        sendRequest(jsonFilm, RequestMethod.POST);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void postShouldNotAcceptReleaseDateEarlierThan28December1895() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\":\"someName\", \"releaseDate\":\"1894-01-01\"}";
        sendRequest(jsonFilm, RequestMethod.POST);
        sendRequest(RequestMethod.GET);
        assertEquals("[]", response.body());
    }

    @Test
    public void postShouldNotAcceptDurationLessThan0() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\":\"someName\", \"duration\":-1}";
        sendRequest(jsonFilm, RequestMethod.POST);

        assertEquals(400, response.statusCode());
    }

    @Test
    public void postShouldCreateFilmAndReturn200() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\":\"someName\"," +
                "\"description\":\"someDescription\"," +
                "\"duration\":20," +
                "\"releaseDate\":\"2000-10-10\"}";
        sendRequest(jsonFilm, RequestMethod.POST);

        assertEquals(200, response.statusCode());

        String expectedBody = "{\"id\":1," +
                "\"name\":\"someName\"," +
                "\"description\":\"someDescription\"," +
                "\"releaseDate\":\"2000-10-10\"," +
                "\"duration\":20}";

        assertEquals(expectedBody, response.body());
    }

    @Test
    public void putShouldNotApplyIfIdIsIncorrect() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\":\"someName\"," +
                "\"description\":\"description\"," +
                "\"releaseDate\":\"2000-10-10\"," +
                "\"duration\":10}";
        sendRequest(jsonFilm, RequestMethod.POST);

        String jsonFilmToChange = "{\"name\":\"newName\"," +
                "\"description\":\"newDescription\"," +
                "\"releaseDate\":\"2010-10-10\"," +
                "\"duration\":100}";
        sendRequest(jsonFilmToChange, RequestMethod.PUT);

        jsonFilmToChange = "{\"id\":2, \"name\":\"newName\"," +
                "\"description\":\"newDescription\"," +
                "\"releaseDate\":\"2010-10-10\"," +
                "\"duration\":100}";
        sendRequest(jsonFilmToChange, RequestMethod.PUT);

        sendRequest(RequestMethod.GET);

        String expectedBody = "[{\"id\":1," +
                "\"name\":\"someName\"," +
                "\"description\":\"description\"," +
                "\"releaseDate\":\"2000-10-10\"," +
                "\"duration\":10}]";

        assertEquals(expectedBody, response.body());
    }

    @Test
    public void putShouldChangeParameters() throws IOException, InterruptedException {
        String jsonFilm = "{\"name\":\"someName\"," +
                "\"description\":\"someDescription\"," +
                "\"duration\":20," +
                "\"releaseDate\":\"2000-10-10\"}";
        sendRequest(jsonFilm, RequestMethod.POST);

        String filmToChange = "{\"id\":1," +
                "\"name\":\"anotherName\"," +
                "\"description\":\"anotherDescription\"," +
                "\"releaseDate\":\"2011-11-11\"," +
                "\"duration\":200}";
        sendRequest(filmToChange, RequestMethod.PUT);

        assertEquals(filmToChange, response.body());

        sendRequest(RequestMethod.GET);

        assertEquals("[" + filmToChange + "]", response.body());
    }

    private void sendRequest(String jsonString, RequestMethod method) throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:" + port + "/films");

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
        URI url = URI.create("http://localhost:" + port + "/films");

        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
