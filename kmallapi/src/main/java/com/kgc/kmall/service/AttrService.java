package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;
import com.kgc.kmall.bean.PmsBaseAttrValue;

import java.util.List;

public interface AttrService {
    List<PmsBaseAttrInfo> select(Long catalog3Id);

    Integer add(PmsBaseAttrInfo attrInfo);

    List<PmsBaseAttrValue> getAttrValueList(Long attrId);
}
