package server;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import model.UserData;

public class ServerFacade {
    private HttpClient client = HttpClient.newHttpClient();
    private String serverUrl;
    public ServerFacade(String url) {
        serverUrl = url;
    }

    public UserData register(UserData userData) throws Exception {
        HttpRequest request = buildRequest("POST", "/userData", userData);
        HttpResponse<String> response = sendRequest(request);
        return handleResponse(response, UserData.class);
    }
    public void login() throws Exception {}
    public void logout() throws Exception {}
    public void create() throws Exception{}
    public void list() throws Exception {}
    public void join() throws Exception {}
    public void observe() throws Exception {}
    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
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
