package utility;

import java.util.Arrays;
import java.util.List;

public class TokenInfo {

    /**
     * 操作符
     */
    private List<String> operators = Arrays.asList("+", "-", "*", "/", "|", ">", "<", "=", "!", "&", "==",
            ">=", "<=", "+=", "-=", "*=", "/=", "&&", "||",
            "!=");

    /**
     * 标点符号
     */
    private List<String> punctuation = Arrays.asList("{", "}", ";", "(", ")", "[", "]", ":", "\"", ",", ".");

    /**
     * 判断当前字符是否为字母
     *
     * @param letter 输入的字符
     * @return boolean 判断结果
     */
    public boolean isLetter(char letter) {
        return (letter <= 'z' && letter >= 'a') || (letter <= 'Z' && letter >= 'A');
    }

    /**
     * 判断当前字符是否为数字
     *
     * @param letter 输入的字符
     * @return boolean 判断结果
     */
    public boolean isNumber(char letter) {
        return (letter <= '9' && letter >= '0');
    }

    /**
     * 判断当前字符是否为操作符
     *
     * @param word 输入的字符串
     * @return boolean 判断结果
     */
    public boolean isOperator(String word) {
        return operators.contains(word);
    }

    /**
     * 判断当前字符是否为标点符号
     *
     * @param word 输入的字符串
     * @return boolean 判断结果
     */
    public boolean isPunctuation(String word) {
        return punctuation.contains(word);
    }
}
