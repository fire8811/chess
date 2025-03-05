package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDao implements UserDAO{

    final private HashMap<String, UserData> usersMemory = new HashMap<>();

    public void clearUsers(){
        usersMemory.clear();
    }
}
