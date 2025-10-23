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

    public List<GameData> listGames(String authToken) throws UnauthorizedException{
        List<GameData> gameData = new ArrayList<>();
        if (memoryDataAccess.isAuthorized(authToken)) {
            for (int gameID: memoryDataAccess.getGames()) {
                gameData.add(memoryDataAccess.getGame(gameID));
            }
            return gameData;
        }
        throw new UnauthorizedException("not authorzied");
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException {
        if (memoryDataAccess.isAuthorized(authToken)) {
            return memoryDataAccess.addGame(gameName);
        }
        throw new UnauthorizedException("not authorized");
    }

    public void joinGame(String authToken, ChessGame.TeamColor playerColor, int gameID) throws AlreadyTakenException, UnauthorizedException, DataAccessException  {
        if (memoryDataAccess.isAuthorized(authToken)) {
            if (memoryDataAccess.getGame(gameID) != null) {
                if (playerColor == ChessGame.TeamColor.WHITE){
                    if (memoryDataAccess.getWhiteUsername(gameID).isEmpty()) {
                        memoryDataAccess.updateGame(gameID, playerColor, memoryDataAccess.getAuthData(authToken).username());
                    }
                    else {
                        throw new AlreadyTakenException("color already taken");
                    }
                }
                else {
                    if (memoryDataAccess.getBlackUsername(gameID) == null) {
                        memoryDataAccess.updateGame(gameID, playerColor, memoryDataAccess.getAuthData(authToken).username());
                    }
                    else {
                        throw new AlreadyTakenException("color already taken");
                    }
                }
            }
            else {
                throw new DataAccessException("gameID does not exist");
            }
        }
        else {
            throw new UnauthorizedException("not authorized");
        }
    }
}
