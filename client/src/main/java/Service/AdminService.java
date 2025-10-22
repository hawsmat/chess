package Service;

import dataaccess.MemoryDataAccess;
import model.AuthData;

public class AdminService {
    private MemoryDataAccess memoryDataAccess;
    public AdminService(MemoryDataAccess memoryDataAccess) {
        this.memoryDataAccess = memoryDataAccess;
    }

    public void clear(AuthData authData) {

    }
}
