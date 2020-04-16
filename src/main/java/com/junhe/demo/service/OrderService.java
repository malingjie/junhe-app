package com.junhe.demo.service;

import com.junhe.demo.mapper.OrderMapper;
import com.junhe.demo.model.Goods;
import com.junhe.demo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;

//新增订单信息
public boolean addOrder(Order order){
    int effectedNum = orderMapper.insertOrder(order);
    if (effectedNum > 0) {
        return true;
    } else {
        throw new RuntimeException("添加订单失败!");
    }
}

    //更新订单支付状态
    public boolean delUserOrder(String orderNum){
        int effectedNum = orderMapper.delUserOrder(orderNum);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("更新订单支付状态失败!");
        }
    }

    //更新订单支付状态
    public boolean modifyOrderStatus(Order order){
        int effectedNum = orderMapper.updateOrderStatus(order);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("更新订单支付状态失败!");
        }
    }



    //更新订单支付公对公转账照片与支付状态
    public boolean modifyOrderPic(String orderNum,String picName,String orderStatus,String publicStatus){
        int effectedNum = orderMapper.updateOrderPic(orderNum,picName,orderStatus,publicStatus);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("更新订单失败!");
        }
    }

//根据用户手机号查询订单
public List<Order> queryOrder(Long phoneNumber){
    return orderMapper.queryOrderByphoneNumber(phoneNumber);

}

    //根据订单号查询订单
    public Order queryOrderByOrderNum(String orderNum){
        return orderMapper.queryOrderByOrderNum(orderNum);

    }

//根据用户手机号查询公对公转账订单
public List<Order> queryBusinessOrder(Long phoneNumber){
    return orderMapper.queryBusinessOrderByphoneNumber(phoneNumber);

}

}
