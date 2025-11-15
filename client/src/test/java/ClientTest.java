import chess.ChessGame;
import org.junit.jupiter.api.Test;

class ClientTest {

    @Test
    void printBoard() {
        Client client = new Client("string");
        client.printBoard(new ChessGame(), ChessGame.TeamColor.WHITE);
        client.printBoard(new ChessGame(), ChessGame.TeamColor.BLACK);
    }
}