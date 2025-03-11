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

    public void clearAuths(){
        authDataMemory.clear();
    }

    public boolean authTokenExists(String authToken) throws UnauthorizedException {
        try {
            getAuthTokenIndex(authToken); //if getAuthTokenIndex returns an index it means the token exists
            return true;
        } catch (UnauthorizedException e){
            throw new UnauthorizedException(e.getMessage()); //authData not in memory
        }
    }

    public int getAuthTokenIndex(String authToken) throws UnauthorizedException{
        for (int i = 0; i < authDataMemory.size(); i++){
            if(authDataMemory.get(i).authToken().equals(authToken)){
                return i;
            }
        }
        throw new UnauthorizedException("unauthorized"); //authData not in memory
    }

    public void deleteAuthData(String authToken) throws UnauthorizedException {
        int tokenIndex = getAuthTokenIndex(authToken);
        authDataMemory.remove(tokenIndex);
    }

    public String getUsername(String authToken) throws UnauthorizedException {
        int tokenIndex = getAuthTokenIndex(authToken);
        return authDataMemory.get(tokenIndex).username();
    }
}
