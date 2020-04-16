package com.junhe.demo.service;

import com.junhe.demo.mapper.UserMapper;
import com.junhe.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * @author Explorer
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
//根据用户手机号查询用户信息
    public User queryUser(String phoneNumber){
        return userMapper.queryUserByphoneNumber(phoneNumber);
    }
//新增用户
    public boolean addUser(User user){
        int effectedNum = userMapper.insert(user);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("添加用户失败!");
        }
    }



    //修改用户登录状态
    public boolean modifyUser(String phoneNumber){
        int effectedNum = userMapper.updateUser(phoneNumber);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("修改用户登录状态失败!");
        }
    }
    //新增用户上传的营业执照或者
    public boolean addUserPic(Long phoneNumber , String license , String medicalCard){
        int effectedNum = userMapper.updateUserByPhone(phoneNumber,license,medicalCard);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("修改用户登录状态失败!");
        }
    }

}
