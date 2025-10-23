package service;


import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.UnauthorizedException;
import model.CreateGameData;
import model.JoinGameData;
import model.ListGameResult;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private MemoryDataAccess memoryDataAccess;

    public GameService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public List<ListGameResult> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        List<ListGameResult> gameData = new ArrayList<>();
        if (memoryDataAccess.isAuthorized(authToken)) {
            for (int gameID : memoryDataAccess.getGames()) {
                ListGameResult listGameResult = new ListGameResult(memoryDataAccess.getGame(gameID).GameID(), memoryDataAccess.getGame(gameID).whiteUsername(), memoryDataAccess.getGame(gameID).blackUsername(), memoryDataAccess.getGame(gameID).gameName());
                gameData.add(listGameResult);
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

    public void joinGame(String authToken, JoinGameData joinGameData) throws AlreadyTakenException, UnauthorizedException, DataAccessException {
        if (memoryDataAccess.isAuthorized(authToken)) {
            if (memoryDataAccess.getGame(joinGameData.gameID()) != null) {
                if (joinGameData.playerColor() == ChessGame.TeamColor.WHITE) {
                    if (memoryDataAccess.getWhiteUsername(joinGameData.gameID()) == null) {
                        memoryDataAccess.updateGame(joinGameData.gameID(), joinGameData.playerColor(), memoryDataAccess.getAuthData(authToken).username());
                    }
                    else {
                        throw new AlreadyTakenException("color already taken");
                    }
                }
                else {
                    if (memoryDataAccess.getBlackUsername(joinGameData.gameID()) == null) {
                        memoryDataAccess.updateGame(joinGameData.gameID(), joinGameData.playerColor(), memoryDataAccess.getAuthData(authToken).username());
                    }
                    else {
                        throw new AlreadyTakenException("color already taken");
                    }
                }
            }
            else {
                throw new UnauthorizedException("gameID does not exist");
            }
        }
        else {
            throw new UnauthorizedException("not authorized");
        }
    }
}
