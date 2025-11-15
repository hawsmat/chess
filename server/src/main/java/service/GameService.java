package service;


import chess.ChessGame;
import dataaccess.*;
import model.LoginResult;
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
                ListGameResult listGameResult = new ListGameResult(gameID,
                        dataAccess.getWhiteUsername(gameID),
                        dataAccess.getBlackUsername(gameID),
                        dataAccess.getGameName(gameID));
                gameData.add(listGameResult);
            }
            return gameData;
        }
        throw new UnauthorizedException("not authorzied");
    }

    public int createGame(String authToken, CreateGameData createGameData) throws UnauthorizedException, DataAccessException {
        if (dataAccess.isAuthorized(authToken)) {
            return dataAccess.addGame(createGameData.gameName());
        }
        throw new UnauthorizedException("not authorized");
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws AlreadyTakenException,
            UnauthorizedException, DataAccessException, Exception{
        if (!dataAccess.isAuthorized(authToken)) {
            throw new UnauthorizedException("not authorized");
        }

        LoginResult authdata = dataAccess.getAuthData(authToken);
        if (authdata == null) {
            throw new UnauthorizedException("Invalid authToken");
        }

        if (dataAccess.getGame(joinGameData.gameID()) == null) {
            throw new UnauthorizedException("gameID does not exist");

        }
        if (joinGameData.playerColor() != ChessGame.TeamColor.WHITE &&
                joinGameData.playerColor() != ChessGame.TeamColor.BLACK) {
            throw new Exception("bad color");
        }
        if (joinGameData.playerColor() == ChessGame.TeamColor.WHITE) {
            if (dataAccess.getWhiteUsername(joinGameData.gameID()) == null) {
                dataAccess.updateUsernames(joinGameData.gameID(), joinGameData.playerColor(),
                        dataAccess.getAuthData(authToken).username());
            }
            else {
                throw new AlreadyTakenException("color already taken");
            }
        }
        else {
            if (dataAccess.getBlackUsername(joinGameData.gameID()) == null) {
                dataAccess.updateUsernames(joinGameData.gameID(), joinGameData.playerColor(),
                        dataAccess.getAuthData(authToken).username());
            }
            else {
                throw new AlreadyTakenException("color already taken");
            }
        }
    }
}
