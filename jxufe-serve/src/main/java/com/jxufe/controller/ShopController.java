package com.jxufe.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxufe.dto.Result;
import com.jxufe.entity.Shop;
import com.jxufe.service.IShopService;
import com.jxufe.utils.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/*
 * shop
 * @param null
 * @return
 * @author 逍遥
 * @create 2024/10/22 下午2:16
 **/
@RestController
@RequestMapping("/shop")
@Slf4j
@Api(tags = "商铺相关功能")
@RequiredArgsConstructor
public class ShopController {


    public final IShopService shopService;

    /**
     * 根据id查询商铺信息
     * @param id 商铺id
     * @return 商铺详情数据
     */
    @ApiOperation("查询商铺信息")
    @GetMapping("/{id}")
    public Result queryShopById(@PathVariable("id") Long id) {

        log.info("正在查询id为{}的商铺信息", id);
        return Result.ok(shopService.queryShopById(id));
    }

    /*
    * 新增商铺信息
    * @param shop
    * @return com.jxufe.constant.dto.Result
    * @author 逍遥
    * @create 2024/10/22 下午2:27
    **/
    @ApiOperation("新增商铺信息")
    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {

        // 写入数据库
        shopService.save(shop);
        // 返回店铺id
        return Result.ok(shop.getId());
    }


    /*
     * 更新商铺信息
     * @param shop
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/10/22 下午6:14
     **/
    @ApiOperation("更新商铺信息")
    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        // 写入数据库
        log.info("正在更新商铺id为{}的商铺信息", shop.getId());
        shopService.updateById(shop);
        return Result.ok();
    }

    /**
     * 根据商铺类型分页查询商铺信息
     * @param typeId 商铺类型
     * @param current 页码
     * @return 商铺列表
     */
    @ApiOperation("根据商铺类型分页查询商铺信息")
    @GetMapping("/of/type")
    public Result queryShopByType(
            @RequestParam("typeId") Integer typeId,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Shop> page = shopService.query()
                .eq("type_id", typeId)
                .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
        // 返回数据
        return Result.ok(page.getRecords());
    }

    /**
     * 根据商铺名称关键字分页查询商铺信息
     * @param name 商铺名称关键字
     * @param current 页码
     * @return 商铺列表
     */
    @ApiOperation("根据商铺类型分页查询商铺信息")
    @GetMapping("/of/name")
    public Result queryShopByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        // 根据类型分页查询
        Page<Shop> page = shopService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 返回数据
        return Result.ok(page.getRecords());
    }
}
