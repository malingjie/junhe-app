package com.junhe.demo.mapper;

import com.junhe.demo.model.Goods;
import com.junhe.demo.model.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Mapper
public interface OrderMapper {

    int insertOrder(Order order);

    List<Order> queryOrderByphoneNumber(Long  phoneNumber);

    Order queryOrderByOrderNum(String  orderNum);

    List<Order> queryBusinessOrderByphoneNumber(Long phoneNumber);

    int updateOrderStatus(Order order);

    int delUserOrder(String orderNUm);

    //更新订单图片
    int updateOrderPic(@Param("orderNum") String orderNum, @Param("picName") String picName, @Param("orderStatus") String orderStatus,  @Param("publicStatus") String publicStatus);
}
