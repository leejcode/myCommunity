package com.leej.community.utils;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
@Component
public class SensitiveFilter {
    private static final Logger logger= LoggerFactory.getLogger(SensitiveFilter.class);
    private static final String REPLACEMENT="***";//用于替换的符号
    //根节点
    private TrieNode rootNode=new TrieNode();
    @PostConstruct//在构建后自动调用
    public void init(){
        //读取文件
        try(        InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is))
        ){
                    String keyword;
                    while((keyword=reader.readLine())!=null){
                        //添加到前缀树\
                        this.addKeyword(keyword);
                    }
        }catch (IOException e){
            logger.error("加载敏感词文件失败"+e.getMessage());
        }

    }
    private void addKeyword(String keyword){
        TrieNode tempNode =rootNode;
        for(int i = 0;i<keyword.length();++i){
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode==null){
                //初始化子节点
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;//进入下一轮循环
            //设置结束标识
            if(i==keyword.length()-1){
                tempNode.setKeywordEnd(true);
            }
        }
    }
    //过滤敏感词
    public String filter(String text){
        if(StringUtils.isBlank(text))
            return null;
        //指针1指向Trie
        TrieNode tempNode= rootNode;
        //
        int begin=0,position=0;
        StringBuilder stringBuilder=new StringBuilder();
        while(position<text.length()){
            char c= text.charAt(position);
            //跳过符号
            if(isSymbol(c)){
                if(tempNode==rootNode){
                    stringBuilder.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //检查下一节点
            tempNode=tempNode.getSubNode(c);
            if(tempNode==null) {
                stringBuilder.append(text.charAt(begin));
                begin++;
                position = begin;
                tempNode = rootNode;//重写指向跟节点
            }else if(tempNode.isKeywordEnd){
                //发现敏感词,将区间字符替换
                stringBuilder.append(REPLACEMENT);
                begin=++position;
                tempNode=rootNode;
            }else{
                position++;
            }
        }
        //将最后一批字符记录
        stringBuilder.append(text.substring(begin));
        return stringBuilder.toString();
    }
    //判断是否为符号
    private boolean isSymbol(Character c){
        return !CharUtils.isAsciiAlphanumeric(c)&&(c<0x2E80||c>0x9FFF);
    }
    //定义前缀树
    private class TrieNode{
        //关键词结束标识
        private boolean isKeywordEnd=false;
        //子节点 key是下级节点字符，value是下级节点
        private Map<Character,TrieNode> subNodes=new HashMap<>();
        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }
        public void addSubNode(Character c,TrieNode node){
            subNodes.put(c,node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }

}
