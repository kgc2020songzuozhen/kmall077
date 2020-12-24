package com.kgc.kmall.manager.service;

import com.kgc.kmall.bean.PmsSkuAttrValue;
import com.kgc.kmall.bean.PmsSkuImage;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.bean.PmsSkuSaleAttrValue;
import com.kgc.kmall.manager.mapper.PmsSkuAttrValueMapper;
import com.kgc.kmall.manager.mapper.PmsSkuImageMapper;
import com.kgc.kmall.manager.mapper.PmsSkuInfoMapper;
import com.kgc.kmall.manager.mapper.PmsSkuSaleAttrValueMapper;
import com.kgc.kmall.service.SkuService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

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
}
