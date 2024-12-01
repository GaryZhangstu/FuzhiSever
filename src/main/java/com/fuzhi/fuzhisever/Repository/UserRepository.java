package com.fuzhi.fuzhisever.Repository;

import com.fuzhi.fuzhisever.Model.SkinAnalysis;
import com.fuzhi.fuzhisever.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {


    User findUserByEmail(String email);


}
