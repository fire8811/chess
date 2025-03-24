package serverfacade;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import exceptions.ResponseException;
import model.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade() {
        serverUrl = "http://localhost:8080";
    }
    public ServerFacade(String url) {serverUrl = url;}

    public void clearServer(){
        var path = "/db";
        this.sendRequest("DELETE", path, null, null, null);
    }

    public RegisterResult registerUser(RegisterRequest request){
        var path = "/user";
        return this.sendRequest("POST", path, request, RegisterResult.class, null);
    }

    public LoginResult loginUser(LoginRequest request) {
        var path = "/session";
        return this.sendRequest("POST", path, request, LoginResult.class, null);
    }

    public void logoutUser(){
        var path = "/session";
        this.sendRequest("DELETE", path, null, null, null);
    }

    public ListResult listGames(ListRequest request){
        var path = "/game";
        Map<String, String> header = new HashMap<>();
        header.put("authorization", request.authToken()); //place the authtoken in a request header instead of the body

        return this.sendRequest("GET", path, null, ListResult.class, header);
    }

    private CreateResult createGame(CreateRequest request){
        var path = "/game";
        Map<String, String> header = new HashMap<>();
        header.put("authorization", request.authToken());

        return this.sendRequest("POST", path, request, CreateResult.class, header);
    }

    private JoinResult joinGame(JoinRequest request){
        var path = "/game";
        Map<String, String> header = new HashMap<>();
        header.put("authorization", request.authToken());

        return this.sendRequest("PUT", path, request, JoinResult.class, header);
    }

    private <T> T sendRequest(String method, String path, Object request, Class<T> responseClass, Map<String, String> header) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (request != null) {
                writeHttpBody(request, http); //write JSON body
            }

            if (header != null) { //add header to request (usually for sending authTokens)
                writeHeader(header, http);
            }

            http.connect();
            throwIfNotSuccessful(http);

            return readFromBody(http, responseClass);

        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(e.getMessage());
        }
    }

    private static void writeHeader(Map<String, String> header, HttpURLConnection http) throws IOException {
        for (var entry: header.entrySet()){
            http.addRequestProperty(entry.getKey(), entry.getValue());
        }
    }

    private static void writeHttpBody(Object request, HttpURLConnection http) throws IOException {
        http.addRequestProperty("Content-Type", "application/json");
        String data = new Gson().toJson(request);

        try (OutputStream reqBody = http.getOutputStream()){
            reqBody.write(data.getBytes());
        }
    }

    private static <T> T readFromBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T responseObject = null;

        if (http.getContentLength() > 0){
            try (InputStream body = http.getInputStream()){
                InputStreamReader reader = new InputStreamReader(body);
                if(responseClass != null){
                    responseObject = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return responseObject;
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException {
        var status = http.getResponseCode();
        if (!successCode(status)){
            try(InputStream responseError = http.getErrorStream()){
                if (responseError != null){
                    var map = new Gson().fromJson(new InputStreamReader(responseError), HashMap.class);
                    String message = map.get("message").toString();

                    throw new ResponseException(message);
                }

                throw new ResponseException(status + ": something bad happened");
            }
        }

    }

    private boolean successCode(int status){
        return status/100 == 2;
    }
}
