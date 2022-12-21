package com.example.controller;

import cn.alphahub.dtt.plus.util.JacksonUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.common.page.PageHandler;
import com.example.common.page.PageWrapper;
import com.example.demain.DttMember;
import com.example.service.MemberService;
import com.github.pagehelper.page.PageMethod;
import io.github.weasleyj.mybatis.encrypt.config.MybatisEncryptProperties;
import io.github.weasleyj.mybatis.encrypt.core.EncryptStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

/**
 * 用户信息
 *
 * @author Weasley
 */
@Slf4j
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private MybatisEncryptProperties mybatisEncryptProperties;

    /**
     * 用户信息分页查询(Pagehelper写法)
     *
     * @param member   用户信息查询参数
     * @param pageNum  前页码, 默认: 1
     * @param pageSize 每页显示条数，默认: 10
     * @return 用户信息分页数据
     * @apiNote 请求示例: <a href="http://localhost:8080/api/member/page/pagehelper?pageNum=1&pageSize=2">请求示例</a>
     * @see <a href="https://github.com/pagehelper/pagehelper-spring-boot">多数据源分页推荐使用pagehelper</a>
     * @see <a href="https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md">pagehelper多数据源分页配置</a>
     * @see <a href="https://github.com/Weasley-J/lejing-mall/blob/main/lejing-common/lejing-common-base-service/src/main/java/cn/alphahub/mall/common/page/PageWrapper.java">PageWrapper类源码</a>
     * @see <a href="https://github.com/Weasley-J/lejing-mall/blob/main/lejing-common/lejing-common-base-service/src/main/java/cn/alphahub/mall/common/page/PageHandler.java">PageHandler类源码</a>
     */
    @GetMapping("/page/pagehelper")
    public ResponseEntity<PageWrapper<DttMember>> pageByPagehelper(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                                                                   @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize,
                                                                   @ModelAttribute(value = "member", binding = true) DttMember member) {
        com.github.pagehelper.Page<DttMember> page = PageMethod.startPage(pageNum, pageSize);
        List<DttMember> pageResults = memberService.list(Wrappers.lambdaQuery(member));
        return ResponseEntity.ok(PageHandler.render(page, pageResults));
    }

    /**
     * 用户信息分页查询(Mybatis-Plus写法)
     *
     * @param pageParam 分页参数: current(当前页码,默认1), size(每页显示条数，默认10)
     * @param member    用户信息查询参数
     * @return 用户信息分页数据
     * @apiNote 请求示例: <a href="http://localhost:8080/api/member/page/mmp?current=1&size=3">请求示例</a>
     * @see <a href="https://mp.baomidou.com/guide/page.html">Mybatis Plus官方分页插件配置示例</a>
     * @see <a href="https://github.com/pagehelper/pagehelper-spring-boot">多数据源分页推荐使用pagehelper</a>
     */
    @GetMapping("/page/mmp")
    public ResponseEntity<Page<DttMember>> pageByMmp(@ModelAttribute("pageParam") PageDTO<DttMember> pageParam, @ModelAttribute("member") DttMember member) {
        Page<DttMember> params = new Page<>();
        params.setSize(pageParam.getSize());
        params.setCurrent(pageParam.getCurrent());
        Page<DttMember> page = memberService.page(params, Wrappers.lambdaQuery(member));
        return ResponseEntity.ok(page);
    }

    /**
     * Use encrypted fields as query criteria
     */
    @GetMapping("/lis/encrypted/fields")
    public ResponseEntity<List<DttMember>> selectByEncryptedFields(@ModelAttribute("member") DttMember member) {
        log.info("{}", JacksonUtil.toPrettyJson(member));
        DttMember dttMember = EncryptStrategy.convert(member, mybatisEncryptProperties.getEncryptType());
        log.info("EncryptStrategy.convert {}", JacksonUtil.toPrettyJson(dttMember));
        List<DttMember> members = this.memberService.list(Wrappers.lambdaQuery(DttMember.class)
                .eq(DttMember::getNickname, dttMember.getNickname()));
        return ResponseEntity.ok(members);
    }

    /**
     * 获取用户信息详情
     *
     * @param memberId 用户信息主键id
     * @return 用户信息详细信息
     */
    @GetMapping("/info/{memberId}")
    public ResponseEntity<DttMember> info(@PathVariable("memberId") Long memberId) {
        DttMember member = memberService.getById(memberId);
        return ResponseEntity.ok(member);
    }

    /**
     * 新增用户信息
     *
     * @param member 用户信息元数据
     * @return 成功返回true, 失败返回false
     */
    @PostMapping("/save")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Boolean> save(@RequestBody @Validated DttMember member) {
        boolean save = memberService.save(member);
        return ResponseEntity.ok(save);
    }

    /**
     * 新增用户信息
     *
     * @return 成功返回true, 失败返回false
     */
    @PostMapping("/save/direct")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Boolean> saveNoParams() {
        String json = "{\n" +
                "  \"openId\": \"fawezOE5sT\",\n" +
                "  \"nickname\": \"蒋震南\",\n" +
                "  \"isEnable\": true,\n" +
                "  \"balance\": 865,\n" +
                "  \"birthday\": \"2022-08-19 22:18:51\",\n" +
                "  \"status\": 0,\n" +
                "  \"deleted\": 1\n" +
                "}";
        DttMember member = JacksonUtil.readValue(json, DttMember.class);
        member.setBirthday(LocalDateTime.now());
        member.setRegistrarDate(LocalDate.now());
        member.setUpdateTime(LocalDateTime.now());
        member.setAccelerateBeginTime(LocalTime.now());
        member.setAccelerateEndTime(LocalTime.now());
        DttMember dttMember = memberService.getOne(new QueryWrapper<DttMember>().select("MAX(member_id) memberId"));
        if (null != dttMember) {
            member.setMemberId(dttMember.getMemberId() + 1);
        } else member.setMemberId(1L);
        log.info("{}", JacksonUtil.toJson(member));
        boolean save = memberService.save(member);
        return ResponseEntity.ok(save);
    }

    /**
     * 修改用户信息
     *
     * @param member 用户信息, 根据id选择性更新
     * @return 成功返回true, 失败返回false
     */
    @PutMapping("/update")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Boolean> update(@RequestBody @Validated DttMember member) {
        boolean update = memberService.updateById(member);
        return ResponseEntity.ok(update);
    }

    /**
     * 批量删除用户信息
     *
     * @param memberIds 用户信息id集合
     * @return 成功返回true, 失败返回false
     */
    @DeleteMapping("/delete/{memberIds}")
    @Transactional(rollbackFor = {Exception.class})
    public ResponseEntity<Boolean> delete(@PathVariable("memberIds") Long[] memberIds) {
        boolean delete = memberService.removeByIds(Arrays.asList(memberIds));
        return ResponseEntity.ok(delete);
    }
}
