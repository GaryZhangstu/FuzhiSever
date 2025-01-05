package com.fuzhi.fuzhiServer.Repository;

import com.fuzhi.fuzhiServer.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {


    User findUserByEmail(String email);


}
