package com.techzhao.service.lua;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @version 1.0
 * @description: 抢购优惠券类，通过Lua脚本实现查询和操作的原子性
 * @date 2025-04-12 16:49
 */
@Controller("/redis/lua")
public class SnapUpCouponController {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    // Lua 脚本，判断和操作当做是一个原子操作，避免了并发下超卖的问题
    private static final String SNAP_UP =
                    "local value = redis.call('GET', KEYS[1]) " +
                    "if value == false then " +
                    "   return nil " +
                    "else " +
                    "   redis.call('DEL', KEYS[1]) " +
                    "   return value " +
                    "end";


    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/getBykey")
    @ResponseBody
    public String query(@RequestParam(value = "key") String key) {
        String value = null;
        Object o = get(key);
        if (o != null) {
            value = o.toString();
        }
        return value;
    }

    @GetMapping("/save")
    @ResponseBody
    public String save(@RequestParam(value = "key") String key) {
        String value = "优惠券";
        set(key, value);
        return value;
    }


    /**
     * 并发抢优惠券的接口，通过Lua实现原子操作，避免超卖
     * @param key
     * @return
     */
    @GetMapping("/snapUp")
    @ResponseBody
    public String sanpUp(@RequestParam(value = "key") String key) {
        String[] keys = {key};
        RedisScript<String> script = new DefaultRedisScript<>(SNAP_UP, String.class);
        String result = redisTemplate.execute(script, Arrays.asList(keys));
        System.out.println("抢购优惠券结果：" + result);
        return result;
    }
}
