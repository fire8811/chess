package serverfacade;

import com.google.gson.Gson;
import exceptions.DataAccessException;
import exceptions.ResponseException;
import model.RegisterRequest;
import model.RegisterResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {serverUrl = url;}

    private <T> T sendRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            //TODO: writebody
            http.connect();
            //TODO: throw errors if connection not successful

            return getObjectFromBody(http, responseClass);
        } catch (ResponseException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseException(e.getMessage());
        }
    }

    private static <T> T getObjectFromBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
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
}
