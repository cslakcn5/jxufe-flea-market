package com.jxufe.service.impl;

import com.jxufe.context.BaseContext;
import com.jxufe.entity.Blog;
import com.jxufe.mapper.BlogMapper;
import com.jxufe.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxufe.constant.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@RequiredArgsConstructor
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    private final BlogMapper blogMapper;

    private final RedisTemplate<String, Object> redisTemplate;

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
}
