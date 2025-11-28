import chess.ChessGame;
import serverfacade.ServerFacade;

public class ConnectFacade {
    public static void main(String[] args) {
        Connect connection = new Connect(new ServerFacade("http://localhost:8080"), new ChessGame(), ChessGame.TeamColor.WHITE);
        connection.run();
    }

}
