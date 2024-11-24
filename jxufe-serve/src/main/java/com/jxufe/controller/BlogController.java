package com.jxufe.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.Result;
import com.jxufe.entity.Blog;
import com.jxufe.entity.User;
import com.jxufe.service.IBlogService;
import com.jxufe.service.IUserService;
import com.jxufe.utils.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/*
 * @return 博客相关功能
 * @author 逍遥
 * @create 2024/11/22 上午10:26
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/blog")
public class BlogController {

    private final IBlogService blogService;

    private final IUserService userService;

    /*
     * 查询博客
     * @param id
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/21 上午10:49
     **/
    @GetMapping("{id}")
    public Result queryBlog(@PathVariable(name = "id") Integer id){

        Blog blog = blogService.lambdaQuery().eq(Blog::getId, id).one();
        User user = userService.lambdaQuery().eq(User::getId, blog.getUserId()).one();
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
        return Result.ok(blog);
    }

    /*
     * 添加博客
     * @param blog
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/21 上午10:43
     **/
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        // 获取登录用户
        Long userId =  BaseContext.getCurrentId();
        User user = userService.getById(userId);
        blog.setUserId(user.getId());
        // 保存探店博文
        blogService.save(blog);
        // 返回id
        return Result.ok(blog.getId());
    }

    /*
     * 修改点赞
     * @param id
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/21 上午10:44
     **/
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        // 修改点赞数量
        blogService.updateBlog(id);
        return Result.ok();
    }

    /*
     * 查询点赞
     * @param id
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/21 下午3:42
     **/
    @GetMapping("/likes/{id}")
    public Result queryLikeBlog(@PathVariable("id") Long id) {
        // 查询点赞

        return Result.ok( blogService.lambdaQuery().eq(Blog::getId, id).one().getLiked() );
    }
    
    /*
     *
     * @param null
     * @return 
     * @author 逍遥
     * @create 2024/11/21 下午3:41
     **/
    @GetMapping("/of/me")
    public Result queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 获取登录用户
        Long userId =  BaseContext.getCurrentId();
        User user = userService.getById(userId);
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .eq("user_id", user.getId()).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    @GetMapping("/hot")
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        // 根据用户查询
        Page<Blog> page = blogService.query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog ->{
            Long userId = blog.getUserId();
            User user = userService.getById(userId);
            blog.setName(user.getNickName());
            blog.setIcon(user.getIcon());
        });
        return Result.ok(records);
    }
}
