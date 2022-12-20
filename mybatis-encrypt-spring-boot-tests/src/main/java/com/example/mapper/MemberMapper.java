package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demain.DttMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * The mybatis mapper interface of 用户信息
 */
@Mapper
public interface MemberMapper extends BaseMapper<DttMember> {

}
