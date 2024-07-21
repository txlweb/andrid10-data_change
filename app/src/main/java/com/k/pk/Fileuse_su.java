package com.k.pk;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.k.pk.MainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fileuse_su {
    public static String run_sh(String command){
        try {
            System.out.println(command);
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
            System.out.println("--------------SHELL COMMAND RUNNING--------------");
            System.out.println(output);
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "ERROR";
    }
    public static List<String> get_process_pkg_names(Context context){
        List<String> r = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        for (PackageInfo lp : pm.getInstalledPackages(0)){

//            if (((ApplicationInfo.FLAG_SYSTEM & lp.applicationInfo.flags) == 0)
//                    && ((ApplicationInfo.FLAG_UPDATED_SYSTEM_APP & lp.applicationInfo.flags) == 0)
//                    && ((ApplicationInfo.FLAG_STOPPED & lp.applicationInfo.flags) == 0)) {

                // 第三方正在运行的 app 进程信息  更多信息查询 PackageInfo 类
               System.out.println("packageName=" + lp.packageName
                        + "  processName=" + lp.applicationInfo.processName);
               r.add(lp.packageName);
            //}
        }
        return r;
    }
    public static boolean cfg_to_bash(String d, Boolean su,int mode,Context ct,String sp){
        String p = "";
        String b = "";
        String c = "";
        if(su) {
            console.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    console.csv.setText(console.csv.getText() + "\r\n" + "RUN MODE -> SU SHELL");
                }
            });
        }else{
            console.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    console.csv.setText(console.csv.getText() + "\r\n" + "RUN MODE -> USER JAVA");
                }
            });
        }
        try {
            FileInputStream fis = new FileInputStream(d+"/cfg.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            new File(d+"/backup/").mkdir();
            int i =0;
            String line;
            while ((line = br.readLine()) != null) {
                String finalLine = line;
                finalLine=finalLine.replaceAll("%SAVED%",sp+"/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/");
                finalLine=finalLine.replaceAll("%BASE%",sp+"/");
                i++;
                if(finalLine.contains("//")){
                    if(finalLine.contains("@echo")){
                        String finalLine1 = finalLine;
                        console.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                console.csv.setText(console.csv.getText()+"\r\n"+"[I]: "+ finalLine1);
                            }
                        });
                    }
                }
                if(finalLine.contains("|")){
                    String[] v = finalLine.split("\\|");
                    if(v.length!=2){
                        String finalLine2 = finalLine;
                        int finalI = i;
                        console.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                console.csv.setText(console.csv.getText()+"\r\n"+"[E]: NOT CREATE SH line["+ finalI +"]: "+ finalLine2);
                            }
                        });
                        return false;
                    }
                    if(mode == 0){//备份
                        console.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                console.csv.setText(console.csv.getText() + "\r\n" + "Runtime: backup");
                            }
                        });
                        if(su){

                            console.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    console.csv.setText(console.csv.getText()+"\r\n[shsu]: "+"cp \""+v[1]+"/"+v[0]+"\" \""+d+"/backup/"+"\"");
                                    console.csv.setText(console.csv.getText()+"\r\n"+run_sh("cp \""+v[1]+"/"+v[0]+"\" \""+d+"/backup/"+v[0]+"\""));
                                }
                            });
                        }else{
                            console.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    console.csv.setText(console.csv.getText()+"\r\n[cpth]: "+"cp \""+v[1]+"/"+v[0]+"\" \""+d+"/backup/"+"\"\n\n");
                                }
                            });
                            cp copyth = new cp();
                            copyth.init(v[1]+"/"+v[0],d+"/backup/"+v[0],ct);
                            copyth.start();
                            while (!copyth.isOK()){
                                Thread.sleep(500);
                                console.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        console.csv.setText(console.csv.getText()+"\r\n[cp]: 已完成"+copyth.prs()+"%");
                                    }
                                });
                            }
                        }
                    }
                    if(mode == 1){//复制
                        console.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                console.csv.setText(console.csv.getText() + "\r\n" + "Runtime: push");
                            }
                        });
                        if(su){

                            console.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    console.csv.setText(console.csv.getText()+"\r\n[shsu]: "+"cp \""+d+"/"+v[0]+"\" \""+v[1]+"/"+v[0]+"\"");
                                    console.csv.setText(console.csv.getText()+"\r\n"+run_sh("cp \""+d+"/"+v[0]+"\" \""+v[1]+"\""));
                                }
                            });
                        }else{
                            console.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    console.csv.setText(console.csv.getText()+"\r\n[cpth]: "+"cp \""+d+"/"+v[0]+"\" \""+v[1]+"/"+v[0]+"\"\n\n");
                                }
                            });
                            cp copyth = new cp();
                            copyth.init(d+"/"+v[0],v[1]+"/"+v[0],ct);
                            copyth.start();
                            while (!copyth.isOK()){
                                Thread.sleep(500);
                                console.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        console.csv.setText(console.csv.getText()+"\r\n[cp]: 已完成"+copyth.prs()+"%");
                                    }
                                });
                            }
                        }
                    }
                    if(mode == 2){//还原
                        console.mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                console.csv.setText(console.csv.getText() + "\r\n" + "Runtime: return");
                            }
                        });
                        if(su){
                            console.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    console.csv.setText(console.csv.getText()+"\r\n[shsu]: "+"cp \""+d+"/backup/"+v[0]+"\" \""+v[1]+"\"");
                                    console.csv.setText(console.csv.getText()+"\r\n"+run_sh("cp \""+d+"/backup/"+v[0]+"\" \""+v[1]+"\""));
                                }
                            });
                        }else{
                            console.mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    console.csv.setText(console.csv.getText()+"\r\n[cpth]: "+"cp \""+d+"/backup/"+v[0]+"\" \""+v[1]+"\"\n\n");
                                }
                            });
                            cp copyth = new cp();
                            copyth.init(d+"/backup/"+v[0],v[1]+"/"+v[0],ct);
                            copyth.start();
                            while (!copyth.isOK()){
                                Thread.sleep(500);
                                console.mainHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        console.csv.setText(console.csv.getText()+"\r\n[cp]: 已完成"+copyth.prs()+"%");
                                    }
                                });
                            }
                        }
                    }
                    //push
                    p=p+"cp \""+d+"/"+v[0]+"\" \""+v[1]+"\" \r\n";
                    //backup
                    b=b+"cp \""+v[1]+"/"+v[0]+"\" \""+d+"/backup/"+"\" \r\n";
                    //back
                    c=c+"cp \""+v[1]+"/backup/"+v[0]+"\" \""+d+"/"+"\" \r\n";
                }
            }
            br.close();
            new File(d+"/push.sh").createNewFile();
            FileOutputStream pp = new FileOutputStream(d+"/push.sh");
            pp.write(p.getBytes());
            pp.close();
            new File(d+"/backup.sh").createNewFile();
            FileOutputStream bb = new FileOutputStream(d+"/backup.sh");
            bb.write(b.getBytes());
            bb.close();
            new File(d+"/back.sh").createNewFile();
            FileOutputStream cc = new FileOutputStream(d+"/back.sh");
            cc.write(c.getBytes());
            cc.close();
            console.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    console.csv.setText(console.csv.getText()+"\r\n[脚本执行完毕,现在可以退出了]"+"%");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

            console.mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    console.csv.setText(console.csv.getText()+"\r\n [E]: "+e);
                }
            });
            return false;
        }
        return true;
    }



    public static String get_fn(String path){
        String[] v = path.split("/");
        if(v.length==1){
            return path;
        }
        return v[v.length-1];
    }
}
