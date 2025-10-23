package Service;


import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private MemoryDataAccess memoryDataAccess;

    public GameService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public List<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        List<GameData> gameData = new ArrayList<>();
        try {
            if (memoryDataAccess.isAuthorized(authToken)) {
                for (int gameID : memoryDataAccess.getGames()) {
                    gameData.add(memoryDataAccess.getGame(gameID));
                }
                return gameData;
            }
            throw new UnauthorizedException("not authorzied");
        } catch (DataAccessException e) {
            throw e;
        }
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, DataAccessException {
         try {
            if (memoryDataAccess.isAuthorized(authToken)) {
                return memoryDataAccess.addGame(gameName);
            }
            throw new UnauthorizedException("not authorized");
        } catch (DataAccessException e) {
             throw e;
         }
    }

    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws AlreadyTakenException, UnauthorizedException, DataAccessException  {
        try {
            if (memoryDataAccess.isAuthorized(authToken)) {
                if (memoryDataAccess.getGame(gameID) != null) {
                    if (playerColor == ChessGame.TeamColor.WHITE) {
                        if (memoryDataAccess.getWhiteUsername(gameID).isEmpty()) {
                            memoryDataAccess.updateGame(gameID, playerColor, memoryDataAccess.getAuthData(authToken).username());
                        } else {
                            throw new AlreadyTakenException("color already taken");
                        }
                    } else {
                        if (memoryDataAccess.getBlackUsername(gameID) == null) {
                            memoryDataAccess.updateGame(gameID, playerColor, memoryDataAccess.getAuthData(authToken).username());
                        } else {
                            throw new AlreadyTakenException("color already taken");
                        }
                    }
                } else {
                    throw new UnauthorizedException("gameID does not exist");
                }
            } else {
                throw new UnauthorizedException("not authorized");
            }
        } catch (DataAccessException e) {
            throw e;
        }
    }
}
