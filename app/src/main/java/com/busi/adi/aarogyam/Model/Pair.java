package com.busi.adi.aarogyam.Model;

import com.fasterxml.jackson.core.type.TypeReference;

public class Pair {
    public String url;
    public TypeReference<?> valueTypeRef;
    public Pair(String u, TypeReference<?> v){
        url = u;
        valueTypeRef = v;
    }
}
