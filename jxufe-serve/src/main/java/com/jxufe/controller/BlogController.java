package com.jxufe.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxufe.context.BaseContext;
import com.jxufe.dto.Result;
import com.jxufe.entity.Blog;
import com.jxufe.entity.User;
import com.jxufe.service.IBlogService;
import com.jxufe.service.IUserService;
import com.jxufe.constant.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api( tags = "博客功能")
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
    @ApiOperation("查询博客")
    @GetMapping("{id}")
    public Result<Object> queryBlog(@PathVariable(name = "id") Integer id){

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
    @ApiOperation("添加博客")
    @PostMapping
    public Result<Object> saveBlog(@RequestBody Blog blog) {

        return Result.ok(blogService.saveBlog(blog));
    }

    /*
     * 修改点赞
     * @param id
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/21 上午10:44
     **/
    @ApiOperation("点赞")
    @PutMapping("/like/{id}")
    public Result<Object> likeBlog(@PathVariable("id") Long id) {
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
    @ApiOperation("查询点赞")
    @GetMapping("/likes/{id}")
    public Result<Object> queryLikeBlog(@PathVariable("id") Long id) {
        // 查询点赞

        return Result.ok( blogService.lambdaQuery().eq(Blog::getId, id).one().getLiked() );
    }
    
    /*
     * 查询自己的相关博客
     * @param null
     * @return 
     * @author 逍遥
     * @create 2024/11/21 下午3:41
     **/
    @ApiOperation("查询自己的相关博客")
    @GetMapping("/of/me")
    public Result<Object> queryMyBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
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
    public Result<Object> queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
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

    /*
     * 访问用户主页
     * @param current
     * @param id
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/25 下午11:08
     **/
    @ApiOperation("访问用户主页")
    @GetMapping("/of/user")
    public Result<Object> queryBlogByUserId(
            @RequestParam("current") Integer current,
            @RequestParam("id") Long id) {
        // 根据用户查询
        Page<Blog> page = blogService.lambdaQuery().eq(Blog::getUserId, id).page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        return Result.ok(records);
    }

    /*
     * 推送关注的博客
     * @param max
     * @param offset
     * @return com.jxufe.dto.Result<java.lang.Object>
     * @author 逍遥
     * @create 2024/11/28 下午4:53
     **/
    @ApiOperation("推送关注的博客")
    @GetMapping("/of/follow")
    public Result<Object> pushFollowedBlog(@RequestParam("lastId") Long max,
                                         @RequestParam(value = "offset", defaultValue = "0")
                                           Integer offset){

        return Result.ok(blogService.pushFollowedBlog(max, offset)) ;
    }
}
