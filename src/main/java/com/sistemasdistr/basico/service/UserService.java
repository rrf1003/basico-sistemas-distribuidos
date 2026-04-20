package com.sistemasdistr.basico.service;

import com.sistemasdistr.basico.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private KeyService keyservice;

    public User register(String username, String password) throws Exception{

        Map<String, String> keys = keyService.generateKeyPair();

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setPublicKey(keys.get("public"));
        user.setPrivateKey(keys.get("private"));

        return repo.save(user);
    }
}
