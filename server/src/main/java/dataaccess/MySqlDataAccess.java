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

import java.util.List;
import java.util.Set;


public class MySqlDataAccess implements DataAccess {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void clearUserData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE authData";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO userData (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public LoginData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM userData WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new LoginData(rs.getString("username"), rs.getString("password"));
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void addAuthData(AuthData authData) throws DataAccessException {
        String statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
        String json = new Gson().toJson(authData);
        executeUpdate(statement, authData.authToken(), authData.username(), json);
    }

    @Override
    public AuthData getAuthData(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT FROM authData WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE authTokens";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {
        String statement = "DELETE FROM authData WHERE id=?";
        executeUpdate(statement, username);
    }

    @Override
    public int addGame(String gameName) throws DataAccessException {
        String statement = "INSERT INTO gameData (gameName, game) VALUES (?, ?)";
        String json = new Gson().toJson(gameName);
        executeUpdate(statement, gameName, new ChessGame(), json);
        try (Connection conn = DatabaseManager.getConnection()) {
            String newStatement = "SELECT gameID FROM gameData WHERE gameName=?";
            try (PreparedStatement ps = conn.prepareStatement(newStatement)){
                ps.setString(1, gameName);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return 0;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public Set<Integer> getGames() throws DataAccessException {
        Set<Integer> gameIDs = new Set<Integer>() {};
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT FROM gameData";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameIDs;
    }

    @Override
    public void updateGame(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {

    }

    @Override
    public String getWhiteUsername(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT whiteUsername FROM gameDATA WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public String getBlackUsername(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT blackUsername FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public String getGameName(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameName FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT chessGame FROM gameData WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)){
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {

                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearGameData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE gameData";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        return false;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS gameData (
            gameID INT NOT NULL AUTO_INCREMENT,
            whiteUsername VARCHAR(255) DEFAULT NULL,
            blackUsername VARCHAR(255) DEFAULT NULL,
            gameName VARCHAR(255) DEFAULT NULL,
            game LONGTEXT NOT NULL,
            PRIMARY KEY (gameID)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS authData (
            username VARCHAR(255) DEFAULT NULL,
            password VARCHAR(255) DEFAULT NULL
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS authTokens(
            authToken VARCHAR(255) DEFAULT NULL,
            username VARCHAR(255) DEFAULT NULL
            );
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
