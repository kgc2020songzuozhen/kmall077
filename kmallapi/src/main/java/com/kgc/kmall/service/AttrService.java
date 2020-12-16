package com.kgc.kmall.service;

import com.kgc.kmall.bean.PmsBaseAttrInfo;

import java.util.List;

public interface AttrService {
    List<PmsBaseAttrInfo> select(Long catalog3Id);
}
