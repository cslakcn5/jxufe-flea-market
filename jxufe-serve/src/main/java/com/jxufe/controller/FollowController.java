package com.jxufe.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.Result;
import com.jxufe.entity.Follow;
import com.jxufe.service.IFollowService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    @ApiOperation("是否关注")
    @GetMapping("/or/not/{id}")
    public Result queryFollowed(@PathVariable(name = "id") Long followedId){

        Follow follow = service.lambdaQuery().eq(Follow::getFollowUserId, followedId).eq(Follow::getUserId, BaseContext.getCurrentId()).one();
        if ( Optional.ofNullable(follow).isEmpty() ) {
            return Result.ok(false);
        }
        return Result.ok(true);
    }

    @ApiOperation("关注或取关")
    @PutMapping("/{id}/{isFollow}")
    public Result Followed(@PathVariable("id") Long followedId, @PathVariable("isFollow") Boolean isFollow){

       if( isFollow ){
       service.remove(new LambdaQueryWrapper<Follow>()
               .eq(Follow::getFollowUserId, followedId)
               .eq(Follow::getUserId, BaseContext.getCurrentId()));

       return Result.ok();
       }
       service.save(
               Follow.builder()
                       .followUserId(followedId)
                       .userId(BaseContext.getCurrentId())
                       .build());

       return Result.ok();
    }
}
