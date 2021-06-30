package com.leej.community.entity;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
@Getter
@Setter
@ToString//添加getter和setter等方法
public class User {
    private Integer id;
    private String username;
    private String password;
    private String salt;
    private String email;
    private Integer type;
    private Integer status;
    private String activationCode;
    private String headerUrl;
    private Date createTime;
}
