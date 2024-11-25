package com.jxufe.controller;


import com.jxufe.dto.Result;
import com.jxufe.entity.Voucher;
import com.jxufe.service.IVoucherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/*
 * 优惠券相关功能
 * @author 逍遥
 * @create 2024/11/25 下午7:23
 **/
@RestController
@RequestMapping("/voucher")
@Api(tags = "优惠券功能")
public class VoucherController {

    @Resource
    private IVoucherService voucherService;

    /*
     * 新增优惠券
     * @param voucher
     * @return com.jxufe.dto.Result
     * @author 逍遥
     * @create 2024/11/25 下午7:24
     **/
    @ApiOperation(value = "新增普通券")
    @PostMapping
    public Result addVoucher(@RequestBody Voucher voucher) {
        voucherService.save(voucher);
        return Result.ok(voucher.getId());
    }

    /*
     * 新增秒杀券
     * @param voucher
     * @return com.jxufe.dto.Result
     * @author 逍遥
     * @create 2024/11/25 下午7:24
     **/
    @ApiOperation(value = "新增秒杀券")
    @PostMapping("seckill")
    public Result addSeckillVoucher(@RequestBody Voucher voucher) {
        voucherService.addSeckillVoucher(voucher);
        return Result.ok(voucher.getId());
    }

    /*
     * 查询店铺的优惠券列表
     * @param shopId
     * @return com.jxufe.dto.Result
     * @author 逍遥
     * @create 2024/11/25 下午7:24
     **/
    @ApiOperation(value = "查询店铺优惠券")
    @GetMapping("/list/{shopId}")
    public Result queryVoucherOfShop(@PathVariable("shopId") Long shopId) {
       return voucherService.queryVoucherOfShop(shopId);
    }
}
