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
            if (memoryDataAccess.isAuthorized(authToken)) {
                for (int gameID : memoryDataAccess.getGames()) {
                    gameData.add(memoryDataAccess.getGame(gameID));
                }
                return gameData;
            }
        throw new UnauthorizedException("not authorzied");
    }

    public int createGame(CreateGameData createGameData) throws UnauthorizedException, DataAccessException {
        if (memoryDataAccess.isAuthorized(createGameData.authToken())) {

            return memoryDataAccess.addGame(createGameData.gameName());
        }
        throw new UnauthorizedException("not authorized");
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws AlreadyTakenException, UnauthorizedException, DataAccessException  {
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
    }
}
