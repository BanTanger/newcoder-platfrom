package com.bantanger.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import sun.text.normalizer.Trie;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author bantanger 半糖
 * @version 1.0
 * @Date 2022/7/28 19:38
 */

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    /* 替换符 */
    private static final String REPLACEMENT = "***";

    /* 根节点 */
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try (
                // 字节流 --> 字符流
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                // 字符流 --> 缓冲流
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while ((keyword = br.readLine()) != null) {
                // 添加到前缀树
                this.addKeyword(keyword);
            }
        } catch (IOException e) {
            logger.error("加载敏感词文件失败", e.getMessage());
        }
    }

    private void addKeyword(String keyword) {
        TrieNode tempNode = rootNode;
        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if (subNode == null) {
                // 没有就创建
                subNode = new TrieNode();
                tempNode.addSubNode(c, subNode);
            }

            // 指向下一个节点，进入下一层循环
            tempNode = subNode;
            // 设置结束标识
            if (i == keyword.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤敏感词
     * @param text 待过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        // 判空处理
        if (StringUtils.isBlank(text)) {
            return null;
        }
        // 指针 1 --> 指向树
        TrieNode tempNode = rootNode;
        // 指针 2 --> 指向敏感词头位置,指针2只会向前移动
        int begin = 0;
        // 指针 3 --> 指向敏感词尾位置,指针3向前移动，检测不是敏感词就回退或者前进
        int position = 0;
        // 结果: 敏感词被替换后的String字符串
        StringBuffer sb = new StringBuffer();
        // 因为指针 3 比起指针 2 优先到达字符串末尾，所以以指针 3 作为while循环条件
        while (position < text.length()) {
            char c = text.charAt(position);
            if (isSymbol(c)) {
                // 如果此时指针 1 位于根节点, 那么将此符号记录结果，让指针 2 往下走一步
                if (tempNode == rootNode) {
                    sb.append(c);
                    begin ++;
                }
                position++; // 指针 3 无论如何都会步进，不管符号在开头还是结尾
                continue; // 判断下一个是否为特殊符号，不处理下面的情况
            }
            // 检查下级节点（指针 1 移动）
            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) { // 已经走到末尾了
                // 以 begin 开头的字符串不是敏感词
                sb.append(text.charAt(begin)); // 记录
                // 进入下一个位置
                position = ++ begin; // 指针 2 和 指针 3 同时步进，并且指针 3 比 指针 2 多移动一位
                // 不是敏感词，所以指针 1 重新指向根节点
                tempNode = rootNode;
            } else if(tempNode.isKeywordEnd()) { // 指针走到末尾
                // 发现敏感词，将 begin - position 范围进行替换
                sb.append(REPLACEMENT);
                // 双指针往后移动
                begin = ++ position;
                // 指针 1 重新指向根节点
                tempNode = rootNode;
            } else {
                // 移动过程, 检查下一个字符
                position ++;
            }
        }
        sb.append(text.substring(begin));
        return sb.toString();
    }

    /**
     * 判断是否为特殊符号
     *
     * @param c 当前遍历到的字符
     * @return 是否为特殊符号
     */
    private boolean isSymbol(char c) {
        /* (0x2E80, 0x9FFF) 是东亚象形字 */
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 && c > 0x9FFF);
    }

    /* 前缀树 */
    private class TrieNode {
        // 关键词结束标识
        private boolean isKeywordEnd = false;

        // 子节点 （key是下级字符，value是下级节点）
        private Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        // 维护子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c, node);
        }

        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}
