package com.jxufe.controller;


import com.jxufe.constant.ExceptionConstant;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.LoginFormDTO;
import com.jxufe.dto.Result;
import com.jxufe.entity.UserInfo;
import com.jxufe.service.IUserInfoService;
import com.jxufe.service.IUserService;
import com.jxufe.utils.RegexUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Api(tags = "用户登录功能")
public class UserController {

    private final IUserService userService;

    private final IUserInfoService userInfoService;

    /**
     * 发送手机验证码
     */
    @ApiOperation("发送手机验证码")
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone) {

        log.info("正在对用户发送手机验证码");
        //进行手机格式验证
        if( RegexUtils.isPhoneInvalid(phone)) {
            return Result.fail(ExceptionConstant.PHONE_EXCEPTION);
        }
        userService.sendCode(phone);
        return Result.ok();
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm){

        log.info("正在对用户信息进行校验");
        return Result.ok(userService.login(loginForm));
    }

    /*
     * 退出
     * @return com.jxufe.dto.Result
     * @author 逍遥
     * @create 2024/11/25 下午7:36
     **/
    @ApiOperation("用户退出")
    @PostMapping("/logout")
    public Result logout(){

        return Result.ok();
    }

    /*
     * 获取当前用户
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/9/26 下午7:34
     **/
    @ApiOperation("获得当前用户")
    @GetMapping("/me")
    public Result me(){

        return Result.ok(userService.getById(BaseContext.getCurrentId()));
    }

    /*
     * 查看用户信息
     * @param userId
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/9/26 下午7:35
     **/
    @ApiOperation("查看用户信息")
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        // 查询详情
        UserInfo info = userInfoService.getById(userId);
        if (info == null) {
            // 尚未注册账号
            return Result.ok();
        }

        // 返回
        return Result.ok(info);
    }
}
