package service;


import chess.ChessGame;
import dataaccess.*;
import model.CreateGameData;
import model.JoinGameData;
import model.ListGameResult;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public List<ListGameResult> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        List<ListGameResult> gameData = new ArrayList<>();
        if (dataAccess.isAuthorized(authToken)) {
            for (int gameID : dataAccess.getGames()) {
                ListGameResult listGameResult = new ListGameResult(dataAccess.getGame(gameID).gameID(),
                        dataAccess.getGame(gameID).whiteUsername(),
                        dataAccess.getGame(gameID).blackUsername(),
                        dataAccess.getGame(gameID).gameName());
                gameData.add(listGameResult);
            }
            return gameData;
        }
        throw new UnauthorizedException("not authorzied");
    }

    public int createGame(CreateGameData createGameData) throws UnauthorizedException, DataAccessException {
        if (dataAccess.isAuthorized(createGameData.authToken())) {
            return dataAccess.addGame(createGameData.gameName());
        }
        throw new UnauthorizedException("not authorized");
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws AlreadyTakenException, UnauthorizedException, DataAccessException {
        if (dataAccess.isAuthorized(authToken)) {
            if (dataAccess.getGame(joinGameData.gameID()) != null) {
                if (joinGameData.playerColor() == ChessGame.TeamColor.WHITE) {
                    if (dataAccess.getWhiteUsername(joinGameData.gameID()) == null) {
                        dataAccess.updateGame(joinGameData.gameID(), joinGameData.playerColor(),
                                dataAccess.getAuthData(authToken).username());
                    }
                    else {
                        throw new AlreadyTakenException("color already taken");
                    }
                }
                else {
                    if (dataAccess.getBlackUsername(joinGameData.gameID()) == null) {
                        dataAccess.updateGame(joinGameData.gameID(), joinGameData.playerColor(),
                                dataAccess.getAuthData(authToken).username());
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
