package com.jxufe.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jxufe.dto.LoginFormDTO;
import com.jxufe.entity.User;


/*
 *
 * @return
 * @author 逍遥
 * @create 2024/9/25 上午11:45
 **/
public interface IUserService extends IService<User> {

    void sendCode(String phone);

    String login(LoginFormDTO loginForm);

    void sign();
}
