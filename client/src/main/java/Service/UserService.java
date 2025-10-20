package Service;

import dataaccess.DataAccess;
import datamodel.RegistrationResult;
import datamodel.User;

public class UserService {
    private DataAccess dataAccess;
    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegistrationResult register(User user) {
        return new RegistrationResult(user.username(), "zzyz");
    }
}
