package service;
import exceptions.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;

import java.sql.SQLException;

//question for TA: the clearservice is accessing the entire DAO classes when it really only needs to access the clear methods. Is this best practice?
public class ClearService {
    private final AuthDAO auth;
    private final UserDAO users;
    private final GameDAO games;

    public ClearService(AuthDAO auth, UserDAO users, GameDAO games){
        this.auth = auth;
        this.users = users;
        this.games = games;
    }

    public void clear() throws SQLException, DataAccessException {
        auth.clearAuths();
        users.clearUsers();
        games.clearGames();
    }
}
