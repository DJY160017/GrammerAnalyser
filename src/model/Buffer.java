package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 用于存放暂时的字节流
 *
 */
public class Buffer {

    /**
     * buffer的实际载体
     */
    private List<String> value;

    public Buffer() {
        value = new ArrayList<>();
    }

    /**
     * buffer填充
     *
     * @param character 需要填充的字符
     */
    public void add(char character) {
        value.add(String.valueOf(character));
    }

    /**
     * 获取buffer中存放的字符串值
     *
     * @return String 结果
     */
    public String getValue() {
        StringBuilder result =new StringBuilder();
        for(String temp_str: value){
            result.append(temp_str);
        }
        return result.toString();
    }

    /**
     * 判断buffer是否为空
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return value.isEmpty();
    }

    /**
     * 清空buffer
     *
     */
    public void clear(){
        value.clear();
    }
}
