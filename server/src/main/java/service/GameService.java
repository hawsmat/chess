package service;


import chess.ChessGame;
import dataaccess.*;
import model.*;

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
                        dataAccess.getGameName(gameID), dataAccess.getChessGame(gameID));
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

    public void updateGame(String authToken, int gameID, ChessGame game) throws AlreadyTakenException, DataAccessException, Exception {
        if (!dataAccess.isAuthorized(authToken)) {
            throw new UnauthorizedException("not authorized");
        }
        LoginResult loginResult = dataAccess.getAuthData(authToken);
        if (loginResult == null) {
            throw new UnauthorizedException("Invalid authToken");
        }
        dataAccess.updateGame(gameID, game);
    }

    public void joinGame(String authToken, JoinGameData joinGameData) throws AlreadyTakenException,
            UnauthorizedException, DataAccessException, Exception {
        if (!dataAccess.isAuthorized(authToken)) {
            throw new UnauthorizedException("not authorized");
        }
        LoginResult loginResult = dataAccess.getAuthData(authToken);
        if (loginResult == null) {
            throw new UnauthorizedException("Invalid authToken");
        }
        GameData gameData = dataAccess.getGame(joinGameData.gameID());
        if (gameData == null) {
            throw new UnauthorizedException("gameID does not exist");
        }

        String username = loginResult.username();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        boolean whiteTaken = whiteUsername != null && !whiteUsername.equals(username);
        boolean blackTaken = blackUsername != null && !blackUsername.equals(username);

        if ((joinGameData.playerColor() != ChessGame.TeamColor.WHITE &&
                joinGameData.playerColor() != ChessGame.TeamColor.BLACK)) {
            throw new Exception("bad color");
        }

        if (joinGameData.playerColor() == ChessGame.TeamColor.WHITE) {
            if (whiteTaken) {
                throw new AlreadyTakenException("color already taken");
            }
            dataAccess.updateUsernames(joinGameData.gameID(), ChessGame.TeamColor.WHITE, username);
        }
        else {
            if (blackTaken) {
                throw new AlreadyTakenException("color already taken");
            }
            dataAccess.updateUsernames(joinGameData.gameID(), ChessGame.TeamColor.BLACK, username);
        }
    }
}
