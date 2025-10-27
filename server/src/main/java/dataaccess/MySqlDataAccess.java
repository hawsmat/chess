package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.util.Set;


public class MySqlDataAccess implements DataAccess{
    public MySqlDataAccess() throws DataAccessException{
        configureDatabase();
    }
    @Override
    public void clearUserData() throws DataAccessException {

    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public LoginData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData createAuthData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuthData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearAuthData() throws DataAccessException {

    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {

    }

    @Override
    public int addGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Set<Integer> getGames() throws DataAccessException {
        return Set.of();
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {

    }

    @Override
    public String getWhiteUsername(int gameID) throws DataAccessException {
        return "";
    }

    @Override
    public String getBlackUsername(int gameID) throws DataAccessException {
        return "";
    }

    @Override
    public String getGameName(int gameID) throws DataAccessException {
        return "";
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void clearGameData() throws DataAccessException {

    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        return false;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  game (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException ex) {
            System.out.println(ex.getMessage());

        }
    }
}
