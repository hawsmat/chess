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
            String statement = "TRUNCATE authdata";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO authdata (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public LoginData getUser(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM authdata WHERE username=?";
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
            String statement = "INSERT INTO authtokens (authtoken, username) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, authData.authToken());
            ps.setString(2, authData.username());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM authtokens WHERE authtoken=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, authToken);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new AuthData(rs.getString("authtoken"), rs.getString("username"));
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearAuthData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE authtokens";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void deleteAuthData(String username) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "DELETE FROM authdata WHERE username=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int addGame(String gameName) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "INSERT INTO gamedata (gamename, game) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS);
            ps.setString(1, gameName);
            String gameData = new Gson().toJson(new ChessGame());
            ps.setString(2, gameData);
            ps.executeUpdate();
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
            String statement = "SELECT * FROM gamedata WHERE gameid=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ChessGame chessGame = new Gson().fromJson(rs.getString("game"), ChessGame.class);
                return new GameData(rs.getInt("gameid"), rs.getString("whiteusername"),
                        rs.getString("blackusername"), rs.getString("gamename"), chessGame);
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
            String statement = "SELECT gameid FROM gamedata";
            PreparedStatement ps = conn.prepareStatement(statement);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                gameIDs.add(rs.getInt("gameid"));
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return gameIDs;
    }

    @Override
    public void updateUsernames(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        if (teamColor == ChessGame.TeamColor.WHITE) {
            try (Connection conn = DatabaseManager.getConnection()) {
                String statement = "UPDATE gamedata SET whiteusername=? WHERE gameid=?";
                PreparedStatement ps = conn.prepareStatement(statement);
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage());
            }
        }
        else {
            try (Connection conn = DatabaseManager.getConnection()) {
                String statement = "UPDATE gamedata SET blackusername=? WHERE gameid=?";
                PreparedStatement ps = conn.prepareStatement(statement);
                ps.setString(1, username);
                ps.setInt(2, gameID);
                ps.executeUpdate();
            } catch (Exception e) {
                throw new DataAccessException(e.getMessage());
            }
        }


    }

    @Override
    public String getWhiteUsername(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT whiteusername FROM gamedata WHERE gameid=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("whiteusername");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("something is very wrong with getWhiteUsername");
    }

    @Override
    public String getBlackUsername(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT blackusername FROM gamedata WHERE gameid=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.getString("blackusername");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("somethign is wrong with getBlackUsername");
    }

    @Override
    public String getGameName(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gamename FROM gamedata WHERE gameid=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("gamename");
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        throw new DataAccessException("somethign is wrong with getGameName");
    }

    @Override
    public ChessGame getChessGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT chessgame FROM gamedata WHERE gameid=?";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.setInt(1, gameID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Gson().fromJson(rs.getString("gamedata"), ChessGame.class);
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void clearGameData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "TRUNCATE gamedata";
            PreparedStatement ps = conn.prepareStatement(statement);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean isAuthorized(String authToken) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT authtoken FROM authtokens";
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
            CREATE TABLE IF NOT EXISTS gamedata (
            gameid INT NOT NULL AUTO_INCREMENT,
            whiteusername VARCHAR(255) DEFAULT NULL,
            blackusername VARCHAR(255) DEFAULT NULL,
            gamename VARCHAR(255) DEFAULT NULL,
            game LONGTEXT NOT NULL,
            PRIMARY KEY (gameID)
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS authdata (
            username VARCHAR(255) DEFAULT NULL,
            password VARCHAR(255) DEFAULT NULL
            );
            """,
            """
            CREATE TABLE IF NOT EXISTS authtokens(
            authtoken VARCHAR(255) DEFAULT NULL,
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
