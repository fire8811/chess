package dataaccess;


import dataaccess.exceptions.UnauthorizedException;
import dataaccess.exceptions.UsernameTakenException;
import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, UserData> usersMemory = new HashMap<>();

    public HashMap<String, UserData> getUsers(){ //getter for testing
        return usersMemory;
    }

    public boolean isUsernameFree(String username) throws UsernameTakenException {
        if (usersMemory.containsKey(username) == false){
            return true;
        }
        else {
            throw new UsernameTakenException("already taken");
        }
    }

    public boolean findUser(String username, String password) throws UnauthorizedException {
        if (usersMemory.containsKey(username) && usersMemory.get(username).password().equals(password)){
            return true;
        }
        else {
            throw new UnauthorizedException("unauthorized");
        }
    }

    public void addUser(UserData userData) {
        usersMemory.put(userData.username(), userData);

    }

    public void clearUsers(){
        usersMemory.clear();
    }
}
