package Code;

import java.util.*;
import java.io.*;

public class Code{
    private ArrayList<String> code;
    private final String DOUBLE_SLASH;
    private final String SLASH_ASTERISK;
    private final String ASTERISK_SLASH;

    public Code () throws Exception{
        DOUBLE_SLASH = "//";
        SLASH_ASTERISK = "/*";
        ASTERISK_SLASH = "*/";
        code = new ArrayList<>();
    }

    public void loadFromFile (String fileName) throws Exception{
        code.clear();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s = br.readLine();
        while (s != null){
            code.add(s);
            s = br.readLine();
        }
    }

    public void saveToFile (String fileName) throws Exception{
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (String item : code){
            bw.write(item);
            bw.write("\n");
        }
        bw.close();
    }


    public void delComments (){
        int position;
        int newPosition;
        for (int i = 0; i < code.size(); i++){
            position = 0;
            while ((newPosition = code.get(i).indexOf(DOUBLE_SLASH, position)) != -1){
                delLine(i, newPosition);
                position = newPosition + 1;
            }
            position = 0;
            while ((newPosition = code.get(i).indexOf(SLASH_ASTERISK, position)) != -1){
                position = delBlock(i, newPosition);
            }
        }

    }

    private int delBlock (int line, int position){
        String res;
        if (isInString(line, position)){
            return position+1;
        }
        int endLine = code.size() - 1;
        int endPos = 0;
        for (int i = line; i < code.size(); i++){
            if ((endPos = code.get(i).indexOf(ASTERISK_SLASH,position+1)) != -1 && !isInString(i, endPos)){
                endLine = i;
                break;
            }
        }
        res = code.get(line).substring(0, position);
        if (line == endLine){
            res += code.get(line).substring(endPos + 2);
            code.remove(line);
        }
        else{
                code.add(line, res);
            for (int i = line+1; i < endLine+1; i++){
                code.remove(line+1);
            }

            res = code.remove(line+1).substring(endPos+2);
        }
        if (!res.matches("^( |\t)*$")){
            code.add(line, res);
        }
        return position+1;
    }


    private boolean delLine (int line, int position){
        if (isInString(line, position)){
            return false;
        }

        char[] chars = code.get(line).toCharArray();
        char[] newChars = new char[position];
        String res;

        for (int i = 0; i < position; i++){
            newChars[i] = chars[i];
        }

        code.remove(line);

        res = new String(newChars);
        if (!res.matches("^( |\t)*$")){
            code.add(line, res);
        }
        return true;
    }

    private boolean isInString (int line, int position){
        boolean isStr;
        char[] chars = code.get(line).toCharArray();
        int numS = 0;
        for (int i = 0; i < position; i++){
            if (chars[i] == '"'){
                int numSl = 0;
                isStr = true;
                int j = i - 1;
                while (j >= 0 && chars[j] == '\\'){
                    numSl++;
                    j--;
                }
                if (numSl % 2 != 0){
                    isStr = false;
                }
                if (i > 0 && i < position - 1){
                    if (chars[i - 1] == '\'' && chars[i + 1] == '\''){
                        isStr = false;
                    }
                }
                if (isStr){
                    numS++;
                }
            }
        }
        return numS % 2 != 0;
    }
}
