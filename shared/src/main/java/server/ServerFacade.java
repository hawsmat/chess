package server;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import model.CreateGameData;
import model.JoinGameData;
import model.LoginData;
import model.UserData;

public class ServerFacade {
    private HttpClient client = HttpClient.newHttpClient();
    private String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData register(UserData userData) throws Exception {
        HttpRequest request = buildRequest("POST", "/user", null, userData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, UserData.class);
    }
    public LoginData login(LoginData loginData) throws Exception {
        HttpRequest request = buildRequest("POST", "/session", null, loginData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LoginData.class);
    }
    public String logout(String authToken) throws Exception {
        Map<String, String> headers = Map.of("authorization", authToken);
        HttpRequest request = buildRequest("DELETE", "/session", headers, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, String.class);
    }
    public CreateGameData createGame(CreateGameData createGameData) throws Exception{
        Map<String, String> headers = Map.of("authorization", createGameData.authToken());
        HttpRequest request = buildRequest("POST", "/game", headers, createGameData.gameName());
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, CreateGameData.class);
    }
    public String listGames(String authToken) throws Exception {
        Map<String, String> headers = Map.of("authorization", authToken);
        HttpRequest request = buildRequest("POST", "/game", headers, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, CreateGameData.class);
    }
    public JoinGameData join(JoinGameData joinGameData) throws Exception {
        Map<String, String> headers = Map.of("authorization", joinGameData.authToken());
        Map<String, Object> body = Map.of("PlayerColor", joinGameData.playerColor(), "gameID", joinGameData.gameID());
        HttpRequest request = buildRequest("POST", "/game", headers, body);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, JoinGameData.class);
    }
    public void observe() throws Exception {}
    private HttpRequest buildRequest(String method, String path, Object headers, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (headers != null) {
            for (var entry : headers.entrySet()){
                request.header(entry.getKey(), entry.getValue());
            }
        }
        if (body != null) {
            request.header("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws Exception {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws Exception {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new Exception();
            }
            throw new Exception();
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
