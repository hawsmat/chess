package dataaccess;

import datamodel.User;

public interface DataAccess {
    void saveUse(User user);
    void getUser(String username);
}
