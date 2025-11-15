import chess.ChessGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class clientTest {

    @Test
    void printBoard() {
        Client client = new Client("string");
        client.printBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
        client.printBoard(new ChessGame(), ChessGame.TeamColor.BLACK);
    }
}