package com.leej.community.entity;

import lombok.Getter;

//封装分页相关信息
@Getter
public class Page {
    private Integer current=1;
    //单页显示上限
    private Integer limit=10;
    //数据总数，用于计算总页数
    private Integer rows;
    //查询路径（用于复用分页链接）
    private String path;

    public void setCurrent(Integer current) {
        if(current>=1)
        this.current = current;
    }

    public void setLimit(Integer limit) {
        if(limit>=1&&limit<=100)
        this.limit = limit;
    }

    public void setRows(Integer rows) {
        if(rows>=0)
        this.rows = rows;
    }

    public void setPath(String path) {
        this.path = path;
    }
    //获取当前页的起始行
    public int getOffset(){
        return (current-1)*limit;
    }
    //获取总页数
    public int getTotal(){
        int total = rows/limit;
        return rows%limit==0?total:total+1;
    }
    //获取启示页码
    public int getFrom(){
        int from = current-2;
        return Math.max(1,from);
    }
    //获取终止页码
    public int getTo(){
        int to = current+2;
        int total = getTotal();
        return Math.min(to,total);
    }
}
