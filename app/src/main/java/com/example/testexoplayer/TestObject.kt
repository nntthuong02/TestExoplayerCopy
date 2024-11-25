package com.example.testexoplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testexoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.LoadControl
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

object TestObject {
    class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
        private lateinit var player: ExoPlayer

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Sử dụng View Binding
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Khởi tạo SimpleCache
            val cache = SimpleCache(
                File(applicationContext.cacheDir, "video_cache"), // Đường dẫn thư mục cache
                LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024)  // Dung lượng tối đa: 100 MB
            )

            val cacheDataSourceFactory = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

            // Cấu hình LoadControl để giới hạn thời gian tải trước (buffer)
            val loadControl: LoadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    5_000, // Bộ nhớ đệm tối thiểu (5 giây)
                    10_000, // Bộ nhớ đệm tối đa (10 giây)
                    1_000,  // Bộ nhớ đệm trước khi phát lại
                    2_000   // Bộ nhớ đệm sau khi phát lại
                )
                .build()

            // Khởi tạo ExoPlayer với LoadControl tùy chỉnh
            player = ExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build()

            // Liên kết PlayerView với player
            binding.playerView.player = player

            // Tải video từ đường dẫn .m3u8
            val mediaSource = HlsMediaSource.Factory(cacheDataSourceFactory)
                .createMediaSource(MediaItem.fromUri("https://dramabox.store/nquEby.m3u8"))
            player.setMediaSource(mediaSource)
            player.prepare()
            player.playWhenReady = true
        }

        override fun onResume() {
            super.onResume()
            player.prepare()
            player.playWhenReady = true
        }

        override fun onPause() {
            super.onPause()
            player.pause()
        }

        override fun onStop() {
            super.onStop()
            player.pause()
        }
    }
}