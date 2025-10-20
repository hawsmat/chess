package dataaccess;

import datamodel.User;

public interface DataAccess {
    void clear();
    void createUser(User user);
    User getUser(String username);
}
