package com.kgc.kmall.manager.service;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.bean.PmsSkuImage;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.bean.PmsSkuSaleAttrValue;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.kgc.kmall.manager.utils.RedisUtil;
import com.kgc.kmall.service.SkuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@Component
public class SkuServiceImpl implements SkuService {

    @Resource
    PmsSkuInfoMapper pmsSkuInfoMapper;
    @Resource
    PmsSkuImageMapper pmsSkuImageMapper;
    @Resource
    PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Resource
    PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;

    @Resource
    RedisUtil redisUtil;


    @Override
    public String saveSkuInfo(PmsSkuInfo skuInfo) {
        try {
            //添加sku
            pmsSkuInfoMapper.insert(skuInfo);
            List<PmsSkuImage> skuImageList = skuInfo.getSkuImageList();
            if (skuImageList!=null&&skuImageList.size()>0){
                for (PmsSkuImage pmsSkuImage : skuImageList) {
                    pmsSkuImage.setSkuId(skuInfo.getId());
                    pmsSkuImageMapper.insert(pmsSkuImage);
                }
            }
            for (PmsSkuAttrValue pmsSkuAttrValue : skuInfo.getSkuAttrValueList()) {
                pmsSkuAttrValue.setSkuId(skuInfo.getId());
                pmsSkuAttrValueMapper.insert(pmsSkuAttrValue);
            }
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuInfo.getSkuSaleAttrValueList()) {
                pmsSkuSaleAttrValue.setSkuId(skuInfo.getId());
                pmsSkuSaleAttrValueMapper.insert(pmsSkuSaleAttrValue);
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        return "success";
    }

    @Override
    public PmsSkuInfo selectBySkuId(Long skuId) {
        PmsSkuInfo pmsSkuInfo =null;
        Jedis jedis=redisUtil.getJedis();
        String key="sku:"+skuId+":info";
        String skuJson = jedis.get(key);

        if (skuJson!=null){
            //缓存中有数据
            pmsSkuInfo = JSON.parseObject(skuJson, PmsSkuInfo.class);
            jedis.close();
            return pmsSkuInfo;
        }else{
            //获取分布式锁
            String skuLockKey="sku:"+skuId+":lock";
            String skuLockValue= UUID.randomUUID().toString();
            String ok = jedis.set(skuLockKey, skuLockValue, "NX", "PX", 60 * 1000);
            //拿到分布式锁
            if (ok.equals("OK")){
                //缓存中没有数据，从数据库中查询，并写入redis
                pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
                if (pmsSkuInfo!=null){
                    String json = JSON.toJSONString(pmsSkuInfo);
                    //有效期随机，防止缓存雪崩
                    Random random=new Random();
                    int i = random.nextInt(10);
                    jedis.setex(key,i*60*1000,json);
                }else {
                    jedis.setex(key,5*60*1000, "empty");
                }
                //写完缓存后要删除分布式锁，获取锁的值，并对比原来的值
//                String skuLockValue2 = jedis.get(skuLockKey);
//                if (skuLockValue2!=null&&skuLockValue2.equals(skuLockValue)) {
//                    //刚刚做完判断，过期
//                    jedis.del(skuLockKey);
//                }
                String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                jedis.eval(script, Collections.singletonList(skuLockKey),Collections.singletonList(skuLockValue));
            }else {
                //未拿到锁，线程睡眠3s，递归调用
                try {
                    Thread.sleep(3000);
                }catch (Exception ex){
                }
                selectBySkuId(skuId);
            }

            jedis.close();
        }
        return pmsSkuInfo;
    }

    @Override
    public List<PmsSkuInfo> selectBySpuId(Long spuId) {
        return pmsSkuInfoMapper.selectBySpuId(spuId);
    }
}
