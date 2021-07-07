package com.leej.community.utils;

public class RedisKeyUtil {
    private static final String SPLIT=":";
    private static final String PREFIX_ENTITY_LIKE="like:entity";
    private static final String PREFIX_USER_LIKE="like:user";
    private static final String PREFIX_FOLLOWEE ="followee";//
    private static final String PREFIX_FOLLOWER ="follower";//粉丝
    private static final String PREFIX_KAPTCHA="kaptcha";
    private static final String PREFIX_TICKET="ticket";
    private static final String PREFIX_USER="user";
    private static final String PREFIX_UV="uv";
    private static final String PREFIX_DAU="dau";
    private static final String PREFIX_POST="post";
    //某个实体的赞
    //like:entity:entityType:entityId-> set(userId)
    public static String getEntityLikeKey(int entityType,int entityId){
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    //某个用户的赞
    //like:user:userid  ->int
    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE+SPLIT+userId;
    }
    //某个用户关注的实体  用户id，关注的实体类型，存放的是实体id-时间
    //followee:userId:entityType -> zset(entityId,now) 根据关注时间排序
    public static String getFolloweeKey(int userId,int entityType){
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }
    //某个实体的粉丝  实体类型，实体id 存放的是用户id，时间
    //follower:entityType:entityId ->zset(userId,now)
    public static String getFollowerKey(int entityType,int entityId){
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }
    //登录验证码的key
    public static String getKaptchaKey(String owner){//owner为用户的临时凭证
        return PREFIX_KAPTCHA+SPLIT+owner;
    }
    //登陆凭证的key
    public static String getTicketKey(String ticket){
        return PREFIX_TICKET+SPLIT+ticket;
    }
    //用户
    public static String getUserKey(int userId){
        return PREFIX_USER+SPLIT+userId;
    }
    //单日UV
    public static String getUVKey(String date){//日期
        return PREFIX_UV+SPLIT+date;
    }
    //区间UV
    public static String getUVKey(String startDate,String endDate){
        return PREFIX_UV+SPLIT+startDate+SPLIT+endDate;
    }
    //单日活跃用户
    public static String getDAUKey(String date){
        return PREFIX_DAU+SPLIT+date;
    }
    public static String getDAUKey(String startDate,String endDate){
        return PREFIX_DAU+SPLIT+startDate+SPLIT+endDate;
    }
    public static String getPostScoreKey(){
        return  PREFIX_POST+SPLIT+"score";
    }
}
