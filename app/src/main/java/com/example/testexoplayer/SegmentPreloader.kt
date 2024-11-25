package com.example.testexoplayer

import android.util.Log
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SegmentPreloader {

    private val client = OkHttpClient()

    fun preloadSegments(baseUrl: String, segmentCount: Int, saveDir: File) {
        // Tạo thư mục lưu nếu chưa tồn tại
        if (!saveDir.exists()) {
            saveDir.mkdirs()
        }

        for (i in 0 until segmentCount) {
            val segmentUrl = "$baseUrl/nquEby$i.ts"
            val fileName = "nquEby$i.ts"
            val file = File(saveDir, fileName)
            Log.d("SegmentPreloader", "Preloading segment: $segmentUrl")

            // Nếu file đã tồn tại, bỏ qua tải lại
            if (file.exists()) continue

            // Tải segment
            val request = Request.Builder().url(segmentUrl).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    response.body?.let { body ->
                        // Lưu vào file cục bộ
                        FileOutputStream(file).use { output ->
                            output.write(body.bytes())
                        }
                        println("Tải thành công: $fileName")
                    }
                }
            })
        }
    }
}
