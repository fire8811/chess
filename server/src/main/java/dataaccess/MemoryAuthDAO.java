package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    //final private ArrayList<AuthData> authDataMemory = new ArrayList<>();
    final private HashMap<String, String> authDataMemory = new HashMap<>(); //k: username v: authToken

    public HashMap<String, String> getAuthDataMemory() { //getter used for controlled testing
        return authDataMemory;
    }

    public void addAuthData(AuthData authData){
        authDataMemory.put(authData.username(), authData.authToken());
    }

    public void clearAuthtokens(){
        authDataMemory.clear();
    }
}
