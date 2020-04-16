package com.junhe.demo.controller;

import com.junhe.demo.model.User;
import com.junhe.demo.service.CheckOutService;
import com.junhe.demo.service.UserService;

import com.junhe.demo.utils.CryptoUtil;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/test")
@Scope("prototype")
public class UserController {


    @Autowired
    private UserService userService;
    private CheckOutService checkOutService;

    /**
     * 用户营业执照上传
     */
    @RequestMapping("/upload")
    public void  uploadPicture(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "userPhoneNumber") Long phoneNumber,
                               @RequestParam(value = "license") String license, @RequestParam(value = "medicalCard") String medicalCard) throws Exception {
        //获取文件需要上传到的路径/本地测试路径
//        File directory = new File(".");
//        String path = directory.getCanonicalPath() + "\\src\\main\\resources\\static\\imgs\\";
        //服务器路径
        String path = "/root/junhe_userCard_images/";


        // 判断存放上传文件的目录是否存在（不存在则创建）
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
        System.out.println("path=" + path);

        request.setCharacterEncoding("utf-8"); //设置编码

        JSONArray jsonArray = new JSONArray();

        try {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            Iterator<String> iterator = req.getFileNames();
            while (iterator.hasNext()) {
                HashMap<String, Object> res = new HashMap<String, Object>();
                MultipartFile file = req.getFile(iterator.next());
                // 获取文件名
                String fileNames = file.getOriginalFilename();
                int split = fileNames.lastIndexOf(".");
                //获取上传文件的后缀
                String extName = fileNames.substring(split + 1, fileNames.length());
                //申明UUID
                String uuid = UUID.randomUUID().toString().replace("-", "");

                //组成新的图片名称
                String newName = uuid + "." + extName;
                System.out.println(newName);

                String destPath = path + newName;
                System.out.println("destPath=" + destPath);

                //真正写到磁盘上
                File file1 = new File(destPath);
                OutputStream out = new FileOutputStream(file1);
                out.write(file.getBytes());
                res.put("url", destPath);
                out.close();
                if("0".equals(license)&&"1".equals(medicalCard)){
                    license = "";
                     medicalCard = newName;
                }else if("1".equals(license)&&"0".equals(medicalCard)){
                     license = newName;
                    medicalCard = "";
                }
                userService.addUserPic(phoneNumber,license,medicalCard);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }


    /**
     * 用户注册
     */
    @RequestMapping(value = "/adduser",method = RequestMethod.POST)
    private Map addUser(HttpServletRequest request, User user){
        System.out.println(user);
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Map checkUpload = new HashMap();
        String phone = user.getPhoneNumber();
        String psd_encode = CryptoUtil.getEncryptString(user.getPassword());
        user.setPassword(psd_encode);
        User user1 =  userService.queryUser(phone);
        if(user1!=null){
            throw new RuntimeException("用户已存在!");
        }else {
            modelMap.put("success", userService.addUser(user));
            checkUpload.put("userPhoneNumber",user.getPhoneNumber());
            checkUpload.put("license",user.getLicense());
            checkUpload.put("medicalCard",user.getMedicalCard());
            return checkUpload;
        }

    }


    /**
     * 用户登录
     */
    @RequestMapping(value = "/checkuser",method = RequestMethod.GET)
    private Map<String, Object> checkUser(@RequestParam(value = "phoneNumber") String phoneNumber,@RequestParam(value = "password")String password){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        String password_encode = CryptoUtil.getEncryptString(password);
        User user = userService.queryUser(phoneNumber);
        if(user==null){
            throw new RuntimeException("用户不存在或注册审核未通过，请注册!");
        }
        if(user.getPassword().equals(password_encode)&&user.getCheckStatus().equals("1")){
            userService.modifyUser(user.getPhoneNumber());
            User user1 = userService.queryUser(phoneNumber);
            modelMap.put("user",user1);
            return modelMap;
        }else if(!user.getPassword().equals(password_encode)){
            throw new RuntimeException("密码错误!");
        }else if(!user.getCheckStatus().equals("1")){
            throw new RuntimeException("用户审核中，请审核通过登录!");
        }
        return modelMap;
    }


    /**
     * 用户查询
     */
    @RequestMapping(value = "/queryuser",method = RequestMethod.GET)
    private User queryuser(@RequestParam(value = "phoneNumber") String phoneNumber){
        User user = userService.queryUser(phoneNumber);
        String password_decode = CryptoUtil.getDecryptString(user.getPassword());
        user.setPassword(password_decode);
       return user;
    }
}