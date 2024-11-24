package com.jxufe.service.impl;


import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.jxufe.constant.VoucherConstant;
import com.jxufe.context.BaseContext;
import com.jxufe.context.RandomId;
import com.jxufe.dto.Result;
import com.jxufe.entity.SeckillVoucher;
import com.jxufe.entity.VoucherOrder;
import com.jxufe.mapper.SeckillVoucherMapper;
import com.jxufe.mapper.VoucherMapper;
import com.jxufe.mapper.VoucherOrderMapper;
import com.jxufe.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    private final VoucherMapper voucherMapper;

    private final VoucherOrderMapper voucherOrderMapper;

    private final SeckillVoucherMapper seckillVoucherMapper;

    private final RedissonClient redissonClient;

    private final RabbitTemplate rabbitTemplate;



    /*
     * 购买秒杀券
     * @param voucherId
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/11/10 下午6:40
     **/
    @Transactional
    public Result seckillVoucher(Long voucherId) {

        //1.查询优惠券
        SeckillVoucher voucher = seckillVoucherMapper.selectById(voucherId);
        //2.判断是否在秒杀时间内
        if ( !(LocalDateTime.now().isAfter(voucher.getBeginTime()) && LocalDateTime.now().isBefore(voucher.getEndTime())) ) {
            return Result.fail(VoucherConstant.OUT_TIME);
        }
        //3.判断库存
        if( ! (voucher.getStock() > 0) ){
            return Result.fail(VoucherConstant.STOCK_END);
        }

        //4.扣减库存
        // synchronized ( BaseContext.getCurrentId().toString().intern() )  //只适用于单机模式下

        ((VoucherOrderServiceImpl) AopContext.currentProxy()).reduceStock(voucherId);
        //5.存入订单信息
        long id = RandomId.getId();
        JSONObject json = null;

        try {
            json = new JSONObject();
            json.putIfAbsent("id", id);
            json.putIfAbsent("voucherId", voucherId);
            json.putIfAbsent("userId", BaseContext.getCurrentId());
            rabbitTemplate.convertAndSend("hmdp", json);
        } catch (JSONException e) {
            // 处理JSONObject创建失败的情况
            log.error(e.getMessage(), e);
        } catch (AmqpException e) {
            // 处理消息发送失败的情况
            log.error(e.getMessage(), e);
        }

        //6.返回订单id
        return Result.ok(id);
    }

    /*
     * redis上锁减少库存
     * @param voucherId
     * @return void
     * @author 逍遥
     * @create 2024/11/15 上午11:01
     **/
    private void reduceStock(Long voucherId) {
        RLock lock = redissonClient.getLock("lock:order:" + BaseContext.getCurrentId().toString()); //上锁
        //获取锁,并且返回boolean类型
        boolean isLock = lock.tryLock();
        if (!isLock) {
            throw new RuntimeException("已有订单信息");
        }
        try {
            //判断该用户是否已经购买过
            if( !lambdaQuery().eq(VoucherOrder::getUserId, BaseContext.getCurrentId()).list().isEmpty() ) {
                throw new RuntimeException("不可重复购买该秒杀券");
            }

            //减少存货量
            boolean result = voucherMapper.reduceStock(voucherId);
            if( !result ){
                throw new RuntimeException("已售罄");
            }
        }
        finally {
            lock.unlock();
        }
    }

    /*
     * 异步通信mq
     * @param json
     * @return void
     * @author 逍遥
     * @create 2024/11/15 上午11:01
     **/
    @RabbitListener(queues = "hmdp")
    private void updateOrder(JSONObject json) {
        Long id = Long.valueOf(json.get("id").toString());
        Long voucherId = Long.valueOf(json.get("voucherId").toString());
        Long userId = Long.valueOf(json.get("userId").toString());
        voucherOrderMapper.insert(
                VoucherOrder.builder().
                userId(userId).
                voucherId(voucherId).
                id(id)
                .build()
        );
    }

}
