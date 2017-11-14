package controller;

import model.Buffer;
import utility.InfoHandler;
import utility.TokenInfo;

import java.util.ArrayList;
import java.util.List;

public class Lex {

    /**
     * 输入流的指针
     */
    private int pointer;

    public static void main(String[] args) {
        Lex lex = new Lex();
        String root = System.getProperty("user.dir");
        String path = root + "\\src\\resource\\procedure.txt";
        List<String> result = lex.scan(path);
        System.out.println(result);
    }

    /**
     * 用于扫描输入流得到分隔开的字符
     *
     * @param path 需要扫描的文件
     * @return List<Token> Token序列
     */
    public List<String> scan(String path) {
        InfoHandler infoHandler = new InfoHandler();
        TokenInfo tokenInfo = new TokenInfo();
        List<String> tokens = new ArrayList<>();
        char[] input = infoHandler.reader(path);

        Buffer buffer = new Buffer();
        while (input[pointer] != '$') {

            if (tokenInfo.isPunctuation(String.valueOf(input[pointer]))) { //扫描到当前的字符为标点符号
                handlePunctuation(tokens, input, buffer);
                continue; //已判断为标点符号，无需继续下去
            }

            if (tokenInfo.isOperator(String.valueOf(input[pointer]))) {
                handleOperator(tokens, input, buffer);
                continue; //已判断为操作符，无需继续下去
            }

            if (input[pointer] == 32 || input[pointer] == '\n') {
                if (!buffer.isEmpty()) {
                    tokens.add(buffer.getValue());
                    buffer.clear();
                }
                pointer = pointer + 1;
            } else {
                buffer.add(input[pointer]);
                pointer = pointer + 1; //正常字符流前进
            }
        }

        return tokens;
    }

    /**
     * 当判断当前字符(input[pointer])为标点符号调用
     *
     * @param tokens 结果列表
     * @param input  输入流
     * @param buffer 需要添加字符的buffer
     */
    private void handlePunctuation(List<String> tokens, char[] input, Buffer buffer) {
        TokenInfo tokenInfo = new TokenInfo();
        if (input[pointer] == '.') {
            if (tokenInfo.isNumber(input[pointer + 1])) {
                buffer.add(input[pointer]); //代表是double类型，正常进行过程
                pointer++;
            } else { //代表是操作符，需要先把word变成token
                if (!buffer.isEmpty()) {
                    tokens.add(buffer.getValue());
                    buffer.clear();
                }
                tokens.add(String.valueOf(input[pointer]));
                pointer++;
            }
        } else if (input[pointer] == '"') {
            buffer.add(input[pointer]);
            pointer++;

        } else if (input[pointer] == '[') {
            if (input[pointer + 1] == ']') {
                buffer.add(input[pointer]);
                pointer++;
                buffer.add(input[pointer]);
                pointer++;
            } else {
                if (!buffer.isEmpty()) {
                    tokens.add(buffer.getValue());
                    buffer.clear();
                }
                tokens.add(String.valueOf(input[pointer]));
                pointer++;
            }
        } else { //普通标点符号
            if (!buffer.isEmpty()) {
                tokens.add(buffer.getValue());
                buffer.clear();
            }
            tokens.add(String.valueOf(input[pointer]));
            pointer++;
        }
    }

    /**
     * 当判断当前字符(input[pointer])为操作符调用
     *
     * @param tokens 结果列表
     * @param input  输入流
     * @param buffer 需要添加字符的buffer
     */
    private void handleOperator(List<String> tokens, char[] input, Buffer buffer) {
        TokenInfo tokenInfo = new TokenInfo();
        switch (input[pointer]) {
            case '+':
                if (input[pointer + 1] == '+') {
                    if (!buffer.isEmpty()) {
                        tokens.add(buffer.getValue());
                        buffer.clear();
                    }
                    tokens.add("++"); //默认前方无空格
                    pointer = pointer + 2; //跳过第二个+号
                } else if (input[pointer + 1] == '=') {
                    tokens.add("+=");
                    pointer = pointer + 2; //跳过=号
                } else {
                    tokens.add("+");
                    pointer = pointer + 1;
                }
                break;
            case '-':
                if (input[pointer + 1] == '-') {
                    if (buffer.isEmpty()) {
                        tokens.add(buffer.getValue());
                        buffer.clear();
                    }
                    tokens.add("--"); //默认前方无空格
                    pointer = pointer + 2; //跳过第二个+号
                } else if (input[pointer + 1] == '=') {
                    tokens.add("-=");
                    pointer = pointer + 2; //跳过=号
                } else if (tokenInfo.isNumber(input[pointer + 1])) {
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                } else {
                    tokens.add("-");
                    pointer = pointer + 1;
                }
                break;
            case '/':
                if (input[pointer + 1] == '=') {
                    tokens.add("/=");
                    pointer = pointer + 2; //跳过=号
                } else if (input[pointer + 1] == '*') {
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    buffer.add(input[pointer]); // 跳过第一个*
                    pointer = pointer + 1;

                    while ((input[pointer] != '*') || (input[pointer + 1] != '/')) {
                        buffer.add(input[pointer]);
                        pointer = pointer + 1;
                    }

                    // 跳过最后的*/
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    if (!buffer.isEmpty()) {
                        tokens.add(buffer.getValue());
                        buffer.clear();
                    }
                    pointer = pointer + 1;
                } else if (input[pointer + 1] == '/') {
                    buffer.add(input[pointer]);
                    pointer = pointer + 1;
                    buffer.add(input[pointer]); // 跳过//
                    pointer = pointer + 1;

                    while (input[pointer] != '\n') {
                        buffer.add(input[pointer]);
                        pointer = pointer + 1;
                    }

                    if (!buffer.isEmpty()) {
                        tokens.add(buffer.getValue());
                        buffer.clear();
                    }
                    pointer = pointer + 1;
                } else {
                    tokens.add("/");
                    pointer = pointer + 1;
                }
                break;
            case '&':
                if (input[pointer + 1] == '&') {
                    tokens.add("&&");
                    pointer = pointer + 2; //跳过&号
                } else {
                    tokens.add("&");
                    pointer = pointer + 1;
                }
                break;
            case '|':
                if (input[pointer + 1] == '|') {
                    tokens.add("||");
                    pointer = pointer + 2; //跳过|号
                } else {
                    tokens.add("|");
                    pointer = pointer + 1;
                }
                break;
            default:
                if (input[pointer + 1] == '=') {
                    tokens.add(String.valueOf(input[pointer]) + "=");
                    pointer = pointer + 2; //跳过=号
                } else {
                    tokens.add(String.valueOf(input[pointer]));
                    pointer = pointer + 1;
                }
                break;
        }
    }
}
