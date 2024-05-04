# DkPlayer
 DK播放器 1、UI增加(上一集 下一集 倍速 选集 设置 投屏 小窗)。 2、功能添加(倍速弹窗 画面比例弹窗 定时关闭 长按倍速 长按倍速修改 设置静音 跳过片头 跳过片尾 DLNA投屏 )

### [APK下载预览](https://github.com/uaoan/DkPlayer/raw/main/app/release/DK%E6%92%AD%E6%94%BE%E5%99%A8.apk)


### 集成
 **1.在 project 的 build.gradle 文件中找到 allprojects{} 代码块添加以下代码：** 

```
allprojects {
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }      //增加 jitPack Maven 仓库
    }
}
```
 **在 app 的 build.gradle 文件中找到 dependencies{} 代码块，并在其中加入以下语句：** 

```
implementation 'com.github.uaoan:DkPlayer:dkplayer-1.0'
implementation 'xyz.doikki.android.dkplayer:dkplayer-java:3.3.7'
implementation 'com.tencent:mmkv-static:1.3.0'
```

 **配合DLNA投屏使用**
[DLNA投屏库](https://github.com/uaoan/UaoanDLNA) 


 **添加权限**

```
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

###  **简单使用** 

 **添加布局** 

```
 <xyz.doikki.videocontroller.UaoanVideoPlayer
        android:id="@+id/player"
        android:layout_width="match_parent"
        android:layout_height="200dp" />
```

 **设置视频地址、控制器等** 

```
videoView.setUrl(URL); //设置视频地址
StandardVideoController controller = new StandardVideoController(this);
controller.addDefaultControlComponent("斗破苍穹", false);
videoView.setVideoController(controller); //设置控制器
videoView.start(); //开始播放，不调用则不自动播放
```

 **在Activity中** 


```
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
```

 **在AndroidManifest.xml中** 


```
<activity
    android:name=".PlayerActivity"
    android:configChanges="orientation|screenSize|keyboardHidden"
    android:screenOrientation="portrait" /> 
```

 **上一集按钮点击事件**


```
videoView.setOnUpSetClickListener(new UaoanVideoPlayer.OnUpSetClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "上一集", Toast.LENGTH_SHORT).show();
            }
        });
```

 **下一集按钮点击事件**


```
videoView.setOnDownSetClickListener(new UaoanVideoPlayer.OnDownSetClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "下一集", Toast.LENGTH_SHORT).show();
            }
        });
```

 **选集按钮点击事件**


```
videoView.setOnSelectClickListener(new UaoanVideoPlayer.OnSelectClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "选集", Toast.LENGTH_SHORT).show();
            }
        });
```


 **小窗按钮点击事件**



```
videoView.setOnWindowClickListener(new UaoanVideoPlayer.OnWindowClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "小窗", Toast.LENGTH_SHORT).show();
            }
        });
```


 **投屏按钮点击事件**


```
videoView.setOnScreenClickListener(new UaoanVideoPlayer.OnScreenClickListener() {
            @Override
            public void onClick(View view) {
                //投屏
              
            }
        });
```

 
 **隐藏底部UI按钮**


```
videoView.setVisibilityBottom();
```
| setVisibilityBottom(a，b，c，d) | 隐藏底部UI按钮 |
|------------------------------|----------|
| a                            | 选集按钮     |
| b                            | 倍速按钮     |
| c                            | 上一集按钮    |
| d                            | 下一集按钮    |


 **隐藏顶部UI按钮** 

```
videoView.setVisibilityTop();
```
| setVisibilityTop(a，b，c) | 隐藏顶部UI按钮 |
|-------------------------|----------|
| a                       | 小窗按钮     |
| b                       | 投屏按钮     |
| c                       | 设置按钮     |


### 投屏
 **投屏使用代码** 


```
new Screen().setStaerActivity(MainActivity.this)
            .setName("斗破苍穹") 
            .setUrl("https://s.xlzys.com/play/9avDmPgd/index.m3u8")
            .setImageUrl("http://i0.hdslb.com/bfs/article/96fa4320db5115711c8c30afaff936910595d336.png")
            .show();
```
 
 **————————————————————————————————————————————————————————————————————————** 


 ### [更多方法请点击前往 >](https://github.com/Doikki/DKVideoPlayer/wiki/API)


 **————————————————————————————————————————————————————————————————————————** 


![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165736.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165743.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165752.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165756.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165801.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165806.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165811.jpg)
![输入图片说明](%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20240203165816.jpg)
