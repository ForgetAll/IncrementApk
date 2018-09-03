package com.xiasuhuei321.incrementapk2

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe{
                }


        val destApk = File(Environment.getExternalStorageDirectory(), "dest.apk")
        val patch = File(Environment.getExternalStorageDirectory(), "xxx/PATCH.patch")

        patchApkBtn.setOnClickListener {
            Thread {
                bspatch(applicationInfo.sourceDir, destApk.absolutePath, patch.absolutePath)
                runOnUiThread { installApk(destApk) }
            }.start()
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    external fun bspatch(oldPth: String, newPath: String, path: String): Int

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private fun installApk(file: File) {
        try {
            val f = file
//            val f = File("sdcard/remeet/apk/remeet.apk")
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val apkUri = FileProvider.getUriForFile(this, "${applicationInfo.packageName}.installapk.provider", f)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive")
            }

            startActivity(intent)
        } catch (e: Exception) {
        } finally {

        }
    }
}
