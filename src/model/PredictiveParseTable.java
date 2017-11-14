package model;

import java.util.*;

public class PredictiveParseTable {

    /**
     * 预测分析表
     */
    private Map<String, Map<String, List<String>>> table;

    public PredictiveParseTable(Set<String> terminal, Set<String> non_terminal) {
        table = new HashMap<>();
        init(terminal, non_terminal);
    }

    /**
     * 用于从预测分析表中获取产生式
     *
     * @param rowName    行的key
     * @param columnName 列的key
     * @return String 产生式
     */
    public String getValue(String rowName, String columnName) {
        List<String> result = table.get(rowName).get(columnName);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

    public Map<String, Map<String, List<String>>> getTable() {
        return table;
    }

    /**
     * 用于向预测分析表中添加值
     *
     * @param rawName    行的key
     * @param columnName 列的key
     * @param production 需要添加的产生式
     */
    public void addValue(String rawName, String columnName, String production) {
        table.get(rawName).get(columnName).add(production);
    }

    /**
     * 从预测分析表中判断该文法是否为LL(1)文法
     *
     * @return boolean 判断结果
     */
    public boolean isLL() {
        for (String raw : table.keySet()) {
            for (String column : table.get(raw).keySet()) {
                if (table.get(raw).get(column).size() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 用于初始化table的列和行
     *
     * @param terminal     终结符集合
     * @param non_terminal 非终结符集合
     */
    private void init(Set<String> terminal, Set<String> non_terminal) {
        for (String non_terminalTag : non_terminal) {
            Map<String, List<String>> column = new HashMap<>();
            for (String terminalTag : terminal) {
                if(!terminalTag.equals("ε")) {
                    List<String> production = new ArrayList<>();
                    column.put(terminalTag, production);
                }
            }

            List<String> production = new ArrayList<>();
            column.put("$", production);
            table.put(non_terminalTag, column);
        }
    }
}
