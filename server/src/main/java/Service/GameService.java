package Service;


import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.CreateGameData;
import model.GameData;
import model.JoinGameData;

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

    public int createGame(CreateGameData createGameData) throws UnauthorizedException, DataAccessException {
         try {
            if (memoryDataAccess.isAuthorized(createGameData.authToken())) {
                return memoryDataAccess.addGame(createGameData.gameName());
            }
            throw new UnauthorizedException("not authorized");
        } catch (DataAccessException e) {
             throw e;
         }
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws AlreadyTakenException, UnauthorizedException, DataAccessException  {
        try {
            if (memoryDataAccess.isAuthorized(authToken)) {
                if (memoryDataAccess.getGame(joinGameData.gameID()) != null) {
                    if (joinGameData.playerColor() == ChessGame.TeamColor.WHITE) {
                        if (memoryDataAccess.getWhiteUsername(joinGameData.gameID()).isEmpty()) {
                            memoryDataAccess.updateGame(joinGameData.gameID(), joinGameData.playerColor(), memoryDataAccess.getAuthData(authToken).username());
                        } else {
                            throw new AlreadyTakenException("color already taken");
                        }
                    } else {
                        if (memoryDataAccess.getBlackUsername(joinGameData.gameID()) == null) {
                            memoryDataAccess.updateGame(joinGameData.gameID(), joinGameData.playerColor(), memoryDataAccess.getAuthData(authToken).username());
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
