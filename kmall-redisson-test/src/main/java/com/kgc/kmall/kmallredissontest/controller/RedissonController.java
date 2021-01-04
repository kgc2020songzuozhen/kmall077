package com.kgc.kmall.kmallredissontest.controller;

import com.kgc.kmall.utils.RedisUtil;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;

@RestController
public class RedissonController {

    @Resource
    RedissonClient redissonClient;

    RedisUtil redisUtil=new RedisUtil();

    @RequestMapping("/test")
    public String testRedisson(){
        redisUtil.initPool("192.168.38.135",6379,0);
        Jedis jedis = redisUtil.getJedis();
        Lock lock = redissonClient.getLock("lock");// 声明锁
        lock.lock();//上锁

        try {
            String v = jedis.get("k");
            if (v==null) {
                v = "1";
            }
            System.out.println("->" + v);
            jedis.set("k", (Integer.parseInt(v) + 1) + "");
        }finally {
            jedis.close();
            lock.unlock();// 解锁
        }
        return "success";
    }
}
