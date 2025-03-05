package dataaccess;
import java.util.Collection;
import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData data);
    AuthData getAuth(AuthData data);

    void clearAuthtokens();
}
