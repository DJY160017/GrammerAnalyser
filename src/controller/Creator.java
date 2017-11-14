package controller;

import utility.InfoHandler;
import model.PredictiveParseTable;
import model.Production;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Creator {

    /**
     * 所有产生式
     */
    private Production production;

    public Creator() {
        String root = System.getProperty("user.dir");
        String path = root + "\\src\\resource\\CFG.txt";
        InfoHandler infoHandler = new InfoHandler();
        production = infoHandler.read(path);
    }

    /**
     *  生成预测分析表
     *
     * @return PredictiveParseTable 预测分析表
     */
    public PredictiveParseTable getPredictiveParseTable(){
        Map<String, List<String>> productions = production.getProductions();
        PredictiveParseTable predictiveParseTable = new PredictiveParseTable(production.getTerminal(),production.getNon_terminal());

        for(String key: productions.keySet()){
            for(String sub: productions.get(key)){
                if(sub.equals("ε")){
                    Set<String> result = follow(key);
                    for(String tag: result){
                        if(tag.equals("ε")){
                            predictiveParseTable.addValue(key, "$", key+"->"+sub);
                        } else{
                            predictiveParseTable.addValue(key, tag, key+"->"+sub);
                        }
                    }
                } else{
                    Set<String> result = first(key);
                    String[] sub_str = sub.split("\\s");
                    if(result.contains(sub_str[0])){
                        predictiveParseTable.addValue(key, sub_str[0], key+"->"+sub);
                        continue;
                    }

                    for(String tag: result){
                        if(!tag.equals("ε")){
                            predictiveParseTable.addValue(key, tag, key+"->"+sub);
                        }
                    }
                }
            }
        }

        return predictiveParseTable;
    }

    /**
     *  获取产生式
     *
     * @return Production 产生式
     */
    public Production getProduction() {
        return production;
    }

    /**
     * 用于完成LL（1）中的first
     *
     * @param non_terminal 需要查找的非终结符号
     * @return 所有的first
     */
    private Set<String> first(String non_terminal) {
        Map<String, List<String>> productions = production.getProductions();
        List<String> production_one = productions.get(non_terminal);
        Set<String> result = new HashSet<>();

        for (String one : production_one) {
            String[] one_group = one.split("\\s");
            if (production.isTerminal(one_group[0])) {
                if(one_group[0].equals("ε")){
                    Set<String> next = follow(non_terminal);
                    result.addAll(next);
                } else {
                    result.add(one_group[0]);
                }
            } else {
                Set<String> next = first(one_group[0]);
                if (next.contains("ε")) {
                    for (int i = 1; i < one_group.length; i++) {
                        if (production.isTerminal(one_group[i])) {
                            result.add(one_group[i]);
                            break;
                        } else {
                            Set<String> next_next = first(one_group[i]);
                            if (next_next.contains("ε")) {
                                next_next.remove("ε");
                                result.addAll(next_next);
                            } else {
                                result.addAll(next_next);
                                break;
                            }
                        }
                    }
                }
                next.remove("ε");
                result.addAll(next);
            }
        }
        return result;
    }

    /**
     * 用于完成LL（1）中的follow
     *
     * @param non_terminal 需要查找的非终结符号
     * @return Set<String> 结果
     */
    private Set<String> follow(String non_terminal) {
        Map<String, List<String>> productions = production.getProductions();
        Set<String> result = new HashSet<>();

        for (String key : productions.keySet()) {
            List<String> production_one = productions.get(key);
            for (String subProduction : production_one) {

                if(key.equals(non_terminal)&&subProduction.equals("ε")){
                    result.add("ε");
                }

                if (subProduction.contains(non_terminal)) {
                    if(subProduction.endsWith(non_terminal)){
                        if(!key.equals(non_terminal)) {
                            result.add("ε");
                            Set<String> next = follow(key);
                            result.addAll(next);
                        } else{
                            result.add("ε");
                        }
                        continue;
                    }

                    String[] temp_one = subProduction.split("\\s");
                    String tag = null;
                    for(int i =0; i<temp_one.length;i++){
                        if(temp_one[i].equals(non_terminal)){
                            tag = temp_one[i+1];
                            break;
                        }
                    }

                    if(tag == null){
                        continue;
                    }

                    if(production.isTerminal(tag)){
                        result.add(tag);
                    } else{
                        Set<String> next = first(tag);
                        result.addAll(next);
                    }
                }
            }
        }
        return result;
    }
}
