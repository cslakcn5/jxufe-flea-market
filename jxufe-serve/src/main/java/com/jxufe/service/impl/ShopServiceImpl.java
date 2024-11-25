package com.jxufe.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jxufe.entity.Shop;
import com.jxufe.mapper.ShopMapper;
import com.jxufe.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jxufe.constant.RedisConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
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
@RequiredArgsConstructor
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    private final ShopMapper shopMapper;

    private final RedisTemplate<String, Object> redisTemplate;

    private final ObjectMapper objectMapper;

    /*
     * 根据id查询商铺信息
     * @param id
     * @return com.jxufe.entity.Shop
     * @author 逍遥
     * @create 2024/9/28 下午10:39
     **/
    public Shop queryShopById(Long id) {

        //1.根据id查询redis是否含有商铺信息
        Map<Object, Object> map = redisTemplate.opsForHash().entries(RedisConstants.CACHE_SHOP_KEY + id);
        // 将Map转换为User对象
        Shop entity;
        //2. 判断是否为空 有则直接返回
        if( map.toString().equals("{=}") ){
            throw new RuntimeException("该商铺不存在");
        }

        if( !map.isEmpty() ){
            entity = objectMapper.convertValue(map, Shop.class);
            return entity;
        }
        //3.没有则从数据库查询再存储redis
        Shop shop = shopMapper.selectById(id);
        if( Optional.ofNullable(shop).isEmpty() ){
            redisTemplate.opsForHash().put(RedisConstants.CACHE_SHOP_KEY + id, "" , "");
            redisTemplate.expire(RedisConstants.CACHE_SHOP_KEY + id, 25 + RandomUtil.randomInt(0,10), TimeUnit.MINUTES);
            throw new RuntimeException("该商铺不存在");
        }

        //4.将商铺信息写入redis
        Map<String, Object> maps = BeanUtil.beanToMap(shop);
        redisTemplate.opsForHash().putAll(RedisConstants.CACHE_SHOP_KEY + id, maps);
        redisTemplate.expire(RedisConstants.CACHE_SHOP_KEY + id, 1, TimeUnit.MINUTES);
        return shop;
    }

    /*
     * 更新商铺信息
     * @param shop
     * @return com.jxufe.constant.dto.Result
     * @author 逍遥
     * @create 2024/10/22 下午6:14
     **/
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(Shop shop) {
        //1.先判断id存不存在
        if ( Optional.ofNullable( shopMapper.selectById(shop.getId()) ).isEmpty() ){
            throw new RuntimeException("传入的商铺信息存在问题");
        }
        //2.先改数据库
        shopMapper.updateById(shop);
        //3.后改缓存
        redisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + shop.getId());
        return true;
    }
}
