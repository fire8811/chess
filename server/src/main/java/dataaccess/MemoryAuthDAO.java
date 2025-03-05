package dataaccess;

import java.util.ArrayList;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    final private ArrayList<AuthData> authDataMemory = new ArrayList<>();

    public void clearAuthtokens(){
        authDataMemory.clear();
    }
}
