package controller;

import model.PredictiveParseTable;
import model.Production;
import utility.InfoHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {

    /**
     * 字符栈
     */
    private Stack<String> stack;

    /**
     * 字符流
     */
    private List<String> stream;

    /**
     * 预测分析表
     */
    private PredictiveParseTable predictiveParseTable;

    private Production production;

    public Parser() {
        stack = new Stack<>();
        stack.push("$");
        Lex lex = new Lex();
        String root = System.getProperty("user.dir");
        String path = root + "\\src\\resource\\procedure.txt";
        stream = lex.scan(path);
        stream.add("$");
        Creator creator = new Creator();
        predictiveParseTable = creator.getPredictiveParseTable();
        production = creator.getProduction();
        stack.push(production.getStart());
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.handle();
    }

    /**
     *  完成整个过程
     */
    public void handle(){
        List<String> result = parse();
        InfoHandler infoHandler = new InfoHandler();
        infoHandler.write(result);
    }

    /**
     * 采用预测分析表，读入字符流，输出推导结果
     *
     * @return List<String> 推到结果 （自顶向下）
     */
    private List<String> parse() {
        List<String> result = new ArrayList<>();
        if (!predictiveParseTable.isLL()) {
            result.add("这个文法不是LL文法，请检查！");
            return result;
        }

        int index = 0;
        String top = stack.peek();
        while (!top.equals("$")) {

            if (top.equals(stream.get(index))) {
                String value = stack.pop();
                result.add("match: "+value);
                index++;
            } else if (production.isTerminal(top)) {
                result.add("error: 该程序不符合该文法！--terminal");
                return result;
            } else if (predictiveParseTable.getValue(top, stream.get(index)) == null) {
                result.add("error: 该程序不符合该文法！--null");
                return result;
            } else if (predictiveParseTable.getValue(top, stream.get(index)) != null) {
                String production_one = predictiveParseTable.getValue(top, stream.get(index));
                String[] str_production = production_one.split("->")[1].split("\\s");
                result.add("output: "+production_one);
                stack.pop();
                if(!str_production[0].equals("ε")){
                    for (int i = str_production.length - 1; i >= 0; i--) {
                        stack.push(str_production[i]);
                    }
                }
            }
            top = stack.peek();
        }
        return result;
    }
}
