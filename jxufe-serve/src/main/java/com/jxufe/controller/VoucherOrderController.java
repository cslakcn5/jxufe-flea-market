package com.jxufe.controller;


import com.jxufe.context.BaseContext;
import com.jxufe.dto.Result;
import com.jxufe.service.IVoucherOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@RestController
@RequestMapping("/voucher-order")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "优惠券订单功能")
public class VoucherOrderController {

    private final IVoucherOrderService service;

    /*
     * 购买秒杀券
     * @param voucherId
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/10 下午11:44
     **/
    @ApiOperation(value = "购买秒杀券")
    @PostMapping("seckill/{id}")
    public Result seckillVoucher(@PathVariable("id") Long voucherId) {

        log.info("用户id为{}正在购买秒杀券", BaseContext.getCurrentId());
        return service.seckillVoucher(voucherId);
    }
}
