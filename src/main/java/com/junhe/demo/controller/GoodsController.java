package com.junhe.demo.controller;

import com.junhe.demo.model.Goods;
import com.junhe.demo.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 商品查询
     */
    @RequestMapping(value = "/findgoods",method = RequestMethod.GET)
    private Map<String, Object> findGoods(@RequestParam(value = "goodsArea") String goodsArea){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        List<Goods> list = new ArrayList<Goods>();
        list = goodsService.searchGoods(goodsArea);
           modelMap.put("goods",list);
           return modelMap;

    }
//色带查询
    @RequestMapping(value = "/getgoods",method = RequestMethod.GET)
    private Map<String, Object> findGood(@RequestParam(value = "goodsName") String goodsName){
        Map<String, Object> modelMap = new HashMap<String, Object>();
        Goods good = goodsService.searchGood(goodsName);
        modelMap.put("goods",good);
        return modelMap;
    }
}
