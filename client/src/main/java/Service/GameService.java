package Service;

import chess.ChessBoard;
import chess.ChessGame;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private List<GameData> listGames(String authToken){
        return List.of();
    }

    private void createGame(String authToken, String gameName){}

    private void joinGame(String authToken, ChessGame.TeamColor playerColor, int GameID) {}
}
