package com.k.pk

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipInputStream


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // 权限未被授予，请求权限
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.INTERNET
                ),
                1)
        } else {

        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("授权提示 [ROOT/文件管理器]")
            .setMessage("建议用系统授权,如果系统授权不能成功操作其他应用文件再用ROOT授权,如果使用ROOT授权,如果不成功程序将会自动退出.")
            .setPositiveButton("继续 (ROOT)") { dialog, which ->
                try {
                    val process = Runtime.getRuntime().exec("su")
                    val os = DataOutputStream(process.outputStream)
                    os.writeBytes("exit\n")
                    os.flush()
                    val exitVal = process.waitFor()
                    if (exitVal == 0) {
                        Toast.makeText(
                            this@MainActivity,
                            "SU权限获取成功!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        su_mode = true
                    } else {
                        Toast.makeText(this@MainActivity, "SU权限获取失败!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    File(this@MainActivity.applicationContext.filesDir.toString() + "/su.lock")
                        .createNewFile()
                } catch (e: IOException) {
                    throw RuntimeException(e)
                } catch (e: InterruptedException) {
                    throw RuntimeException(e)
                }
                dialog.dismiss()
            }
            .setNegativeButton(
                "继续 (系统)"
            ) { dialog, which ->
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.setFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // 请求Android/data目录的权限，Android/obb目录则把data替换成obb即可。
                    val treeUri =
                        Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata")
                    val df: DocumentFile? = DocumentFile.fromTreeUri(this, treeUri)
                    if (df != null) {
                        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, df.getUri())
                    }
                }
                startActivityForResult(intent, 1001)
            } // 如果不需要取消按钮，可以传递null

        if(File(this@MainActivity.applicationContext.filesDir.toString() + "/su.lock").isFile){
            su_mode = true
            Toast.makeText(
                this@MainActivity,
                "正在使用SU授权",
                Toast.LENGTH_SHORT
            )
        }else{
            if(File(this@MainActivity.applicationContext.filesDir.toString() + "/sys.lock").isFile){
                su_mode = false
                Toast.makeText(
                    this@MainActivity,
                    "正在使用系统授权!",
                    Toast.LENGTH_SHORT
                )
            }else{
                val dialog = builder.create()
                dialog.show()
            }
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        findViewById<TextView>(R.id.textView2).setText("用法:" +
                "**!!! 这只是个更方便的改文件工具,里面不包含文件,请自己下载导入制作!\n" +
                "先点导入配置,选择你要改的文件压缩成zip,注意:所有文件都放在压缩包根目录里,不然不能自动扫描.\n" +
                "导入完成后,如果里面包含cfg.txt,则不会扫描,如果没有,会为里面的文件创建索引并格式化默认的改法\n" +
                "在使用之前先编辑配置查看是否有问题,点击改的时候自动备份,一定要在改之前点还原,不然他会再备份一遍破坏之前的备份!\n" +
                "系统保留字:%SAVED% = /storage/emulated/0/Android/data/你选的游戏包名/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/\n" +
                "%BASE% = /storage/emulated/0/Android/data/你选的游戏包名/\n" +
                "注意:|左侧为压缩包里面的文件名,右侧为复制到的路径,文件名不会改变!!理论上可以改其他四服的文件,没测过.")

        findViewById<Button>(R.id.button).setOnClickListener {

            Thread { // 执行一些耗时的操作
                val intent = Intent()
                intent.setClass(this, console::class.java)
                startActivity(intent)
                Thread.sleep(500)
                if(findViewById<Switch>(R.id.switch2).isChecked){
                    Fileuse_su.cfg_to_bash(this.applicationContext.filesDir.toString(), su_mode,0,this,"/storage/emulated/0/Android/data/com.tencent.ig")
                    Fileuse_su.cfg_to_bash(this.applicationContext.filesDir.toString(), su_mode,1,this,"/storage/emulated/0/Android/data/com.tencent.ig")
                }else{
                    Fileuse_su.cfg_to_bash(this.applicationContext.filesDir.toString(), su_mode,0,this,"/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd")
                    Fileuse_su.cfg_to_bash(this.applicationContext.filesDir.toString(), su_mode,1,this,"/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd")
                }
            }.start()
        }
        findViewById<Button>(R.id.button2).setOnClickListener {

            Thread { // 执行一些耗时的操作
                val intent = Intent()
                intent.setClass(this, console::class.java)
                startActivity(intent)
                Thread.sleep(500)
                if(findViewById<Switch>(R.id.switch2).isChecked) {
                    Fileuse_su.cfg_to_bash(this.applicationContext.filesDir.toString(), su_mode,2,this,"/storage/emulated/0/Android/data/com.tencent.ig")
                }else{
                    Fileuse_su.cfg_to_bash(this.applicationContext.filesDir.toString(), su_mode,2,this,"/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd")
                }
            }.start()
        }
        findViewById<Button>(R.id.button3).setOnClickListener {
            val chooseFile: Intent
            val intent: Intent
            chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.setType("*/*")
            intent = Intent.createChooser(chooseFile, "选择带有文件的*压缩包*!")
            startActivityForResult(intent, 1002)
        }
        findViewById<Button>(R.id.button4).setOnClickListener {
            val file = File(this.applicationContext.filesDir.toString()+"/cfg.txt")
            val uri = FileProvider.getUriForFile(this, "${this.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "text/plain")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            this.startActivity(intent)
        }
//            Toast.makeText(
//                this@MainActivity,
//                "和平已经启动.",
//                Toast.LENGTH_SHORT
//            ).show()

    }

    companion object {
        var su_mode: Boolean? = false;
    }
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data);
        var uri: Uri? = data?.getData();
        if(requestCode == 1001) {
            if (data != null && uri != null) {
                // 授权成功
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                Toast.makeText(this@MainActivity, "已获取data权限!", Toast.LENGTH_SHORT)
                    .show()
                File(this@MainActivity.applicationContext.filesDir.toString() + "/sys.lock")
                    .createNewFile()
            } else {
                Toast.makeText(this@MainActivity, "data权限获取失败!", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
            return
        }
        //if (resultCode == 1002) {
        val filePath = uri?.path
        Toast.makeText(
            this@MainActivity, filePath,
            Toast.LENGTH_LONG
        ).show()
        try {
            uri?.let {
                contentResolver.openInputStream(it).use { inputStream ->
                    saveFileToPrivateDirectory(
                        inputStream!!,
                        this.applicationContext.filesDir.toString()+"/tmp.zip"
                    )
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        File(this.applicationContext.filesDir.toString()+"/cfg.txt").delete()

        unzipFile(this.applicationContext.filesDir.toString()+"/tmp.zip",this.applicationContext.filesDir.toString())
        File(this.applicationContext.filesDir.toString()+"/tmp.zip").delete()
        val directory: File = File(
            this.applicationContext.filesDir.toString()
        ) // 替换为实际目录路径

        val files = directory.listFiles()
        var t = "//@echo [tool by.K]\r\n\r\n";
        if (files != null) {
            for (file in files) {
                if (file.isFile() && file.name != "sys.lock" && file.name != "su.lock" && file.name != "profileInstalled") {
                    t=t+file.getName()+"|%SAVED%/Paks/\r\n"
                }
            }
        }
        if(File(this.applicationContext.filesDir.toString()+"/cfg.txt").isFile) return

        val ot = FileOutputStream(this.applicationContext.filesDir.toString()+"/cfg.txt")
        ot.write(t.encodeToByteArray());
        System.out.println(t)
        //}
    }
    private fun saveFileToPrivateDirectory(inputStream: InputStream, save_file: String) {
        val file = File(save_file)
        try {
            FileOutputStream(file).use { outputStream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    // 解压ZIP文件到内部存储目录
    fun unzipFile(zipFilePath: String?, destPath: String): Boolean {
        val destDir = File(destPath)
        if (!destDir.exists()) {
            destDir.mkdirs()
        }
        try {
            ZipInputStream(FileInputStream(zipFilePath)).use { zipIn ->
                var zipEntry = zipIn.nextEntry
                while (zipEntry != null) {
                    val filePath =
                        destPath + File.separator + zipEntry.name
                    if (!zipEntry.isDirectory) {
                        // 如果是文件，则解压
                        extractFile(zipIn, filePath)
                    } else {
                        // 如果是目录，则创建目录
                        val dir = File(filePath)
                        dir.mkdir()
                    }
                    zipIn.closeEntry()
                    zipEntry = zipIn.nextEntry
                }
                return true
            }
        } catch (e: IOException) {
            Log.e("FileUtils", "Error unzipping file", e)
            return false
        }
    }

    // 辅助方法：从ZipInputStream中提取文件
    @Throws(IOException::class)
    private fun extractFile(zipIn: ZipInputStream, filePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(filePath))
        val bytesIn = ByteArray(4096)
        var read = 0
        while (zipIn.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }


}

