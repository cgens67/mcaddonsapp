package com.cgens67.mcaddons

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object MinecraftInstaller {
    const val MINECRAFT_PACKAGE = "com.mojang.minecraftpe"

    suspend fun downloadAndInstall(
        context: Context, 
        addon: AddonItem,
        directToMinecraft: Boolean = true
    ): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Download file if not yet cached or empty
                val file = File(context.cacheDir, addon.fileName)
                if (!file.exists() || file.length() == 0L) {
                    val bytes = SupabaseConfig.client.get(addon.fileUrl).readBytes()
                    file.writeBytes(bytes)
                }
                
                // Open in Minecraft or App Selector
                withContext(Dispatchers.Main) {
                    launchMinecraft(context, file, directToMinecraft)
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private fun launchMinecraft(context: Context, file: File, directToMinecraft: Boolean) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "*/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        if (directToMinecraft) {
            intent.setPackage(MINECRAFT_PACKAGE)
            try {
                context.startActivity(intent)
                return
            } catch (e: ActivityNotFoundException) {
                // Minecraft is not installed or cannot handle the direct intent; fallback to chooser
                intent.setPackage(null)
            } catch (e: Exception) {
                e.printStackTrace()
                intent.setPackage(null)
            }
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
