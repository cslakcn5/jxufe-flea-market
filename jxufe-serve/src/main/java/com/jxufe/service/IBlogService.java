package com.jxufe.service;

import com.jxufe.dto.ScrollResult;
import com.jxufe.entity.Blog;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/*
 * 博客功能接口
 * @return
 * @author 逍遥
 * @create 2024/11/28 下午4:21
 **/
public interface IBlogService extends IService<Blog> {

    void updateBlog(Long id);

    Long saveBlog(Blog blog);

    ScrollResult pushFollowedBlog(Long max, Integer offset);
}
