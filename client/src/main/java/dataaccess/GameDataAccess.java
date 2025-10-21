package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class GameDataAccess {
    private HashMap<String, GameData> gameIDs = new HashMap<>();
    int gameID = 0;
    public void createGame(String gameName) {
        new GameData(gameID, "", "", gameName, new ChessGame());
        gameID++;
    }

    public GameData getGame(String gameName) {
        return gameIDs.get(gameName);
    }

    public void updateGame(){}

    public void clear() {
        gameIDs.clear();
    }
}
