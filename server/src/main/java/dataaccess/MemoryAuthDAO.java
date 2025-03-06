package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    final private ArrayList<AuthData> authDataMemory = new ArrayList<>();
    //final private HashMap<String, String> authDataMemory = new HashMap<>(); //k: username v: authToken

    public ArrayList<AuthData> getAuths() { //getter used for controlled testing
        return authDataMemory;
    }

    public void addAuthData(AuthData authData){
        authDataMemory.add(authData);
    }

    public void clearAuthtokens(){
        authDataMemory.clear();
    }

    public int findAuthToken(String authToken) throws UnauthorizedException{
        for (int i = 0; i < authDataMemory.size(); i++){
            if(authDataMemory.get(i).authToken().equals(authToken)){
                return i;
            }
        }
        throw new UnauthorizedException("unauthorized"); //authData not in memory
    }

    public void deleteAuthToken(String authToken) throws UnauthorizedException {
        int tokenIndex = findAuthToken(authToken);
        authDataMemory.remove(tokenIndex);
    }

//    public void verifyAuthToken(String authToken) throws UnauthorizedException {
//        findAuthToken(authToken); //exception thrown in findAuthToken means the token is invalid (not found)
//    }
}
