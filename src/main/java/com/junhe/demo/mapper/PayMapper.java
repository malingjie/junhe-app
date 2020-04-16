package com.junhe.demo.mapper;

import com.junhe.demo.model.SecretKey;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayMapper {

    SecretKey queryKey();

}
