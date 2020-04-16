package com.junhe.demo.service;

import com.junhe.demo.mapper.BillMapper;
import com.junhe.demo.model.Bill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillService {
    @Autowired
    private BillMapper billMapper;

//    新增发票请求
    public boolean addBill(Bill bill){
        int effectedNum = billMapper.insertOrder(bill);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("添加发票请求失败!");
        }
    }

    //    更新申购单信息
    public boolean addSubscribe(String subscribe,String orderNum){
        int effectedNum = billMapper.uopdateByorderNum(subscribe,orderNum);
        if (effectedNum > 0) {
            return true;
        } else {
            throw new RuntimeException("更新申购单请求失败!");
        }
    }

//    查询发票
    public Bill queryOrder(String orderNum){
        return billMapper.queryBillByorderNum(orderNum);
    }
}
