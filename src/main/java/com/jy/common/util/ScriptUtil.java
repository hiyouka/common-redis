package com.jy.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Function:
 *
 * @author jianglei
 * Date: 2019/1/7
 * @since JDK 1.8
 */
public class ScriptUtil {

    /**
     * return lua script String
     *
     * @param path
     * @return
     */
    public static String getScript(String path) {
        StringBuilder sb = new StringBuilder();

        InputStream stream = ScriptUtil.class.getClassLoader().getResourceAsStream(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){

            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str).append(System.lineSeparator());
            }

        } catch (IOException e) {
            System.err.println(e.getStackTrace());
        }
        return sb.toString();
    }

}