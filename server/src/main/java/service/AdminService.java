package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class AdminService {
    private DataAccess dataAccess;
    public AdminService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clearAuthData();
        dataAccess.clearGameData();
        dataAccess.clearUserData();
    }
}
