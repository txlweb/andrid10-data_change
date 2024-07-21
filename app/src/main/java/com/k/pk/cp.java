package com.k.pk;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class cp extends Thread{
    private long all = 0;
    private long now = 0;
    private InputStream from = null;
    private OutputStream to = null;
    private String from_ = null;
    private String to_ = null;
    private Boolean ok = false;
    private Context context = null;
    public void init(String f,String t,Context ct) {
        this.context = ct;
        try {
            if (f.contains("com.k.pk")) {
                this.from = new FileInputStream(f);
            } else {
                this.from = context.getContentResolver().openInputStream(getDoucmentFile(ct, f).getUri());
            }
            if (t.contains("com.k.pk")) {
                this.to = new FileOutputStream(t);
            } else {
                this.to = context.getContentResolver().openOutputStream(getDoucmentFile(ct, t).getUri());
            }
            this.from_ = f;
            this.to_ = t;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    //根据路径获得document文件
    public static DocumentFile getDoucmentFile(Context context, String path) {

        if (path.endsWith("/")) {

            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return DocumentFile.fromSingleUri(context, Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2));
    }
    public void run(){
//        if(!to.isFile()){
//            try {
//                new File(to.getUri().getPath()).createNewFile();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
        all = new File(from_).length();
        try (InputStream fis = from;
             OutputStream fos = to){
            // 创建缓冲区
            byte[] buffer = new byte[1024];
            int length;
            // 从源文件读取内容并写入目标文件
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
                now += length;
            }
            ok=true;
            fis.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Boolean isOK() {
        return ok;
    }

    public float prs(){
        return (float) now / all*100;
    }
}
