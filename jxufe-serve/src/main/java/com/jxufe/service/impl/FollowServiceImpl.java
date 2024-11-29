package com.jxufe.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxufe.constant.RedisConstants;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.UserDTO;
import com.jxufe.entity.Follow;
import com.jxufe.mapper.FollowMapper;
import com.jxufe.mapper.UserMapper;
import com.jxufe.service.IFollowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxufe.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * 关注相关接口实现
 * @author 逍遥
 * @create 2024/11/24 下午11:19
 **/
@Service
@RequiredArgsConstructor
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements IFollowService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final IUserService userService;

    /*
     * 自己是否关注当前用户
     * @param followedId
     * @return java.lang.Boolean
     * @author 逍遥
     * @create 2024/11/26 下午5:05
     **/
    public Boolean queryFollowed(Long followedId) {

        // 1.先判断缓存中是否存在
        String followedUser = RedisConstants.FOLLOWED_USER_KEY + followedId;
        Boolean exist = redisTemplate.opsForSet().isMember(followedUser, BaseContext.getCurrentId());
        // 2.存在则查询redis
        if (Boolean.TRUE.equals(exist)) {
            return true;
        }
        // 3.不存在则查询mysql
        Follow info = lambdaQuery().eq(Follow::getFollowUserId, followedId).eq(Follow::getUserId, BaseContext.getCurrentId()).one();
        // 3.1 判断mysql中是否存在
        if (ObjUtil.isEmpty(info)) {
            // 3.2 不存在则直接返回
            return false;
        }
        // 3.3 存在则写入缓存并返回
        redisTemplate.opsForSet().add(followedUser, BaseContext.getCurrentId());
        return true;

    }

    /*
     * 关注功能
     * @param followedId
     * @param isFollow
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/26 下午5:08
     **/
    public void followed(Long followedId, Boolean isFollow) {

        String followedUser = RedisConstants.FOLLOWED_USER_KEY + followedId;
        // 1.判断
        if (!isFollow) {
            // 2.如果是取关则删除数据库后删除缓存
            remove(new LambdaQueryWrapper<Follow>()
                    .eq(Follow::getFollowUserId, followedId)
                    .eq(Follow::getUserId, BaseContext.getCurrentId()));
            redisTemplate.opsForSet().remove(followedUser, BaseContext.getCurrentId());
        }
        // 3.如果是关注，则存入mysql后再存入缓存
        else {
            save(
                    Follow.builder()
                            .followUserId(followedId)
                            .userId(BaseContext.getCurrentId())
                            .build());
            redisTemplate.opsForSet().add(followedUser, BaseContext.getCurrentId());
        }
    }

    /*
     * 查询共同好友
     * @param id
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/26 下午5:26
     **/
    public List<UserDTO> commonUsers(Long followedId) {

        // 1.从redis中查询共同好友
        String followedUser = RedisConstants.FOLLOWED_USER_KEY + followedId;
        String followedMe = RedisConstants.FOLLOWED_USER_KEY + BaseContext.getCurrentId();
        Set<Object> intersect = redisTemplate.opsForSet().intersect(followedMe, followedUser);
        // 2.没有则返回
        if ( ObjUtil.isEmpty(intersect) ){
            return null;
        }
        // 3.有则查询数据然后转化为dto
        List<Long> collect = intersect.stream().map(String::valueOf)
                .collect(Collectors.toList())
                .stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        return   userService.
                listByIds(collect).stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
    }


}
