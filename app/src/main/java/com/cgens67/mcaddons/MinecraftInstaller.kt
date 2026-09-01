package com.cgens67.mcaddons

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object MinecraftInstaller {
    suspend fun downloadAndInstall(context: Context, addon: AddonItem): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Download file
                val bytes = SupabaseConfig.client.get(addon.fileUrl).readBytes()
                val file = File(context.cacheDir, addon.fileName)
                file.writeBytes(bytes)
                
                // Open in Minecraft
                launchMinecraft(context, file)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun launchMinecraft(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "*/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        try {
            val chooser = Intent.createChooser(intent, "Open with...")
            chooser.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
