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
    int ENTITY_TYPE_USER=3;
    //event topics
    String TOPIC_COMMENT="comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW ="follow";
    String TOPIC_PUBLISH="publish";
    String TOPIC_DELETE="delete";
    String TOPIC_SHARE="share";
    int SYSTEM_USER_ID=1;
    //用户身份
    String AUTHORITY_USER="user";
    String AUTHORITY_ADMIN="admin";
    String AUTHORITY_MODERATOR="moderator";

    //帖子

}
