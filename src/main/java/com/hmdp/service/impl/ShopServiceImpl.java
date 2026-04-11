package com.hmdp.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.*;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 * 商店相关服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient clientClient;

    @Override
    public Result queryById(Long id) {
        //解决缓存穿透
        Shop shop = clientClient
                .queryWithPassThrough(RedisConstants.CACHE_SHOP_KEY, id, Shop.class,
                        this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);

        //1.互斥锁 解决缓存击穿
        /*Shop shop = clientClient.queryWithMutex(
            RedisConstants.CACHE_SHOP_KEY,
            id,
            Shop.class,
            this::getById, // 数据库查询方法
            RedisConstants.CACHE_SHOP_TTL,
            TimeUnit.MINUTES
        );*/

        /*//2.逻辑过期 解决缓存击穿
        Shop shop = clientClient.queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class,
                this::getById, RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
        if (shop == null) {
            return Result.fail("店铺不存在！");
        }*/
        return Result.ok(shop);
    }

    /**
     * 更新店铺信息
     */
    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("店铺id不能为空");
        }
        //1.先修改数据库
        updateById(shop);
        //2.再删除缓存
        stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        return Result.ok();
    }

    /**
     * 根据商店类型分页查询
     */
    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        //1.是否根据坐标查询
        if (x == null || y == null) {
            //不需要坐标查询，该数据库查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            return Result.ok(page.getRecords());
        }
        //2.计算分页参数
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        //3.查询redis，按照距离排序，分页。结果：shopId,distance
        String key = SHOP_GEO_KEY + typeId;
        //在 Redis 中按地理坐标（x, y）查询距离当前用户位置 5000 米内的商店
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo()
                .search(
                        key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        if (results == null || results.getContent().isEmpty()) {
            // 缓存重建
            loadShopGeoData();
            return Result.ok(Collections.emptyList());
        }
        //4.解析出id
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            return Result.ok(Collections.emptyList());
        }
        //4.1.截取从from到end部分  跳过前 from 个结果，实现分页
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            //4.2.获取店铺id
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            //4.3.获取距离
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });
        //5.根据id查询shop
        String idStr = StrUtil.join(",", ids);
        List<Shop> shops = query()
                .in("id", ids).last("order by field(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        //6.返回
        return Result.ok(shops);
    }

    private void loadShopGeoData() {
        List<Shop> shops = query().list();
        for (Shop shop : shops) {
            if (shop.getX() != null && shop.getY() != null) {
                String key = SHOP_GEO_KEY + shop.getTypeId();
                stringRedisTemplate.opsForGeo()
                        .add(key, new Point(shop.getX(), shop.getY()), shop.getId().toString());
            }
        }
    }
}