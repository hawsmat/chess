package server;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import model.*;

public class ServerFacade {
    private HttpClient client = HttpClient.newHttpClient();
    private String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public LoginResult register(UserData userData) throws Exception {
        HttpRequest request = buildRequest("POST", "/user", null, userData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public LoginResult login(LoginData loginData) throws Exception {
        HttpRequest request = buildRequest("POST", "/session", null, loginData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, LoginResult.class);
    }

    public void logout(String authToken) throws Exception {
        Map<String, String> headers = Map.of("authorization", authToken);
        HttpRequest request = buildRequest("DELETE", "/session", headers, null);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, String.class);
    }

    public void createGame(CreateGameData createGameData) throws Exception{
        Map<String, String> headers = Map.of("authorization", createGameData.authToken());
        Map<String, Object> body = Map.of("gameName", createGameData.gameName());
        HttpRequest request = buildRequest("POST", "/game", headers, body);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public GameLists listGames(String authToken) throws Exception {
        Map<String, String> headers = Map.of("authorization", authToken);
        HttpRequest request = buildRequest("GET", "/game", headers, null);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, GameLists.class);
    }

    public void join(JoinGameData joinGameData) throws Exception {
        Map<String, String> headers = Map.of("authorization", joinGameData.authToken());
        Map<String, Object> body = Map.of("authToken", joinGameData.authToken(),"playerColor", joinGameData.playerColor(), "gameID", joinGameData.gameID());
        HttpRequest request = buildRequest("PUT", "/game", headers, body);
        HttpResponse<String> response = sendRequest(request);
        handleResponse(response, null);
    }

    public void observe() throws Exception {}
    private HttpRequest buildRequest(String method, String path, Map<String, String> headers, Object body) {
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
            String requestString = new Gson().toJson(request);
            return HttpRequest.BodyPublishers.ofString(requestString);
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
        String body = response.body();
        if (status/100 != 2) {
            if (status == 401) {
                throw new Exception("You are not authorized.");
            }
            else if (status == 403) {
                throw new Exception("That is already taken.");
            }
            else {
                throw new Exception("There was an internal error.");
            }
        }
        if (responseClass != null && body != null && !body.isBlank()) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }
}
