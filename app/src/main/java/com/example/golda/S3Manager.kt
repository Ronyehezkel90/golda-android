package com.example.golda

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.S3ClientOptions
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class S3Manager @Inject constructor(val context: Context) {
    val s3: AmazonS3Client
    val transferUtility: TransferUtility

    init {
        val ACCESS_KEY = "AKIA6NBK6PIA4SYATZE6"
        val SECRET_KEY = "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"
        val credentials = BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)
        s3 = AmazonS3Client(credentials)
        java.security.Security.setProperty("networkaddress.cache.ttl", "60")
        s3.setRegion(Region.getRegion(Regions.EU_WEST_1))
        s3.endpoint = "https://s3-eu-west-1.amazonaws.com/"
        s3.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).disableChunkedEncoding().build())
        transferUtility = TransferUtility.builder().s3Client(s3).defaultBucket("appgolda-pictures")
            .context(context).build()
    }

    private fun bitmapToImage(imgAsBitmap: Bitmap, imgKey: String): File {
        val imgAsFile = File(context.cacheDir, imgKey)
        imgAsFile.createNewFile()
        val byteArrayOutputStream = ByteArrayOutputStream()
        imgAsBitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream)
        val bitmapData = byteArrayOutputStream.toByteArray()
        val fileOutputStream = FileOutputStream(imgAsFile)
        fileOutputStream.write(bitmapData)
        fileOutputStream.flush()
        fileOutputStream.close()
        return imgAsFile
    }

    fun uploadImage(file: File, imgKey: String) {
        transferUtility.upload("appgolda-pictures", imgKey, file).setTransferListener(
            object : TransferListener {
                override fun onStateChanged(id: Int, state: TransferState) {
                    Timber.d("onStateChanged")
                    if (state == TransferState.COMPLETED) {
                        val imageFile = File(Environment.getExternalStorageDirectory(), "temp_image.jpg").path
                        val imageBitmap = BitmapFactory.decodeFile(imageFile)
//                        reviewItemWithImage.imageBitmap = imageBitmap
                    } else if (state == TransferState.FAILED || state == TransferState.WAITING_FOR_NETWORK) {
                        Timber.d("upload failed")
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    Timber.d("onProgressChanged")
                }

                override fun onError(id: Int, ex: Exception) {
                    Timber.d("Error")
                }
            })
    }

    fun downloadPic(imageKey: String): TransferObserver? {
        return transferUtility.download(imageKey, File(getExternalStorageDirectory(), imageKey))
    }
}