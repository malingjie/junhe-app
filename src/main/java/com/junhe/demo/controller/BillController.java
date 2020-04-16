package com.junhe.demo.controller;

import com.junhe.demo.model.Bill;
import com.junhe.demo.model.Order;
import com.junhe.demo.service.BillService;
import com.junhe.demo.service.OrderService;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/bill")
public class BillController {

    @Autowired
    private BillService billService;

    @Autowired
    private OrderService orderService;

    /**
     * 用户开发票申购单上传
     */
    @RequestMapping("/uploadsubscribe")
    public void  uploadPicture(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "orderNum") String orderNum) throws Exception {
        //获取文件需要上传到的路径/本地测试路径
//        File directory = new File(".");
//        String path = directory.getCanonicalPath() + "\\src\\main\\resources\\static\\imgs\\";
        //服务器路径
        String path = "/root/junhe_forBill_images/";


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

                billService.addSubscribe(newName,orderNum);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * 发票信息生成
     */

    @RequestMapping(value = "/addbill",method = RequestMethod.POST)
    private Map<String, Object> addBill(Bill bill){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Map checkUpload = new HashMap();
        String ordernum = bill.getOrderNum();
        Bill bill1 = billService.queryOrder(ordernum);
        Order order = orderService.queryOrderByOrderNum(ordernum);
        bill.setGoodsName(order.getGoodsName());
        bill.setBuyNum(order.getBuyNum());
        bill.setGoodsPrice(order.getGoodsPrice());
        bill.setAllgoodsPrice(order.getAllgoodsPrice());
        bill.setOrderStatus(order.getOrderStatus());
        if(bill1!=null){
            throw new RuntimeException("该笔订单已经发起开票申请，请勿重复申请!");
        }else {
            modelMap.put("success", billService.addBill(bill));
            checkUpload.put("orderNum",bill.getOrderNum());
            return checkUpload;
        }
    }
}
