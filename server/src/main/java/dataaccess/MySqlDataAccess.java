package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.LoginData;
import model.UserData;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;
import java.util.Set;


public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearUserData() throws DataAccessException {
        String statement = "TRUNCATE authData";
        executeUpdate(statement);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        String json = new Gson().toJson(user);
        executeUpdate(statement);
    }

    @Override
    public LoginData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void addAuthData(AuthData authData) {

    }

    @Override
    public AuthData getAuthData(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        String statement = "TRUNCATE authTokens";
        executeUpdate(statement);
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {
        String statement = "DELETE FROM authData WHERE id=?";
        executeUpdate(statement, username);
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
        String statement = "TRUNCATE gameData";
        executeUpdate(statement);
    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        return false;
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof PetType p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(DataAccessException.Code.ServerError, String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHARR(255) DEFAULT NULL,
            blackUSername VARCHARR(255) DEFAULT NULL,
            gameName VARCHARR(255) DEFAULT NULL,
            game longtext NOTNULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authData (
            username VARCHARR(255) DEFAULT NULL,
            password VARCHARR(255) DEFAULT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS authTokens(
            authToken VARCHARR(255) DEFAULT NULL,
            username VARCHARR(255) DEFAULT NULL
            )
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
