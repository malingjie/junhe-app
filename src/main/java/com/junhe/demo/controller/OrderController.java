package com.junhe.demo.controller;

import com.junhe.demo.model.Order;
import com.junhe.demo.service.OrderService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/order")
public class OrderController {
@Autowired
    private OrderService orderService;

@RequestMapping(value = "/addorder")
private Map<String, Object> addOrder(Order order){
    //使用Date获取当前时间
    Date d = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String orderTime = sdf.format(d);
    String nowtime = d.getTime() +"";
    UUID uuid = UUID.randomUUID();
    //uuid生成订单号
    String orderNum = nowtime+uuid.toString().replaceAll("-","").substring(0,2);
    Map<String, Object> modelMap = new HashMap<String, Object>();
    order.setOrderNum(orderNum);
        order.setOrderTime(orderTime);
        order.setOrderType("公对公转账");
        order.setOrderStatus("未支付");
        order.setPublicStatus("0");
        modelMap.put("success", orderService.addOrder(order));
        return modelMap;

}

    @RequestMapping(value = "/queryorder")
    private Map<String, Object> queryOrder(Long phoneNumber){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<Order> list = new ArrayList<Order>();
        list = orderService.queryOrder(phoneNumber);
        modelMap.put("orders",list);
        return modelMap;
    }

    @RequestMapping(value = "/queryorderbyordernum")
    private Map<String, Object> queryOrderByorderNum(String orderNum){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Order o = new Order();
        o = orderService.queryOrderByOrderNum(orderNum);
        modelMap.put("order",o);
        return modelMap;
    }

    @RequestMapping(value = "/deluserorder")
    private ResponseEntity delOrder(String orderNum){
        orderService.delUserOrder(orderNum);
        return ResponseEntity.ok("delete success");
    }

    @RequestMapping(value = "/querybusiness")
    private Map<String, Object> queryBusinessOrder(Long phoneNumber){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<Order> list = new ArrayList<Order>();
        list = orderService.queryBusinessOrder(phoneNumber);
        modelMap.put("businessOrders",list);
        return modelMap;
    }

    /**
     * 上传公对公转账凭证
     */
    @RequestMapping("/uploadbusiness")
    public void  uploadPicture(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "orderNum") String orderNum) throws Exception {
        //获取文件需要上传到的路径/本地测试路径
        File directory = new File(".");
//        String path = directory.getCanonicalPath() + "\\src\\main\\resources\\static\\imgs\\";
        //服务器路径
        String path = "/root/junhe_forPay_images/";


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
                String orderStatus = "支付中";
                String publicStatus = "2";

                //真正写到磁盘上
                File file1 = new File(destPath);
                OutputStream out = new FileOutputStream(file1);
                out.write(file.getBytes());
                res.put("url", destPath);
                out.close();
                orderService.modifyOrderPic(orderNum,newName,orderStatus,publicStatus);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

}
