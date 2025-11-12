package dataaccess;

import chess.ChessGame;
import model.RegisterResult;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.util.HashMap;
import java.util.Set;

public class MemoryDataAccess implements DataAccess {
    private HashMap<String, LoginData> loginDatas = new HashMap<>();
    private HashMap<Integer, GameData> gameDatas = new HashMap<>();
    int gameID = 1;
    private HashMap<String, RegisterResult> authDatas = new HashMap<>();

    @Override
    public void clearUserData() throws DataAccessException {
        loginDatas.clear();}

    @Override
    public void createUser(UserData user) throws DataAccessException {
        loginDatas.put(user.username(), new LoginData(user.username(), user.password()));}

    @Override
    public LoginData getUser(String username) throws DataAccessException {
        return loginDatas.get(username);}

    @Override
    public void addAuthData(RegisterResult registerResult) {
        authDatas.put(registerResult.authToken(), registerResult);
    }

    @Override
    public RegisterResult getAuthData(String authToken) throws DataAccessException {return authDatas.get(authToken);}

    @Override
    public void clearAuthData() throws DataAccessException {authDatas.clear();}

    @Override
    public void deleteAuthData(String authToken) throws DataAccessException {authDatas.remove(authToken);}

    @Override
    public int addGame(String gameName) throws DataAccessException {
        gameDatas.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
        gameID++;
        return gameID-1;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {return gameDatas.get(gameID);}

    @Override
    public Set<Integer> getGames() throws DataAccessException {
        return gameDatas.keySet();
    }

    @Override
    public void updateUsernames(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            gameDatas.replace(gameID, new GameData(gameID, username, getBlackUsername(gameID), getGameName(gameID), getChessGame(gameID)));
        }
        else {
            gameDatas.replace(gameID, new GameData(gameID, getWhiteUsername(gameID), username, getGameName(gameID), getChessGame(gameID)));
        }
    }

    @Override
    public String getWhiteUsername(int gameID) throws DataAccessException {
        return gameDatas.get(gameID).whiteUsername();
    }

    @Override
    public String getBlackUsername(int gameID) throws DataAccessException{
        return gameDatas.get(gameID).blackUsername();
    }

    @Override
    public String getGameName(int gameID) throws DataAccessException {
        return gameDatas.get(gameID).gameName();
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {return gameDatas.get(gameID).game();}

    @Override
    public void clearGameData() throws DataAccessException {
        gameDatas.clear();}

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {return (getAuthData(authToken) != null);}
}
