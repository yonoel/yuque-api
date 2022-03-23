package com.zyq.yuquetransfer.file;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * 递归删除整个文件夹
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 创建子文件夹
     *
     * @param dir
     * @param dirName
     * @return
     * @throws IOException
     */
    public static File createChildDir(File dir, String dirName) throws IOException {
        String dirPath = dir.getPath() + File.separator + dirName;
//        System.out.println(dirPath);
        File file = new File(dirPath);
        file.mkdir();
        return file;
    }

    /**
     * 创建子文件
     *
     * @param dir
     * @param fileName
     * @return
     * @throws IOException
     */
    public static File createChildFile(File dir, String fileName) throws IOException {
        String filePath = dir.getPath() + File.separator + fileName;
//        System.out.println(filePath);
        File file = new File(filePath);
        file.createNewFile();
        return file;
    }

}
