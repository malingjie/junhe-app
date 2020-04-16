package com.junhe.demo.service;

import com.junhe.demo.mapper.GoodsMapper;
import com.junhe.demo.model.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {
@Autowired
private GoodsMapper goodsMapper;
    //查找商品
    public List<Goods> searchGoods(String goodsArea){
        return goodsMapper.queryGoodsByArea(goodsArea);
    }

    public Goods searchGood(String goodsName) {
        return goodsMapper.queryGoodsByName(goodsName);
    }

    //根据商品编号查询价格
    public Double searchPrice(String goodsCode){
        return goodsMapper.queryPriceByCode(goodsCode);
    }
}
