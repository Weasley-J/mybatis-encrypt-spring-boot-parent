package com.example;

import cn.alphahub.dtt.plus.util.JacksonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.common.page.PageWrapper;
import com.example.demain.DttMember;
import com.example.service.MemberService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class EncryptionTestsAppTests {

    @Autowired
    private MemberService memberService;

    @Test
    void contextLoads() {
    }

    @Test
    void testBatchInsert() throws IOException {
        String resource = IOUtils.resourceToString("member.json", StandardCharsets.UTF_8, Thread.currentThread().getContextClassLoader());
        PageWrapper<DttMember> pageWrapper = JacksonUtil.readValue(resource, new TypeReference<PageWrapper<DttMember>>() {
        });

        List<DttMember> list = pageWrapper.getList();
        List<DttMember> insertList = new ArrayList<>();
        int i = 1000;
        Long id = null;

        DttMember one = memberService.getOne(new QueryWrapper<DttMember>().select("MAX(member_id) memberId"));
        if (null != one) {
            id = one.getMemberId();
        } else id = 1L;

        for (DttMember dttMember : list) {
            i += 1;
            id += 1;
            dttMember.setMemberId(id + 1);
            dttMember.setNickname(dttMember.getNickname() + i);
            insertList.add(dttMember);
        }

        boolean batch = memberService.saveBatch(insertList);
        Assert.isTrue(batch);
    }

    @Test
    void testSelectByEncryptFiled() {
        List<DttMember> members = this.memberService.list(Wrappers.lambdaQuery(DttMember.class)
                .eq(DttMember::getNickname, "蒋震南1005")
                .eq(DttMember::getOpenId, "fawezOE5sT")
        );
        System.out.println(JacksonUtil.toPrettyJson(members));
    }
}
