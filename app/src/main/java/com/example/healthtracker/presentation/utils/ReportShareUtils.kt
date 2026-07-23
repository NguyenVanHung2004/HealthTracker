package com.example.healthtracker.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import com.example.healthtracker.R
import java.io.File
import java.io.FileOutputStream

object ReportShareUtils {

    fun shareComposableAsImage(
        context: Context,
        chooserTitle: String = context.getString(R.string.share_chooser_title),
        content: @Composable () -> Unit
    ) {
        val activity = context as? Activity ?: run {
            Toast.makeText(context, context.getString(R.string.error_occurred), Toast.LENGTH_SHORT).show()
            return
        }
        val rootLayout = activity.findViewById<ViewGroup>(android.R.id.content) ?: return

        val composeView = ComposeView(context).apply {
            visibility = View.INVISIBLE
            setContent { content() }
        }

        rootLayout.addView(composeView)

        composeView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val width = composeView.width
                val height = composeView.height

                if (width > 0 && height > 0) {
                    val bitmap = createBitmap(width, height)
                    val canvas = Canvas(bitmap)
                    canvas.drawColor(android.graphics.Color.WHITE)
                    composeView.draw(canvas)

                    shareBitmapToSocial(context, bitmap, chooserTitle)
                }

                rootLayout.removeView(composeView)
            }
        })
    }

    private fun shareBitmapToSocial(context: Context, bitmap: Bitmap, chooserTitle: String) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()

            val newFile = File(cachePath, "weekly_health_report.png")
            val stream = FileOutputStream(newFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val contentUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                newFile
            )

            if (contentUri != null) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                    putExtra(Intent.EXTRA_STREAM, contentUri)
                    type = "image/png"
                }
                context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                context,
                context.getString(R.string.share_report_error, e.localizedMessage ?: ""),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
