package model;

import utility.InfoHandler;

import java.util.*;

public class Production {

    /**
     * 文法的所有产生式集合
     */
    private Map<String, List<String>> productions;

    /**
     * 终结符集合
     */
    private Set<String> terminal;

    /**
     * 非终结符集合
     */
    private Set<String> non_terminal;

    /**
     * 文法的开始符
     */
    private String start;

    public Production(Map<String, List<String>> productions, String start) {
        this.productions = productions;
        this.terminal = new HashSet<>();
        this.non_terminal = new HashSet<>();
        this.start = start;
        init();
        extractLeftCommonFactor();
        eliminateLeftRecursion();
    }

    /**
     * 用于判断该符号是否属于终结符
     *
     * @param tag 需要判断的符号
     * @return boolean 判断结果
     */
    public boolean isTerminal(String tag) {
        return terminal.contains(tag);
    }

    /**
     * 用于判断该符号是否属于非终结符
     *
     * @param tag 需要判断的符号
     * @return boolean 判断结果
     */
    public boolean isNonTerminal(String tag) {
        return non_terminal.contains(tag);
    }

    /**
     * 获取产生式集合
     *
     * @return Map<String, List<String>> 产生式集合
     */
    public Map<String, List<String>> getProductions() {
        return productions;
    }

    /**
     * 获取终结符集合
     *
     * @return Set<String> 终结符集合
     */
    public Set<String> getTerminal() {
        return terminal;
    }

    /**
     * 获取非终结符集合
     *
     * @return Set<String> 非终结符集合
     */
    public Set<String> getNon_terminal() {
        return non_terminal;
    }

    /**
     * 获取文法的开始符
     *
     * @return String 开始符
     */
    public String getStart() {
        return start;
    }

    /**
     * 用于初始化终结符和非终结符的集合
     */
    private void init() {
        non_terminal.addAll(productions.keySet());
        for (String key : non_terminal) {
            List<String> right = productions.get(key);
            for (String single : right) {
                String[] temp = single.split("\\s");
                for (String character : temp) {

                    if (character.equals("")) {
                        continue;
                    }

                    if (!non_terminal.contains(character)) {
                        terminal.add(character);
                    }
                }
            }
        }
    }

    /**
     * 用于消除左递归
     */
    private void eliminateLeftRecursion() {
        eliminateIndirectLeftRecursion(); //消除所有间接左递归

        for (String key : non_terminal) {
            List<String> production = productions.get(key);
            for (String production_one : production) {
                if (production_one.startsWith(key)) {
                   eliminatedDirectLeftRecursion(key); //消除当前key的直接左递归
                   break;
                }
            }
        }

        non_terminal = productions.keySet();
    }

    /**
     *  消除当前的Key的立即左递归
     * @param key
     */
    private void eliminatedDirectLeftRecursion(String key){
        List<String> production = productions.get(key);
        String newKey = key+"_l";
        List<String> old_production = new ArrayList<>();
        List<String> new_production = new ArrayList<>();
        for(String sub: production){
            if(sub.startsWith(key)){
                String sub_sub = sub.substring(2);
                String new_sub = sub_sub+" "+newKey;
                new_production.add(new_sub);
            } else{
                String new_sub = sub+" "+newKey;
                old_production.add(new_sub);
            }
        }
        new_production.add("ε");
        productions.put(key, old_production);
        productions.put(newKey, new_production);
    }

    /**
     * 消除间接左递归
     */
    private void eliminateIndirectLeftRecursion() {
        for (String key : non_terminal) {
            List<String> production = productions.get(key);
            for (String sub : production) {
                String[] sub_str = sub.split("\\s");
                if (isTerminal(sub_str[0])) {
                    continue; //为终结符，无左递归，跳过
                }

                if (sub.startsWith(key)) {
                    continue; // 立即左递归跳过
                }

                List<String> replaceList = findLeftRecursion(sub_str[0], key, key);
                if (!replaceList.isEmpty()) {
                    List<String> new_key_List = new ArrayList<>();
                    for (String replace_str : replaceList) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(replace_str);
                        for (int i = 1; i < sub_str.length; i++) {
                            builder.append(" ");
                            builder.append(sub_str[i]);
                        }
                        new_key_List.add(builder.toString());
                    }

                    List<String> old_production = productions.get(key);
                    for(String old_sub: old_production){
                        if(!old_sub.equals(sub)){
                            new_key_List.add(old_sub);
                        }
                    }

                    productions.put(key, new_key_List);
                }
            }
        }
    }

    /**
     *  把所有的间接左递归变成立即左递归
     *
     * @param needKey 需要的继续检索的Key
     * @param matchKey 需要匹配的Key
     * @param prekey 上一个检索的Key
     * @return 返回需要替换的立即左递归
     */
    private List<String> findLeftRecursion(String needKey, String matchKey, String prekey) {
        List<String> production = productions.get(needKey);
        List<String> result = new ArrayList<>();
        for (String sub : production) {
            if (sub.startsWith(matchKey)) {
                result.add(sub);
                continue;
            }

            if(needKey.equals(prekey)){ //相同时，则出现实现中添加的新的立即左递归，跳过
                continue;
            }

            String[] sub_str = sub.split("\\s");
            if (!isTerminal(sub_str[0])) {
                List<String> next = findLeftRecursion(sub_str[0], matchKey, needKey);
                if (!next.isEmpty()) {
                    for (String needReplace : next) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(needReplace);
                        for (int i = 1; i < sub_str.length; i++) {
                            builder.append(" ");
                            builder.append(sub_str[i]);
                        }
                        result.add(builder.toString());
                    }
                }
            }
        }
        return result;
    }

    /**
     * 用于提取最大左公因子
     */
    private void extractLeftCommonFactor() {
        for (String key : non_terminal) {
            List<String> production = productions.get(key);
            String new_non_terminal = key + "_e";
            List<String> old_production = new ArrayList<>();
            List<String> new_production = new ArrayList<>();
            if (production.size() <= 1) {
                continue;
            }

            List<String> currentLeftFactor = findLeftFactor(production);
            if(currentLeftFactor.isEmpty()){
                continue;
            }

            for (String sub_production : production) {
                boolean isStart = false;

                for (String factor : currentLeftFactor) {
                    if (sub_production.startsWith(factor)) {
                        isStart = true;
                        String temp_sub = sub_production.substring(factor.length());
                        String newProduction = factor + " " + new_non_terminal;

                        if (!old_production.contains(newProduction)) {
                            old_production.add(newProduction); //添加新产生的产生式
                        }

                        if (temp_sub.equals("")) { //当前的产生式就是最大左公因子
                            if (!new_production.contains("ε")) {
                                new_production.add("ε");
                            }
                        } else {
                            if (!new_production.contains(temp_sub.substring(1))) {
                                new_production.add(temp_sub.substring(1)); //新的非终结符指向的产生式
                            }
                        }
                    }
                }

                if (!isStart) {
                    old_production.add(sub_production);
                }
            }

            productions.put(key, old_production);
            productions.put(new_non_terminal, new_production);
        }

        init(); //更新终结符和非终结符
    }

    /**
     * 获取最大左公因子
     *
     * @param production 产生式集合
     * @return 所有最大左公因子
     */
    private List<String> findLeftFactor(List<String> production) {

        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < production.size(); i++) {
            String[] production_one = production.get(i).split("\\s");
            String subProduction = production_one[0];
            for (int j = 0; j < production_one.length; j++) {

                if (j != 0) { //已经初始化subProduction，需要跳过这一步
                    subProduction = subProduction + " " + production_one[j];
                }

                int count = 0;
                for (int k = 0; k < production.size(); k++) {
                    if (production.get(k).startsWith(subProduction)) {
                        count++;
                    }
                }
                map.put(subProduction, count);
            }
        }

        Set<String> temp_result = findMax(map);
        List<String> result = findMaxLength(temp_result);
        return result;
    }

    /**
     * 用于找到长度最长的String，相同则都返回
     *
     * @param set 需要寻找的集合
     * @return List<String> 结果
     */
    private List<String> findMaxLength(Set<String> set) {
        List<String> result = new ArrayList<>();
        int length = -1;
        for (String key : set) {
            if (key.length() > length) {
                result.clear();
                result.add(key);
                length = key.length();
            } else if (key.length() == length) {
                result.add(key);
            }
        }
        return result;
    }

    /**
     * 找到出现次数最多的String，次数相同则都返回
     *
     * @param map 需要进行查找的集合
     * @return 结果
     */
    private Set<String> findMax(Map<String, Integer> map) {
        Set<String> result = new HashSet<>();
        String result_count = null;
        int judge = -1;
        for (String key : map.keySet()) {
            if(map.get(key) <= 1){
                continue;
            }

            if (map.get(key) > judge) {
                result.clear();
                result.add(key);
                result_count = key;
                judge = map.get(key);
            } else if (map.get(key) == judge) {
                if (!key.equals(result_count)) {
                    result.add(key);
                }
            }
        }
        return result;
    }
}
