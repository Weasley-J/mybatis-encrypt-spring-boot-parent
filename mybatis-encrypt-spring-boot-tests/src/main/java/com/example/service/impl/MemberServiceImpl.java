package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demain.DttMember;
import com.example.mapper.MemberMapper;
import com.example.service.MemberService;
import org.springframework.stereotype.Service;

/**
 * 用户信息Service实现
 */
@Service
public class MemberServiceImpl extends ServiceImpl<MemberMapper, DttMember> implements MemberService {

}
