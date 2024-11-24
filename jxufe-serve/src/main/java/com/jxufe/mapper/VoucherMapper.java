package com.jxufe.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxufe.entity.Voucher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Voucher> queryVoucherOfShop(@Param("shopId") Long shopId);

    //更新存货
    @Update("update tb_seckill_voucher set stock = stock - 1 where voucher_id = #{id} and stock > 0")
    boolean reduceStock(Long id);
}
