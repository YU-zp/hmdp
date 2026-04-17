package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.ShopType;
import com.hmdp.mapper.ShopTypeMapper;
import com.hmdp.service.IShopTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_KEY;
import static com.hmdp.utils.RedisConstants.CACHE_SHOP_TYPE_TTL;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopTypeService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryList() throws JsonProcessingException {
        List<ShopType> list = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String Key = CACHE_SHOP_TYPE_KEY+ "list";
        String shopTypeListJson = stringRedisTemplate.opsForValue().get(Key);

        if(!StringUtils.isBlank(shopTypeListJson)){
            list = objectMapper.readValue(shopTypeListJson, new TypeReference<List<ShopType>>() {});
            return Result.ok(list);
        }

        list = query().orderByAsc("sort").list();
        if(list.isEmpty()){
            return Result.fail("商铺类型列表为空");
        }

        String shopTypeListJson1 = objectMapper.writeValueAsString(list);

        stringRedisTemplate.opsForValue().set(Key, shopTypeListJson1, CACHE_SHOP_TYPE_TTL, TimeUnit.MINUTES);

        return Result.ok(list);
    }
}
