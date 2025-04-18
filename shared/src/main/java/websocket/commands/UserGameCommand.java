package websocket.commands;

import chess.ChessGame;

import java.util.Objects;

/**
 * Represents a command a user can send the server over a websocket
 *
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class UserGameCommand {

    private final CommandType commandType;

    private final String authToken;

    private final Integer gameID;

    private String username;
    private ChessGame.TeamColor teamColor = null;
    private boolean observerStatus = false;

    public UserGameCommand(CommandType commandType, String authToken, Integer gameID) {
        this.commandType = commandType;
        this.authToken = authToken;
        this.gameID = gameID;
    }

    public enum CommandType {
        CONNECT,
        MAKE_MOVE,
        LEAVE,
        RESIGN
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getAuthToken() {
        return authToken;
    }

    public Integer getGameID() {
        return gameID;
    }

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public ChessGame.TeamColor getTeamColor() {return teamColor;}
    public void setTeamColor(ChessGame.TeamColor teamColor) {this.teamColor = teamColor;}
    public void setObserverStatus(boolean status) {this.observerStatus = status;}
    public boolean getObserverStatus(){return this.observerStatus; }

//    public boolean getObserverStatus() {return observerStatus;}
//    public void setObserverStatusTrue() {observerStatus = true;}

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGameCommand)) {
            return false;
        }
        UserGameCommand that = (UserGameCommand) o;
        return getCommandType() == that.getCommandType() &&
                Objects.equals(getAuthToken(), that.getAuthToken()) &&
                Objects.equals(getGameID(), that.getGameID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCommandType(), getAuthToken(), getGameID());
    }
}
