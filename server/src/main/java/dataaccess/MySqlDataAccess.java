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

import java.util.HashSet;
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
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO authData (authToken, username) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuthData(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT FROM authData WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new AuthData(rs.getString("authToken"), rs.getString("username"));
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
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM authData WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int addGame(String gameName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO gameData (gameName, game) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, gameName);
            String gameData = new Gson().toJson(new ChessGame());
            ps.setString(2, gameData);
            ps.executeQuery();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("something is very wrong when adding game");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT FROM gameData WHERE gameID=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ChessGame chessGame = new Gson().fromJson(rs.getString("gameData"), ChessGame.class);
                return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                        rs.getString("blackUsername"), rs.getString("gameName"), chessGame);
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public Set<Integer> getGames() throws DataAccessException {
        Set<Integer> gameIDs = new HashSet<>() {};
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID FROM gameData";
            PreparedStatement ps = conn.prepareStatement(statement);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                gameIDs.add(rs.getInt("gameID"));
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameIDs;
    }

    @Override
    public void updateUsernames(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "UPDATE gameData SET teamColor=? WHERE gameID=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, username);
            ps.setInt(2, gameID);
            ps.executeQuery();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String getWhiteUsername(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT whiteUsername FROM gameDATA WHERE gameID=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("whiteUsername");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("something is very wrong with getWhiteUsername");
    }

    @Override
    public String getBlackUsername(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT blackUsername FROM gameData WHERE gameID=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.getString("blackUsername");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("somethign is wrong with getBlackUsername");
    }

    @Override
    public String getGameName(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameName FROM gameData WHERE gameID=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("gameName");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("somethign is wrong with getGameName");
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT chessGame FROM gameData WHERE gameID=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Gson().fromJson(rs.getString("gameData"), ChessGame.class);
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
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authToken from AuthToken";
            PreparedStatement ps = conn.prepareStatement(statement);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
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
