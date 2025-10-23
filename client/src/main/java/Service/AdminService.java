package Service;

import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class AdminService {
    private MemoryDataAccess memoryDataAccess;
    public AdminService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public void clear() throws DataAccessException {
            memoryDataAccess.clearUserData();
            memoryDataAccess.clearGameData();
            memoryDataAccess.clearUserData();
    }
}
