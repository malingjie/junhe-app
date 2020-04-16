package com.junhe.demo.service;

import com.junhe.demo.mapper.PayMapper;
import com.junhe.demo.model.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    @Autowired
    private PayMapper payMapper;

    public SecretKey searchKey(){
        return payMapper.queryKey();
    }
}
