# mybatis-encrypt-spring-boot-parent

**A spring-boot starter to encrypt and decrypt some column of database**

## [![Maven Central](https://img.shields.io/maven-central/v/io.github.weasley-j/mybatis-encrypt-spring-boot-starter)](https://search.maven.org/artifact/io.github.weasley-j/mybatis-encrypt-spring-boot-starter)

Tips: For running test application I recommend you to set the correct path that you can get a cleanning project directory.

- Set value of `spring.datasource.url`

```yaml
spring:
  datasource:
    embedded-database-connection: h2
    driver-class-name: org.h2.Driver
    #url: jdbc:h2:mem:h2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    url: jdbc:h2:/Users/weasley/Development/IdeaProjects/mybatis-encrypt-spring-boot-parent/mybatis-encrypt-spring-boot-tests/h2;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: ""
    password: ""
```

- Set value for  `alphahub.dtt.all-in-one-table.filepath` and `alphahub.dtt.code-generator.module-path`

```yaml
alphahub:
  dtt:
    show-sql: on
    all-in-one-table:
      enable: true
      filename: all_in_one.sql
      filepath: /Users/weasley/Downloads
    code-generator:
      is-enable: on
      include-controller: on
      include-interface: on
      override-exists: off
      remove-prefix: dtt
      base-classes:
        - com.example.demain.DttMember
      module-name: example
      module-package: com.example
      module-path: /Users/weasley/Development/IdeaProjects/mybatis-encrypt-spring-boot-parent/mybatis-encrypt-spring-boot-tests
```



# Quick start



## 1 Add dependences  for your project pom.xml

```xml
<!-- mybatis-encrypt -->
<dependency>
    <groupId>io.github.weasley-j</groupId>
    <artifactId>mybatis-encrypt-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```



## 2 Configure your configuration yaml file

[`MyBatis` encryption configuration](https://github.com/Weasley-J/mybatis-encrypt-spring-boot-parent/blob/main/mybatis-encrypt-spring-boot-starter/src/main/java/io/github/weasleyj/mybatis/encrypt/config/MybatisEncryptProperties.java)，2 algorithms embeded. you can choose one of them. Or you can implement you own algorithm.

### 2.1 Base64 algorithm

```yaml
# mybatis encryption configuration
mybatis:
  encrypt:
    enable: on
    encrypt-type: base64
```

###  2.2 AES algorithm

**Replace your own key and keyIv**

```yaml
# mybatis encryption configuration
mybatis:
  encrypt:
    enable: on
    encrypt-type: aes
    aes:
      key: Jidkdp1mWL1tRyK=
      key-iv: Poikdp1mWL1jijK=
```

## 2.3 DIY algorithm

As you choose `encrypt-type = diy`，you must implement [`io.github.weasleyj.mybatis.encrypt.core.EncryptStrategy`](https://github.com/Weasley-J/mybatis-encrypt-spring-boot-parent/blob/main/mybatis-encrypt-spring-boot-starter/src/main/java/io/github/weasleyj/mybatis/encrypt/core/EncryptStrategy.java) to define your own algorithms for encryption and decryption some column of database,

```yaml
# mybatis encryption configuration
mybatis:
  encrypt:
    enable: on
    encrypt-type: diy
    diy:
      encrypt-strategy: com.somepackage.YourEncryptStrategyImpl
```



3 Use `@EnableMybatisEncryption` annotation to annotate a `Bean` of your application that `Spring AOC` can find it

- Annotate on Java configuration class

```java
import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis Encrypt Configuration class
 *
 * @author weasley
 * @version 1.0.0
 */
@Configuration
@EnableMybatisEncryption
public class MybatisEncryptConfig {

}
```

- Annotate on your SpringBoot aplciation Main class

```java
import io.github.weasleyj.mybatis.encrypt.annotation.EnableMybatisEncryption;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Some application Main class
 */
@SpringBootApplication
@EnableMybatisEncryption
public class SomeApplication {
    public static void main(String[] args) {
        SomeApplication.run(SomeApplication.class, args);
    }
}
```



## 3 Wirte a Java bean that  MyBatis can map with table column

- Use `@Encryption` annotate on the field property which you need encryption and decryption，Here is an example to decrypt and encrypt `nickname`:

[API link](https://github.com/Weasley-J/mybatis-encrypt-spring-boot-parent/blob/8500f7454d5ec150b5ece6549f4608b38183af19/mybatis-encrypt-spring-boot-tests/src/main/java/com/example/controller/MemberController.java#L112): http://localhost:8080/api/member/save/direct



```java
import com.baomidou.mybatisplus.annotation.TableId;
import io.github.weasleyj.mybatis.encrypt.annotation.Encryption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 用户信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class DttMember implements Serializable {
    /**
     * 主键id
     *
     * @primaryKey
     */
    @TableId
    private Long memberId;
    /**
     * 用户openId
     *
     * @defaultValue e1be63305
     * @length 16
     */
    private String openId;
    /**
     * 用户昵称
     *
     * @length 32
     */
    @Encryption
    private String nickname;
    /**
     * 是否启用, 默认：1
     *
     * @defaultValue true
     */
    private Boolean isEnable;
    /**
     * 用户积分余额, 默认：0.00
     *
     * @precision 10
     * @scale 4
     * @defaultValue 0.01
     */
    private BigDecimal balance;
    /**
     * 出生日期，格式：yyyy-MM-dd HH:mm:ss
     */
    private LocalDateTime birthday;
    /**
     * 用户状态；0 正常(默认)，1 已冻结，2 账号已封，3 账号异常
     *
     * @defaultValue 3
     */
    private Integer status;
    /**
     * 账户注销状态；0 未注销（默认），1 已销户
     *
     * @defaultValue 1
     * @dbDataType SMALLINT
     */
    private Integer deleted;
    /**
     * 注册时间，格式: yyyy-MM-dd
     */
    private LocalDate registrarDate;
    /**
     * 会员加速开始时间, 格式：HH:mm:ss
     */
    private LocalTime accelerateBeginTime;
    /**
     * 会员加速结束时间, 格式：HH:mm:ss
     */
    private LocalTime accelerateEndTime;
    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
```

- When sql with type of  `	INSERT`，`UPDATE` Executed by `MyBatis` Executor, `nickname` will be encrypted, i.e:

```log
Creating a new SqlSession
Registering transaction synchronization for SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@69693f7a]
JDBC Connection [HikariProxyConnection@1043294096 wrapping conn1: url=jdbc:h2:/Users/weasley/Development/IdeaProjects/mybatis-encrypt-spring-boot-parent/mybatis-encrypt-spring-boot-tests/h2 user=] will be managed by Spring
==>  Preparing: SELECT MAX(member_id) memberId FROM dtt_member
==> Parameters: 
<==    Columns: MEMBERID
<==        Row: 5
<==      Total: 1
Releasing transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@69693f7a]
2022-12-20 17:30:27.737  INFO 15312 --- [nio-8080-exec-1] com.example.controller.MemberController  : {"memberId":6,"openId":"fawezOE5sT","nickname":"蒋震南","isEnable":true,"balance":865,"birthday":"2022-12-20 17:30:27","status":0,"deleted":1,"registrarDate":"2022-12-20","accelerateBeginTime":"17:30:27","accelerateEndTime":"17:30:27","updateTime":"2022-12-20 17:30:27"}
Fetched SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@69693f7a] from current transaction
2022-12-20 17:30:27.822 DEBUG 15312 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Encrypt field for 'nickname', Plaintext: 蒋震南, Ciphertext: IgaLfu2eBut5jAKENLZd3A==
==>  Preparing: INSERT INTO dtt_member ( member_id, open_id, nickname, is_enable, balance, birthday, status, deleted, registrar_date, accelerate_begin_time, accelerate_end_time, update_time ) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
==> Parameters: 6(Long), fawezOE5sT(String), IgaLfu2eBut5jAKENLZd3A==(String), true(Boolean), 865(BigDecimal), 2022-12-20T17:30:27.558(LocalDateTime), 0(Integer), 1(Integer), 2022-12-20(LocalDate), 17:30:27.558(LocalTime), 17:30:27.558(LocalTime), 2022-12-20T17:30:27.558(LocalDateTime)
<==    Updates: 1
```

Real SQL:

```sql
INSERT INTO dtt_member ( member_id, open_id, nickname, is_enable, balance, birthday, status, deleted, registrar_date, accelerate_begin_time, accelerate_end_time, update_time ) VALUES ( 6, 'fawezOE5sT', 'IgaLfu2eBut5jAKENLZd3A==', true, 865, '2022-12-20T17:30:27.558', 0, 1, '2022-12-20', '17:30:27.558', '17:30:27.558', '2022-12-20T17:30:27.558' );

```



- When sql with type of  `	SELECT` Executed by `MyBatis` Executor, `nickname` will be decrypted, i.e:

[API link](https://github.com/Weasley-J/mybatis-encrypt-spring-boot-parent/blob/8500f7454d5ec150b5ece6549f4608b38183af19/mybatis-encrypt-spring-boot-tests/src/main/java/com/example/controller/MemberController.java#L53): http://localhost:8080/api/member/page/pagehelper?pageNum=1&pageSize=10

```log
JDBC Connection [HikariProxyConnection@670142194 wrapping conn1: url=jdbc:h2:/Users/weasley/Development/IdeaProjects/mybatis-encrypt-spring-boot-parent/mybatis-encrypt-spring-boot-tests/h2 user=] will not be managed by Spring
==>  Preparing: SELECT count(0) FROM dtt_member
==> Parameters: 
<==    Columns: COUNT(*)
<==        Row: 6
<==      Total: 1
==>  Preparing: SELECT member_id,open_id,nickname,is_enable,balance,birthday,status,deleted,registrar_date,accelerate_begin_time,accelerate_end_time,update_time FROM dtt_member LIMIT ?
==> Parameters: 10(Integer)
<==    Columns: MEMBER_ID, OPEN_ID, NICKNAME, IS_ENABLE, BALANCE, BIRTHDAY, STATUS, DELETED, REGISTRAR_DATE, ACCELERATE_BEGIN_TIME, ACCELERATE_END_TIME, UPDATE_TIME
<==        Row: 1, fawezOE5sT, IgaLfu2eBut5jAKENLZd3A==, TRUE, 865.0000, 2022-12-20 13:33:46.547, 0, 1, 2022-12-20, 13:33:47, 13:33:47, 2022-12-20 13:33:46.547
<==        Row: 2, fawezOE5sT, IgaLfu2eBut5jAKENLZd3A==, TRUE, 865.0000, 2022-12-20 13:33:49.226, 0, 1, 2022-12-20, 13:33:49, 13:33:49, 2022-12-20 13:33:49.226
<==        Row: 3, fawezOE5sT, IgaLfu2eBut5jAKENLZd3A==, TRUE, 865.0000, 2022-12-20 13:33:50.398, 0, 1, 2022-12-20, 13:33:50, 13:33:50, 2022-12-20 13:33:50.398
<==        Row: 4, fawezOE5sT, IgaLfu2eBut5jAKENLZd3A==, TRUE, 865.0000, 2022-12-20 13:33:51.319, 0, 1, 2022-12-20, 13:33:51, 13:33:51, 2022-12-20 13:33:51.319
<==        Row: 5, fawezOE5sT, IgaLfu2eBut5jAKENLZd3A==, TRUE, 865.0000, 2022-12-20 13:33:52.31, 0, 1, 2022-12-20, 13:33:52, 13:33:52, 2022-12-20 13:33:52.31
<==        Row: 6, fawezOE5sT, IgaLfu2eBut5jAKENLZd3A==, TRUE, 865.0000, 2022-12-20 17:30:27.558, 0, 1, 2022-12-20, 17:30:28, 17:30:28, 2022-12-20 17:30:27.558
<==      Total: 6
2022-12-20 17:45:12.534 DEBUG 15634 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Decrypt field for 'nickname', Ciphertext: IgaLfu2eBut5jAKENLZd3A==, Plaintext: 蒋震南
2022-12-20 17:45:12.534 DEBUG 15634 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Decrypt field for 'nickname', Ciphertext: IgaLfu2eBut5jAKENLZd3A==, Plaintext: 蒋震南
2022-12-20 17:45:12.534 DEBUG 15634 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Decrypt field for 'nickname', Ciphertext: IgaLfu2eBut5jAKENLZd3A==, Plaintext: 蒋震南
2022-12-20 17:45:12.535 DEBUG 15634 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Decrypt field for 'nickname', Ciphertext: IgaLfu2eBut5jAKENLZd3A==, Plaintext: 蒋震南
2022-12-20 17:45:12.535 DEBUG 15634 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Decrypt field for 'nickname', Ciphertext: IgaLfu2eBut5jAKENLZd3A==, Plaintext: 蒋震南
2022-12-20 17:45:12.535 DEBUG 15634 --- [nio-8080-exec-1] w.m.e.i.DefaultMybatisEncryptInterceptor : Decrypt field for 'nickname', Ciphertext: IgaLfu2eBut5jAKENLZd3A==, Plaintext: 蒋震南
Closing non transactional SqlSession [org.apache.ibatis.session.defaults.DefaultSqlSession@4b849667]
```

SQL

```sql
SELECT member_id,open_id,nickname,is_enable,balance,birthday,status,deleted,registrar_date,accelerate_begin_time,accelerate_end_time,update_time FROM dtt_member LIMIT 10;
```

Finally, you can get some results  like this:

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "total": 6,
  "pages": 1,
  "list": [
    {
      "memberId": 1,
      "openId": "fawezOE5sT",
      "nickname": "蒋震南",
      "isEnable": true,
      "balance": 865.0000,
      "birthday": "2022-12-20T13:33:46.547",
      "status": 0,
      "deleted": 1,
      "registrarDate": "2022-12-20",
      "accelerateBeginTime": "13:33:47",
      "accelerateEndTime": "13:33:47",
      "updateTime": "2022-12-20T13:33:46.547"
    },
    {
      "memberId": 2,
      "openId": "fawezOE5sT",
      "nickname": "蒋震南",
      "isEnable": true,
      "balance": 865.0000,
      "birthday": "2022-12-20T13:33:49.226",
      "status": 0,
      "deleted": 1,
      "registrarDate": "2022-12-20",
      "accelerateBeginTime": "13:33:49",
      "accelerateEndTime": "13:33:49",
      "updateTime": "2022-12-20T13:33:49.226"
    },
    {
      "memberId": 3,
      "openId": "fawezOE5sT",
      "nickname": "蒋震南",
      "isEnable": true,
      "balance": 865.0000,
      "birthday": "2022-12-20T13:33:50.398",
      "status": 0,
      "deleted": 1,
      "registrarDate": "2022-12-20",
      "accelerateBeginTime": "13:33:50",
      "accelerateEndTime": "13:33:50",
      "updateTime": "2022-12-20T13:33:50.398"
    },
    {
      "memberId": 4,
      "openId": "fawezOE5sT",
      "nickname": "蒋震南",
      "isEnable": true,
      "balance": 865.0000,
      "birthday": "2022-12-20T13:33:51.319",
      "status": 0,
      "deleted": 1,
      "registrarDate": "2022-12-20",
      "accelerateBeginTime": "13:33:51",
      "accelerateEndTime": "13:33:51",
      "updateTime": "2022-12-20T13:33:51.319"
    },
    {
      "memberId": 5,
      "openId": "fawezOE5sT",
      "nickname": "蒋震南",
      "isEnable": true,
      "balance": 865.0000,
      "birthday": "2022-12-20T13:33:52.31",
      "status": 0,
      "deleted": 1,
      "registrarDate": "2022-12-20",
      "accelerateBeginTime": "13:33:52",
      "accelerateEndTime": "13:33:52",
      "updateTime": "2022-12-20T13:33:52.31"
    },
    {
      "memberId": 6,
      "openId": "fawezOE5sT",
      "nickname": "蒋震南",
      "isEnable": true,
      "balance": 865.0000,
      "birthday": "2022-12-20T17:30:27.558",
      "status": 0,
      "deleted": 1,
      "registrarDate": "2022-12-20",
      "accelerateBeginTime": "17:30:28",
      "accelerateEndTime": "17:30:28",
      "updateTime": "2022-12-20T17:30:27.558"
    }
  ]
}
```
