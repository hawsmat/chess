package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, UserData> users = new HashMap<>();
    private HashMap<Integer, GameData> gameIDs = new HashMap<>();
    int gameID = 0;
    private HashMap<String, AuthData> authDatas = new HashMap<>();

    @Override
    public void clearUserData() throws DataAccessException {users.clear();}

    @Override
    public void createUser(UserData user) throws DataAccessException {users.put(user.username(), user);}

    @Override
    public UserData getUser(String username) throws DataAccessException {return users.get(username);}

    @Override
    public void deleteUserData(String username) throws DataAccessException {users.remove(username);}

    @Override
    public AuthData createAuthData(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        while (true) {
            if (!authDatas.containsKey(authToken)) {
                break;
            }
        }
        AuthData authData = new AuthData(username, authToken);
        authDatas.put(authToken, authData);
        return authData;
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {return authDatas.get(authToken);}

    @Override
    public void clearAuthData() throws DataAccessException {authDatas.clear();}

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {authDatas.remove(authToken);}

    @Override
    public int addGame(String gameName) throws DataAccessException {
        gameIDs.put(gameID, new GameData(gameID, "", "", gameName, new ChessGame()));
        gameID++;
        return gameID-1;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {return gameIDs.get(gameID);}

    @Override
    public Set<Integer> getGames() throws DataAccessException {
        return gameIDs.keySet();
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            gameIDs.replace(gameID, new GameData(gameID, username, getBlackUsername(gameID), getGameName(gameID), getChessGame(gameID)));
        }
        else {
            gameIDs.replace(gameID, new GameData(gameID, getWhiteUsername(gameID), username, getGameName(gameID), getChessGame(gameID)));
        }
    }

    @Override
    public String getWhiteUsername(int gameID) throws DataAccessException {
        return gameIDs.get(gameID).whiteUsername();
    }

    @Override
    public String getBlackUsername(int gameID) throws DataAccessException{
        return gameIDs.get(gameID).blackUsername();
    }

    @Override
    public String getGameName(int gameID) throws DataAccessException {
        return gameIDs.get(gameID).gameName();
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {return gameIDs.get(gameID).game();}

    @Override
    public void clearGameData() throws DataAccessException {gameIDs.clear();}

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {return (getAuthData(authToken) != null);}
}
