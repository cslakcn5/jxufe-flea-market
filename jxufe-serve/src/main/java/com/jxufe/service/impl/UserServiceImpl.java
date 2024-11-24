package com.jxufe.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/*
 * user服务实现
 * @param null
 * @return
 * @author 逍遥
 * @create 2024/9/25 上午11:45
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final UserMapper userMapper;

    private final RedisTemplate redisTemplate;

    private final JwtProperty jwtProperty;


    /*
     * 发送验证码并自动注册账号
     * @param phone
     * @param session
     * @return void
     * @author 逍遥
     * @create 2024/9/26 下午6:14
     **/
    public void sendCode(String phone, HttpSession session) {

        //1.发送手机验证码
        String code = RandomUtil.randomString(6);
        System.out.println("用户登录的验证码为:" + code);
        //2.将手机验证码存入缓存中并且5分钟后失效
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
    public String login(LoginFormDTO loginForm, HttpSession session) {

        String code = redisTemplate.opsForValue().get( RedisConstants.LOGIN_CODE_KEY + loginForm.getPhone()).toString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, loginForm.getPhone());

        //1.校验验证码
        if ( RegexUtils.isCodeInvalid(loginForm.getCode()) && loginForm.getCode().equals(code)) {
            throw new RuntimeException(ExceptionConstant.CODE_EXCEPTION);
        }

        //2.如果存在就通过，不存在就注册账号并随机生成昵称
        if( userMapper.selectOne(wrapper) == null){
            String userName = SystemConstants.USER_NICK_NAME_PREFIX + UUID.randomUUID().toString().substring(0,6);
            userMapper.insert(User.builder().phone(loginForm.getPhone())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .nickName(userName).build());
        }

        //3.查找对应用户,并进行验证
        User user = lambdaQuery().eq(User::getPhone, loginForm.getPhone()).one();
        if( Optional.ofNullable(user).isEmpty() ){
            throw new RuntimeException(ExceptionConstant.User_EXCEPTION);
        }

        //4.验证成功后将用户信息保存在redis中, 将jwt返回字符串截取一部分作为key
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> map = BeanUtil.beanToMap(userDTO);
        String jwt = JwtUtil.createJWT(jwtProperty.getSecretKey(), jwtProperty.getTtl(), map);
        String token = jwt.substring(0,6);
        redisTemplate.opsForHash().putAll( RedisConstants.LOGIN_USER_KEY + token, map);
        redisTemplate.expire( RedisConstants.LOGIN_USER_KEY +token, 30, TimeUnit.MINUTES);

        //5.生成随机token作为key并返回
        return jwt;

    }
}
