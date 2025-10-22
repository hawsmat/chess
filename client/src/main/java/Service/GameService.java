package Service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private MemoryDataAccess memoryDataAccess;
    public GameService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    private List<GameData> listGames(String authToken) throws DataAccessException{
        List<GameData> gameData = new ArrayList<>();
        if (isAuthorized(authToken)) {
            for (int gameID: memoryDataAccess.getGames()) {
                gameData.add(memoryDataAccess.getGame(gameID));
            }
            return gameData;
        }
        throw new DataAccessException("not authorzied");
    }

    private int createGame(String authToken, String gameName) throws DataAccessException {
        if (isAuthorized(authToken)) {
            return memoryDataAccess.addGame(gameName);
        }
        throw new DataAccessException("not authorized");
    }

    private void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws DataAccessException {
        if (isAuthorized(authToken)) {
            if (memoryDataAccess.getGame(gameID) != null) {
//                if (memoryDataAccess.getGames(gameID)) {}
            }
            throw new DataAccessException("gameID does not exist");
        }
        throw new DataAccessException("not authorized");
    }

    public boolean isAuthorized(String authToken) {
        return (memoryDataAccess.getAuthData(authToken) != null);
    }
}
