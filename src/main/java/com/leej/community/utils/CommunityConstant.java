package com.leej.community.utils;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS=0;
    int ACTIVATION_REPEAT=1;
    int ACTIVATION_FAILURE=2;
    int DEFAULT_EXPIRED_SECONDS=3600*12;
    int REMEMBER_EXPIRED_SECONDS=3600*24*100;
    //post的类型
    int ENTITY_TYPE_POST=1;
    int ENTITY_TYPE_COMMENT=2;
}
