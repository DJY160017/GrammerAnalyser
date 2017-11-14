package utility;

import model.Production;

import java.io.*;
import java.util.*;

public class InfoHandler {

    /**
     * 读取文法
     *
     * @param path 文法文件路径
     * @return 返回文法的对象
     */
    public Production read(String path){
        Map<String, List<String>> result = new HashMap<>();
        File file = new File(path);
        String start = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line=bufferedReader.readLine())!=null){

                if(line.equals("StartTag:")){
                    start = bufferedReader.readLine();
                    continue;
                }

                if(line.equals("Grammar:")){
                    continue;
                }

                String[] left_right = line.split("->");
                String[] all_right = left_right[1].split("\\|");
                List<String> production = new ArrayList<>();
                for(String character: all_right){
                    if(character.endsWith(" ")){
                        character = character.substring(0, character.length()-1);
                    }

                    if(character.startsWith(" ")){
                        character = character.substring(1);
                    }

                    production.add(character);
                }
                result.put(left_right[0], production);
            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Production(result, start);
    }

    /**
     * 从文件读取字符流
     *
     * @param path 文件路径
     * @return char[] 输入流
     */
    public char[] reader(String path) {
        char[] input = new char[2000];
        int pointer = 0;

        try {
            File file = new File(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                char[] line_buffer = line.toCharArray();
                for (char character : line_buffer) {
                    input[pointer] = character;
                    pointer++;
                }
                input[pointer] = '\n';
                pointer++;
            }

            input[pointer] = '$'; //作为缓冲区的结束符
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Read File: " + path + " Failed");
        }
        return input;
    }

    /**
     *  向文件中写入结果
     *
     * @param result 需要写入的信息
     */
    public void write(List<String> result){
        String root = System.getProperty("user.dir");
        String path = root + "\\src\\resource\\result.txt";
        File file = new File(path);
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            for(String line: result){
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
