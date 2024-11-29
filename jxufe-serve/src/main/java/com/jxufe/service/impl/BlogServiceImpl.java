package com.jxufe.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxufe.constant.RedisConstants;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.ScrollResult;
import com.jxufe.entity.Blog;
import com.jxufe.entity.Follow;
import com.jxufe.entity.User;
import com.jxufe.mapper.BlogMapper;
import com.jxufe.mapper.FollowMapper;
import com.jxufe.mapper.UserMapper;
import com.jxufe.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxufe.constant.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * 博客服务实现
 * @return
 * @author 逍遥
 * @create 2024/11/28 下午4:14
 **/
@Service
@RequiredArgsConstructor
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    private final BlogMapper blogMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserMapper userMapper;

    private final FollowMapper followMapper;

    /*
     * 修改点赞
     * @param id
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/21 上午10:44
     **/
    public void updateBlog(Long id) {

        //1.检验id是否存在
        if( Optional.ofNullable( blogMapper.selectById(id) ).isEmpty() ) {
            throw new RuntimeException("该博客不存在");
        }
        //2.判断是否点过赞，点过则取消，没点过则点
        String name = SystemConstants.BLOG_LIKE + id;
        int number = blogMapper.selectById(id).getLiked();
        Boolean blogLiked = redisTemplate.opsForSet().isMember(name, BaseContext.getCurrentId());

        if ( Boolean.TRUE.equals(blogLiked)) {

            lambdaUpdate().eq( Blog::getId, id).eq( Blog::getLiked, number).set( Blog::getLiked, number - 1).update();
            redisTemplate.delete(name);
        }
        else {
            redisTemplate.opsForSet().add( name, BaseContext.getCurrentId());
            lambdaUpdate().eq( Blog::getId, id).eq( Blog::getLiked, number).set( Blog::getLiked, number + 1).update();
        }

    }

    /*
     * 保存博客
     * @param blog
     * @return java.lang.Long
     * @author 逍遥
     * @create 2024/11/28 下午4:36
     **/
    public Long saveBlog(Blog blog) {

        // 获取登录用户
        Long userId =  BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);
        blog.setUserId(user.getId());

        // 保存探店博文
        save(blog);
        List<Follow> follows = followMapper.selectList(new LambdaQueryWrapper<Follow>().eq(Follow::getFollowUserId, userId));
        for(Follow follow : follows){
            String name = RedisConstants.FEED_KEY + follow.getUserId();
            redisTemplate.opsForZSet().add(name, blog.getId().toString(), System.currentTimeMillis());
        }
        // 返回id
        return blog.getId();
    }

    /*
     * 推送关注的博客
     * @param max
     * @param offset
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/28 下午4:53
     **/
    public ScrollResult pushFollowedBlog(Long max, Integer offset) {

        String name = RedisConstants.FEED_KEY + BaseContext.getCurrentId();
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = redisTemplate.opsForZSet().reverseRangeByScoreWithScores(name, 0, max, offset, 3);
        List<Blog> collect = typedTuples.stream().map(
                        typedTuple ->
                                blogMapper.selectOne(new LambdaQueryWrapper<Blog>().eq(Blog::getId, typedTuple.getValue())))
                .collect(Collectors.toList());

        return ScrollResult.builder()
                .list(collect)
                .offset(1)
                .build();
    }


}
