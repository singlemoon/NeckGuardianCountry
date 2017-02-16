package com.simo.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * 保存文件到SD卡中
 * Created by 孤月悬空 on 2015/12/27.
 */
public class SaveUtils {

    public static int saveToSDCard(String url, Object data) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {     //判断SDcard是否存在，不存在不进行操作
            return -1;
        }

        File file = new File(Environment.getExternalStorageDirectory().getPath() + File.separator + url);

        if (!file.getParentFile().exists()) {   //如果文件不存在，就创建
            file.getParentFile().mkdirs();
        }
        PrintStream out = null;
        try {
            out = new PrintStream(file);
            out.print(data.toString());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return 0;
    }
}
