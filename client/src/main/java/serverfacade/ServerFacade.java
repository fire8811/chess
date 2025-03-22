package serverfacade;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import exceptions.ResponseException;
import model.RegisterRequest;
import model.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.HashMap;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {serverUrl = url;}

    private <T> T sendRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (request != null) {
                writeHttpBody(request, http); //write JSON body
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
