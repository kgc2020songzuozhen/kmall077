package com.kgc.kmall.item.controller;

import com.alibaba.fastjson.JSON;
import com.kgc.kmall.bean.PmsProductSaleAttr;
import com.kgc.kmall.bean.PmsSkuInfo;
import com.kgc.kmall.bean.PmsSkuSaleAttrValue;
import com.kgc.kmall.service.SkuService;
import com.kgc.kmall.service.SpuService;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    SkuService skuService;

    @Reference
    SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String item(@PathVariable(value = "skuId") Long skuId, Model model){
        PmsSkuInfo pmsSkuInfo=skuService.selectBySkuId(skuId);
        model.addAttribute("skuInfo",pmsSkuInfo);
        //根据spuid获取销售属性和属性和属性值
        List<PmsProductSaleAttr> spuSaleAttrListCheckBySku=spuService.spuSaleAttrListIsCheck(pmsSkuInfo.getSpuId(),skuId);
        model.addAttribute("spuSaleAttrListCheckBySku",spuSaleAttrListCheckBySku);

        //获取销售属性值id和skuid的关系
        List<PmsSkuInfo> pmsSkuInfos = skuService.selectBySpuId(pmsSkuInfo.getSpuId());
        //key  saleattrvalueId | saleattrvalueId...
        //value  skuId
         Map<String,Long> map=new HashMap<>();
        if (pmsSkuInfos!=null&&pmsSkuInfos.size()>0){
            for (PmsSkuInfo skuInfo : pmsSkuInfos) {
                if (skuInfo.getSkuSaleAttrValueList()!=null&&skuInfo.getSkuSaleAttrValueList().size()>0){
                    String key="";
                    for (PmsSkuSaleAttrValue info : skuInfo.getSkuSaleAttrValueList()) {
                        key+=info.getSaleAttrValueId()+"|";
                    }
                    map.put(key,skuInfo.getId());
                }
            }
            //转成json，传递到前端
            // 将sku的销售属性hash表放到页面
            String skuSaleAttrHashJsonStr = JSON.toJSONString(map);
            model.addAttribute("skuSaleAttrHashJsonStr",skuSaleAttrHashJsonStr);
        }
        return "item";
    }
}
