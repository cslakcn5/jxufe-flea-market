package com.jxufe.service;

import com.jxufe.dto.UserDTO;
import com.jxufe.entity.Follow;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/*
 * 关注相关接口
 * @author 逍遥
 * @create 2024/11/24 下午11:19
 **/
public interface IFollowService extends IService<Follow> {

    Boolean queryFollowed(Long followedId);

    void followed(Long followedId, Boolean isFollow);

    List<UserDTO> commonUsers(Long followedId);
}
