package usergamecommands;

public class Connect extends UserGameCommand {
    CommandType commandType;
    String authToken;
    int gameID;
    public Connect(CommandType commandType, String authToken, Integer gameID) {
        super(commandType, authToken, gameID);
    }
}
