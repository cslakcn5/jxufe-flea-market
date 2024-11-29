package com.jxufe.controller;

import com.jxufe.dto.Result;
import com.jxufe.service.IFollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/*
 * 关注相关接口
 * @author 逍遥
 * @create 2024/11/24 下午11:19
 **/
@RestController
@RequestMapping("/follow")
@Api(tags = "用户关注功能")
public class FollowController {

    @Autowired
    private IFollowService service;

    /*
     * 自己是否关注当前用户
     * @param followedId
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/26 下午5:04
     **/
    @ApiOperation("是否关注")
    @GetMapping("/or/not/{id}")
    public Result<Object> queryFollowed(@PathVariable(name = "id") Long followedId){

        return Result.ok(service.queryFollowed(followedId));
    }

    /*
     * 关注功能
     * @param followedId
     * @param isFollow
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/26 下午5:08
     **/
    @ApiOperation("关注或取关")
    @PutMapping("/{id}/{isFollow}")
    public Result<Object> followed(@PathVariable("id") Long followedId, @PathVariable("isFollow") Boolean isFollow) {

        service.followed(followedId, isFollow);
        return Result.ok();
    }

    /*
     * 查询共同好友
     * @param id
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/26 下午5:26
     **/
    @ApiOperation("查询共同好友")
    @GetMapping("/common/{id}")
    public Result<Object> commonUsers(@PathVariable("id") Long followedId){

        return Result.ok(service.commonUsers(followedId));
    }
}
