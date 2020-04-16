package com.junhe.demo.controller;


import com.jpay.ext.kit.HttpKit;
import com.jpay.ext.kit.PaymentKit;
import com.jpay.weixin.api.WxPayApi;
import com.junhe.demo.model.Order;
import com.junhe.demo.model.SecretKey;
import com.junhe.demo.service.GoodsService;
import com.junhe.demo.service.OrderService;
import com.junhe.demo.service.PayService;
import com.junhe.demo.utils.UrlUtil;
import com.junhe.demo.utils.WXConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping(value = "/pay")
public class PayController {

    @Autowired
    private PayService payService;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    public static Log log = LogFactory.getLog(PayController.class);



    /**
     * 判断返回的return_code,给前端相应的提示
     * @param return_code
     * @return
     * @author Explorer
     * @time 2019年06月20日16:33:35
     */
    private String getMsgByCode(String return_code) {
        switch (return_code){
            case "NOTENOUGH":return "您的账户余额不足";
            case "ORDERPAID":return "该订单已支付完成,请勿重复支付";
            case "ORDERCLOSED":return "当前订单已关闭，请重新下单";
            case "SYSTEMERROR":return "系统超时，请重新支付";
            case "OUT_TRADE_NO_USED":return "请勿重复提交该订单";
            default:return  "网络正在开小差,请稍后再试";
        }
    }

    //获取openid
    @RequestMapping("/getopenid")
    @ResponseBody
    public String getOpenid(String js_code){
          //微信端登录code值  
           //String wxCode = code;  
           //ResourceBundle resource = ResourceBundle.getBundle("weixin");   //读取属性文件  
           //String requestUrl = resource.getString("url");  //请求地址 https://api.weixin.qq.com/sns/jscode2session  
           Map<String,String> requestUrlParam = new HashMap<String,String>();
           requestUrlParam.put("appid", WXConst.appId); //开发者设置中的appId  
           requestUrlParam.put("secret", WXConst.appSecret); //开发者设置中的appSecret  
           requestUrlParam.put("js_code", js_code); //小程序调用wx.login返回的code  
           requestUrlParam.put("grant_type", " 'authorization_code'"); //默认参数  
          //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识  
           String jsonObject = UrlUtil.sendPost(WXConst.WxGetOpenIdUrl, requestUrlParam);
           return jsonObject;


    }


    /**
     * 小程序微信支付的第一步,统一下单
     * @return
     * @author Explorer
     * @time 2019年06月20日16:33:35
     */
    @RequestMapping("/createUnifiedOrder")
    @ResponseBody
    public AjaxJson createUnifiedOrder(HttpServletRequest request,@RequestParam(value = "phoneNumber") String phoneNumber, @RequestParam(value = "open") String open,@RequestParam(value = "goodsName") String goodsName,
                                       @RequestParam(value = "goodsCode") String goodsCode,@RequestParam(value = "goodsNum") Long goodsNum,@RequestParam(value = "buyerName") String buyerName,@RequestParam(value = "buyerNumber") String buyerNumber,
                                       @RequestParam(value = "buyerAddress") String buyerAddress, @RequestParam(value = "company") String company) {
        AjaxJson aj = new AjaxJson();
        aj.setSuccess(false);

        //获得秘钥
        SecretKey key = payService.searchKey();
        final String appId = key.getAppid();
        final String mch_id = key.getMchId();
        final String partnerKey = key.getPartnerKey();

        //创建 时间戳
        String timeStamp = Long.valueOf(System.currentTimeMillis()).toString();
        //使用Date获取当前时间
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String orderTime = sdf.format(d);
        String nowtime = d.getTime() +"";
        //生成32位随机数
        UUID uuid = UUID.randomUUID();
        //uuid生成订单号
        String orderNum = nowtime+uuid.toString().replaceAll("-","").substring(0,2);
        String openid = open;
        //计算商品价格
        double price = goodsService.searchPrice(goodsCode);
        double allfee = price * goodsNum;
        double totalfee = price * goodsNum * 100 ;
        String totalFee = String.valueOf(totalfee);
        String useTotalFee = totalFee.substring(0,totalFee.indexOf("."));
        //生成订单所需的信息
        Order o =new Order();
        o.setOrderTime(orderTime);
        o.setOrderNum(orderNum);
        o.setPhoneNumber(phoneNumber);
        o.setGoodsCode(goodsCode);
        o.setGoodsName(goodsName);
        o.setGoodsPrice(price);
        o.setAllgoodsPrice(allfee);
        o.setBuyNum(goodsNum);
        o.setOrderType("微信支付");
        o.setOrderStatus("未支付");
        o.setCompany(company);
        o.setBuyerName(buyerName);
        o.setBuyerNumber(buyerNumber);
        o.setBuyerAddress(buyerAddress);

        if (StringUtils.isAnyBlank(openid)){
            aj.setMsg("支付失败,支付所需参数缺失");
            return aj;
        }
        String return_msg = "统一订单失败";
        try {

            String nonceStr = uuid.toString().replaceAll("-","");
            //商品描述
            String body = "君和小商品-支付订单";
            //创建hashmap(用户获得签名)
            SortedMap<String, String> paraMap = new TreeMap<String, String>();
            //设置请求参数(小程序ID)
            paraMap.put("appid", appId);
            //设置请求参数(商户号)
            paraMap.put("mch_id", mch_id);
            //设置请求参数(随机字符串)
            paraMap.put("nonce_str", nonceStr);
            //设置请求参数(商品描述)
            paraMap.put("body", body);
            //设置请求参数(商户订单号)
            paraMap.put("out_trade_no", orderNum);
            //设置请求参数(总金额)
            paraMap.put("total_fee",useTotalFee);
            //设置请求参数(终端IP) 如果是springmvc,或者能获取到request的servlet,用下面这种
            paraMap.put("spbill_create_ip", request.getRemoteAddr());
            //设置请求参数(通知地址)
            paraMap.put("notify_url","https://shop.junhesoft.com.cn/pay/notify");
            //设置请求参数(交易类型)
            paraMap.put("trade_type", String.valueOf(WxPayApi.TradeType.JSAPI));
            //设置请求参数(openid)(在接口文档中 该参数 是否必填项 但是一定要注意 如果交易类型设置成'JSAPI'则必须传入openid)
            paraMap.put("openid", openid);
            //MD5运算生成签名，这里是第一次签名，用于调用统一下单接口
            String sign = PaymentKit.createSign(paraMap, partnerKey);
            paraMap.put("sign", sign);
            //统一下单,向微信api发送数据
            log.info("微信小程序统一下单发送的数据: "+paraMap.toString());
            String xmlResult = WxPayApi.pushOrder(false, paraMap);
            log.info("微信小程序统一下单接受返回的结果: "+xmlResult);
            //转成xml
            Map<String, String> map = PaymentKit.xmlToMap(xmlResult);
            //返回状态码
            String return_code = (String) map.get("return_code");
            return_msg = return_msg+", "+ (String) map.get("return_msg");
            //返回给小程序端需要的参数
            Map<String, String> returnMap = new HashMap<String, String>();
            if("SUCCESS".equals(return_code)){
                //返回的预付单信息
                returnMap.put("appId",appId);
                returnMap.put("nonceStr", nonceStr);
                String prepay_id = (String) map.get("prepay_id");
                returnMap.put("package", "prepay_id=" + prepay_id);
                returnMap.put("signType","MD5");
                //这边要将返回的时间戳转化成字符串，不然小程序端调用wx.requestPayment方法会报签名错误
                returnMap.put("timeStamp", timeStamp);
                //拼接签名需要的参数
                //再次签名，这个签名用于小程序端调用wx.requesetPayment方法
                String paySign = PaymentKit.createSign(returnMap, partnerKey).toUpperCase();
                returnMap.put("paySign", paySign);
                aj.setObj(returnMap);
                aj.setMsg("操作成功");
                aj.setSuccess(true);
                //支付成功，向订单表中插入该笔订单
                orderService.addOrder(o);
                return aj;
            }else{
                aj.setMsg(getMsgByCode(return_code));
                log.error(Thread.currentThread().getStackTrace()[1].getMethodName()+">>>"+return_msg);
            }
        } catch (Exception e) {
            log.error(Thread.currentThread().getStackTrace()[1].getMethodName() +"发生的异常是: ",e);
            aj.setMsg("微信支付下单失败,请稍后再试");
        }
        return aj;
    }


    @RequestMapping("/notify")
    public void notify(HttpServletRequest request) {
        //获取所有的参数
        StringBuffer sbf = new StringBuffer();

        // 支付结果通用通知文档: https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_7
        String xmlMsg = HttpKit.readData(request);
        System.out.println("支付通知=" + xmlMsg);
        Map<String, String> params = PaymentKit.xmlToMap(xmlMsg);
         System.out.println(params);

        String result_code = params.get("result_code");
        //校验返回来的支付结果,根据已经配置的密钥
        if (PaymentKit.verifyNotify(params,  payService.searchKey().getPartnerKey())) {
            //Constants.SUCCESS="SUCCESS"
            if (("SUCCESS").equals(result_code)) {
                //校验通过. 更改订单状态为已支付, 修改库存
                Order order = new Order();
                String out_trade_no = params.get("out_trade_no");
                order.setOrderNum(out_trade_no);
                order.setOrderStatus("已支付");
                System.out.println("校验通过. 更改订单状态为下单成功=" + order);
                orderService.modifyOrderStatus(order);
            }
        }
    }



}
