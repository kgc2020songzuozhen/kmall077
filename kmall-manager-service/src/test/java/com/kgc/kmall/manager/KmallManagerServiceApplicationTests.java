package com.kgc.kmall.manager;

import com.kgc.kmall.bean.PmsBaseCatalog1;
import com.kgc.kmall.manager.utils.RedisUtil;
import com.kgc.kmall.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class KmallManagerServiceApplicationTests {

	@Resource
	CatalogService catalogService;

	@Resource
	RedisUtil redisUtil;

	@Test
	void contextLoads() {
		Jedis jedis = redisUtil.getJedis();
		String ping = jedis.ping();
		System.out.println(ping);
	}
}
