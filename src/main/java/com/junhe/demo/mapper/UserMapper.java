package com.junhe.demo.mapper;

import com.junhe.demo.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insert(User user);

    User queryUserByphoneNumber(String phoneNumber);

    int updateUser(String phoneNumber);

    int updateUserByPhone(@Param("phoneNumber") Long phoneNumber ,@Param("license") String license ,@Param("medicalCard") String medicalCard);
}