package Service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class AdminService {
    private MemoryDataAccess memoryDataAccess;
    public AdminService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public void clear(String authToken) throws DataAccessException {
        if (memoryDataAccess.isAuthorized(authToken)) {
            memoryDataAccess.clearUserData();
            memoryDataAccess.clearGameData();
            memoryDataAccess.clearUserData();
        }
        throw new DataAccessException("not authorized");
    }
}
