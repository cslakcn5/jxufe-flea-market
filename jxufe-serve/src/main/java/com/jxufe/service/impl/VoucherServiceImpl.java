package com.jxufe.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxufe.dto.Result;
import com.jxufe.entity.Voucher;
import com.jxufe.mapper.VoucherMapper;
import com.jxufe.entity.SeckillVoucher;
import com.jxufe.service.ISeckillVoucherService;
import com.jxufe.service.IVoucherService;
import com.jxufe.constant.RedisConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements IVoucherService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Result queryVoucherOfShop(Long shopId) {
        // 查询优惠券信息
        List<Voucher> vouchers = getBaseMapper().queryVoucherOfShop(shopId);
        // 重置过期时间
        vouchers.forEach(voucher ->
            redisTemplate.expire(RedisConstants.SECKILL_STOCK_KEY + voucher.getId(), 3, TimeUnit.DAYS));
        // 返回结果
        return Result.ok(vouchers);
    }

    @Override
    @Transactional
    public void addSeckillVoucher(Voucher voucher) {
        // 保存优惠券
        save(voucher);
        // 保存秒杀信息
        SeckillVoucher seckillVoucher = new SeckillVoucher();
        seckillVoucher.setVoucherId(voucher.getId());
        seckillVoucher.setStock(voucher.getStock());
        seckillVoucher.setBeginTime(voucher.getBeginTime());
        seckillVoucher.setEndTime(voucher.getEndTime());
        seckillVoucherService.save(seckillVoucher);
        redisTemplate.opsForValue().set(RedisConstants.SECKILL_STOCK_KEY + voucher.getId(), voucher.getStock().toString(), 3, TimeUnit.DAYS);
    }
}
