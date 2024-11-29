package com.jxufe.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxufe.constant.ExceptionConstant;
import com.jxufe.constant.RedisConstants;
import com.jxufe.constant.SystemConstants;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.LoginFormDTO;
import com.jxufe.dto.UserDTO;
import com.jxufe.entity.User;
import com.jxufe.mapper.UserMapper;
import com.jxufe.properties.JwtProperty;
import com.jxufe.service.IUserService;
import com.jxufe.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * user服务实现
 * @author 逍遥
 * @create 2024/9/25 上午11:45
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtProperty jwtProperty;


    /*
     * 发送验证码并自动注册账号
     * @param phone
     * @return void
     * @author 逍遥
     * @create 2024/9/26 下午6:14
     **/
    public void sendCode(String phone) {

        //1.发送手机验证码
        String code = RandomUtil.randomString(6);
        log.info("用户登录的验证码为:{}", code);
        //2.将手机验证码存入redis中并且5分钟后失效
        redisTemplate.opsForValue().set( RedisConstants.LOGIN_CODE_KEY + phone, code, 5, TimeUnit.MINUTES);
    }

   /*
    * 登录并校验
    * @param loginForm
    * @param session
    * @return void
    * @author 逍遥
    * @create 2024/9/26 下午6:14
    **/
    public String login(LoginFormDTO loginForm) {

        String code = redisTemplate.opsForValue().get( RedisConstants.LOGIN_CODE_KEY + loginForm.getPhone()).toString();

        // 1.校验验证码
        if ( RegexUtils.isCodeInvalid(loginForm.getCode()) && loginForm.getCode().equals(code)) {
            throw new RuntimeException(ExceptionConstant.CODE_EXCEPTION);
        }

        // 2.如果存在就通过，不存在就注册账号并随机生成昵称
        if( !lambdaQuery().eq(User::getPhone, loginForm.getPhone()).exists() ){
            String userName = SystemConstants.USER_NICK_NAME_PREFIX + UUID.randomUUID().toString().substring(0,10);
            userMapper.insert(User.builder().phone(loginForm.getPhone())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .nickName(userName).build());
        }

        // 3.查找对应用户,并进行验证
        User user = lambdaQuery().eq(User::getPhone, loginForm.getPhone()).one();
        if( Optional.ofNullable(user).isEmpty() ){
            throw new RuntimeException(ExceptionConstant.User_EXCEPTION);
        }

        // 4.验证成功后将用户信息保存在redis中, 将jwt返回字符串截取一部分作为key
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO);
        String jwt = JwtUtil.createJWT(jwtProperty.getSecretKey(), jwtProperty.getTtl(), map);
        String token = StringUtil.subString(jwt, SystemConstants.TOKEN_NUMBER);

        // 存储随机生成的验证码
        redisTemplate.opsForHash().putAll( RedisConstants.LOGIN_USER_KEY + token, map);
        redisTemplate.expire( RedisConstants.LOGIN_USER_KEY +token, 30, TimeUnit.MINUTES);

        // 5.生成随机token作为key并返回
        return jwt;
    }

   /*
    * 用户签到
    * @author 逍遥
    * @create 2024/11/29 下午4:34
    **/
    public void sign() {

        redisTemplate.opsForValue().setBit
                (RedisConstants.USER_SIGN_KEY + BaseContext.getCurrentId() + ":" +
                                LocalDate.now().getYear() +
                                LocalDate.now().getMonthValue(),
                        LocalDate.now().getDayOfMonth() - 1, true);
    }
}
