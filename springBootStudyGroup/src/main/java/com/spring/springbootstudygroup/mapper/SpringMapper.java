package com.spring.springbootstudygroup.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SpringMapper {
    @Select("SELECT * FROM user")
    int findUser();
}
