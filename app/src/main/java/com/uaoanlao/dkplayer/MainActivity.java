package com.uaoanlao.dkplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.uaoanlao.tv.Screen;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videocontroller.UaoanVideoPlayer;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.ijk.IjkPlayerFactory;
import xyz.doikki.videoplayer.player.BaseVideoView;
import xyz.doikki.videoplayer.player.ProgressManager;
import xyz.doikki.videoplayer.player.VideoView;

public class MainActivity extends AppCompatActivity {
    private UaoanVideoPlayer videoView;
    private String URL="https://s.xlzys.com/play/9avDmPgd/index.m3u8";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView=findViewById(R.id.player);
        videoView.setPlayerFactory(ExoMediaPlayerFactory.create());  //使用ijk内核
        videoView.setUrl(URL); //设置视频地址
        StandardVideoController controller = new StandardVideoController(this);
        controller.addDefaultControlComponent("斗破苍穹", false);
        videoView.setVideoController(controller); //设置控制器
        videoView.start(); //开始播放，不调用则不自动播放
        videoView.setOnUpSetClickListener(new UaoanVideoPlayer.OnUpSetClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "上一集", Toast.LENGTH_SHORT).show();
            }
        });
        videoView.setOnDownSetClickListener(new UaoanVideoPlayer.OnDownSetClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "下一集", Toast.LENGTH_SHORT).show();
            }
        });
        videoView.setOnSelectClickListener(new UaoanVideoPlayer.OnSelectClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "选集", Toast.LENGTH_SHORT).show();
            }
        });
        videoView.setOnWindowClickListener(new UaoanVideoPlayer.OnWindowClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "小窗", Toast.LENGTH_SHORT).show();
            }
        });
        videoView.setOnScreenClickListener(new UaoanVideoPlayer.OnScreenClickListener() {
            @Override
            public void onClick(View view) {
                //投屏
                /*new Screen().setStaerActivity(MainActivity.this)
                        .setName("斗破苍穹")
                        .setUrl("https://s.xlzys.com/play/9avDmPgd/index.m3u8")
                        .setImageUrl("http://i0.hdslb.com/bfs/article/96fa4320db5115711c8c30afaff936910595d336.png")
                        .show();*/
            }
        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        //暂停播放
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //继续播放
        videoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放播放器
        videoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!videoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}