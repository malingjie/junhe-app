package com.junhe.demo.mapper;

import com.junhe.demo.model.Goods;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GoodsMapper {

    List<Goods> queryGoodsByArea(String goodsArea);

    Goods queryGoodsByName(String goodsName);

    Double queryPriceByCode(String goodsCode);
}
