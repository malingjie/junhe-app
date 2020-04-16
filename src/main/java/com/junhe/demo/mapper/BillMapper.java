package com.junhe.demo.mapper;

import com.junhe.demo.model.Bill;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BillMapper {

    int insertOrder(Bill bill);

    Bill queryBillByorderNum(String orderNum);

    int uopdateByorderNum(@Param("subscribe") String subscribe,@Param("orderNum") String orderNum);
}
