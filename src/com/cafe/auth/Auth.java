package com.cafe.auth;

import com.cafe.model.User;

public abstract class Auth {
    
    public abstract User login(String username, String password);
}