package dataaccess;


import model.UserData;

import javax.xml.crypto.Data;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    final private HashMap<String, UserData> usersMemory = new HashMap<>();

    public HashMap<String, UserData> getUsersMemory(){ //getter for testing
        return usersMemory;
    }

    public boolean isUsernameFree(String username) throws UsernameTakenException{
        if (usersMemory.containsKey(username) == false){
            return true;
        }
        else {
            throw new UsernameTakenException("username already taken!");
        }
    }

    public boolean findUser(String username) {
        return(usersMemory.containsKey(username));
    }

    public void addUser(UserData userData) {
        usersMemory.put(userData.username(), userData);

    }

    public void clearUsers(){
        usersMemory.clear();
    }
}
