package xyz.doikki.videocontroller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import xyz.doikki.videocontroller.MMKV.UaoanMMKV;
import xyz.doikki.videocontroller.component.TitleView;
import xyz.doikki.videocontroller.component.VodControlView;
import xyz.doikki.videoplayer.player.BaseVideoView;
import xyz.doikki.videoplayer.player.ProgressManager;
import xyz.doikki.videoplayer.player.VideoView;

/**
 * TODO: document your custom view class.
 */
public class UaoanVideoPlayer extends VideoView {

    private String speed_text="1.0x";
    private String screenScale_text="默认";
    public static float speed_play=1.0f;
    private float speed_long =3.0f; //长按倍速速度
    private String speed_long_text ="3.0x"; //长按倍速文本
    private String timing_text="不启用";  //定时
    private CountDownTimer countDownTimer; //倒计时定时关闭
    private int COLOR=Color.RED; //主颜色
    private UaoanMMKV mmkv=new UaoanMMKV();
    public UaoanVideoPlayer(Context context) {
        super(context);
        init(null, 0);
    }

    public UaoanVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public UaoanVideoPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes

        mmkv.init(getContext()); //初始化MMKV
        //判断如果没有设置长按倍速就默认3.0x
        if (!mmkv.isContainsMMKVKey("speed")){
            mmkv.setMMKV("speed",3);
            mmkv.setMMKV("speed_text",speed_long_text);
        }else {
            speed_long=mmkv.getMMKVFloat("speed");
            speed_long_text=mmkv.getMMKVString("speed_text");
        }
        //判断是否有设置静音
        if (!mmkv.isContainsMMKVKey("jy")){
            mmkv.setMMKV("jy",false);
        }else {
            setMute(mmkv.getMMKVBoolean("jy"));
        }
        //判断是否设置了跳过片头
        if (!mmkv.isContainsMMKVKey("pt")){
            mmkv.setMMKV("pt_jd",0);
            mmkv.setMMKV("pt","00:00:00");
        }
        //判断是否设置了跳过片尾
        if (!mmkv.isContainsMMKVKey("pw")){
            mmkv.setMMKV("pw_jd",0);
            mmkv.setMMKV("pw","00:00:00");
        }



        //实时监听播放
        VodControlView.setOnProgressListener(new VodControlView.OnProgressListener() {
            @Override
            public void onProgress() {
                //判断播放进度是否到达跳过片尾的进度
                long sc=getDuration() - getCurrentPosition(); //当前播放位置
                long pw=convertTimeToMilliseconds(mmkv.getMMKVString("pw")); //片尾跳过位置
                if (sc==pw || sc<pw){
                    seekTo(getDuration());
                }




            }
        });

        //播放状态监听
        addOnStateChangeListener(new BaseVideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {
                if (playerState==11){
                    //全屏显示底部按钮
                    VodControlView.setVisibilitys(View.VISIBLE);
                    VodControlView.setVerticalVisibility(GONE);
                }
                if (playerState==10){
                    //竖屏隐藏底部按钮
                    VodControlView.setVisibilitys(View.GONE);
                    VodControlView.setVerticalVisibility(VISIBLE);
                }



            }

            @Override
            public void onPlayStateChanged(int playState) {
                //StandardVideoController.setTextTcpSpeed("正在缓冲："+getTcpSpeed());
                //播放完毕
                if (playState==STATE_PLAYBACK_COMPLETED){
                    if (timing_text.equals("播完当前")){
                        getActivity().finish();
                    }else {
                        //处理播放完毕事件

                    }
                }
                //正在准备播放
                if (playState==STATE_PREPARING){
                    skipPositionWhenPlay((int) convertTimeToMilliseconds(mmkv.getMMKVString("pt"))); //跳过片头
                }
                //正在缓冲
                if (playState==STATE_BUFFERING){

                }

            }


        });


        //竖屏全屏
        VodControlView.setOnVerticalClickListener(new VodControlView.OnVerticalClickListener() {
            @Override
            public void onClick(View view) {
                startFullScreen();
            }
        });

        //小窗
        TitleView.setOnWindowClickListener(new TitleView.OnWindowClickListener() {
            @Override
            public void onClick(View view) {
                onwindow.onClick(view);
            }
        });
        //投屏
        TitleView.setOnScreenClickListener(new TitleView.OnScreenClickListener() {
            @Override
            public void onClick(View view) {
                onscreen.onClick(view);
            }
        });
        //设置
        TitleView.setOnSetClickListener(new TitleView.OnSetClickListener() {
            @Override
            public void onClick(View view) {


                View vws = View.inflate(getContext(), R.layout.view_layout, null);
                LinearLayout linearLayouts = vws.findViewById(R.id.line1);
                final AlertDialog tcs = new AlertDialog.Builder(getContext())
                        .setView(vws)
                        .show();
                tcs.getWindow().setBackgroundDrawable(new ColorDrawable());
                //setDialogSize(tcs, 550); //设置弹窗大小
                ArrayList<String> aar_menu = new ArrayList<>();
                aar_menu.add("画面比例");
                aar_menu.add("播放设置");
                aar_menu.add("定时关闭");
                for (int ii=0;ii<aar_menu.size();ii++){
                    LinearLayout layout=new LinearLayout(getContext());
                    layout.setGravity(Gravity.CENTER);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setPadding(10,10,10,10);
                    WindowManager.LayoutParams layoutParams=new WindowManager.LayoutParams();
                    layoutParams.height= 250;
                    layoutParams.width= 300;
                    layout.setLayoutParams(layoutParams);


                    ImageView imageView=new ImageView(getContext());
                    WindowManager.LayoutParams layoutParam=new WindowManager.LayoutParams();
                    layoutParam.height= 100;
                    layoutParam.width= 100;
                    imageView.setPadding(5,5,5,5);
                    imageView.setLayoutParams(layoutParam);
                    if (aar_menu.get(ii).equals("画面比例")){
                        imageView.setImageResource(R.mipmap.bl);
                    }
                    if (aar_menu.get(ii).equals("播放设置")){
                        imageView.setImageResource(R.mipmap.playsetup);
                    }
                    if (aar_menu.get(ii).equals("定时关闭")){
                        imageView.setImageResource(R.mipmap.time);
                    }
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    final TextView wn = new TextView(getContext());
                    wn.setTextSize(16);
                    wn.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
                    wn.setTextColor(Color.WHITE);
                    wn.setGravity(Gravity.CENTER);
                    wn.setText(aar_menu.get(ii));
                    wn.setPadding(5,5,5,5);

                    layout.addView(imageView);
                    layout.addView(wn);
                    linearLayouts.addView(layout);

                    //菜单按钮点击事件
                    int id = ii;
                    layout.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tcs.dismiss();
                            if (id ==0){
                                //画面比例

                                View vw = View.inflate(getContext(), R.layout.view_layout, null);
                                LinearLayout linearLayout = vw.findViewById(R.id.line1);
                                AlertDialog tc = new AlertDialog.Builder(getContext())
                                        .setView(vw)
                                        .show();
                                tc.getWindow().setBackgroundDrawable(new ColorDrawable());
                                //setDialogSize(tc, 460); //设置弹窗大小
                                ArrayList<String> aar_screenScale = new ArrayList<>();
                                aar_screenScale.add("16:9");
                                aar_screenScale.add("4:3");
                                aar_screenScale.add("默认");
                                aar_screenScale.add("填充");
                                aar_screenScale.add("缩放");
                                aar_screenScale.add("裁剪");
                                for (int i = 0; i < aar_screenScale.size(); i++) {
                                    final TextView wn = new TextView(getContext());
                                    if (aar_screenScale.get(i).equals(screenScale_text)) {
                                        wn.setTextColor(COLOR);
                                    } else {
                                        wn.setTextColor(Color.WHITE);
                                    }
                                    wn.setTextSize(22);
                                    wn.setWidth(220);
                                    wn.setMinWidth(180);
                                    wn.setText(aar_screenScale.get(i));
                                    linearLayout.addView(wn);
                                    wn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String getText=wn.getText().toString();
                                            if (getText.equals("16:9")){
                                                setScreenScaleType(BaseVideoView.SCREEN_SCALE_16_9);
                                            }
                                            if (getText.equals("4:3")){
                                                setScreenScaleType(BaseVideoView.SCREEN_SCALE_4_3);
                                            }
                                            if (getText.equals("默认")){
                                                setScreenScaleType(BaseVideoView.SCREEN_SCALE_DEFAULT);
                                            }
                                            if (getText.equals("填充")){
                                                setScreenScaleType(BaseVideoView.SCREEN_SCALE_MATCH_PARENT);
                                            }
                                            if (getText.equals("缩放")){
                                                setScreenScaleType(BaseVideoView.SCREEN_SCALE_ORIGINAL);
                                            }
                                            if (getText.equals("裁剪")){
                                                setScreenScaleType(BaseVideoView.SCREEN_SCALE_CENTER_CROP);
                                            }

                                            screenScale_text=getText;
                                            tc.dismiss();
                                        }
                                    });
                                }
                                //////////

                            }

                            if (id==1){
                                //播放设置

                                View vw = View.inflate(getContext(), R.layout.playset_layout, null);
                                AlertDialog tc = new AlertDialog.Builder(getContext())
                                        .setView(vw)
                                        .show();
                                tc.getWindow().setBackgroundDrawable(new ColorDrawable());
                                setDialogSize(tc, 350); //设置弹窗大小
                                final LinearLayout speed_layout=vw.findViewById(R.id.speed_layout); //长按倍速布局
                                final SeekBar speed_progress=vw.findViewById(R.id.progress_speed); //长按倍速拖动条
                                final TextView speed_text=vw.findViewById(R.id.speed_text); //长按倍速文本
                                final Switch yyjs_Switch=vw.findViewById(R.id.yjjs_switch); //设置静音开关
                                final SeekBar tgpt_progress=vw.findViewById(R.id.progress); //跳过片头拖动条
                                final TextView tgpt_text=vw.findViewById(R.id.tg_text); //跳过片头文本
                                final SeekBar tgpw_progress=vw.findViewById(R.id.progress1); //跳过片尾拖动条
                                final TextView tgpw_text=vw.findViewById(R.id.tg1_text); //跳过片尾文本

                                //长按倍速
                                int f= (int) mmkv.getMMKVFloat("speed");
                                speed_progress.setProgress(f);
                                speed_text.setText(mmkv.getMMKVString("speed_text"));
                                speed_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                                        int po=progress;
                                        if (progress==0){
                                            po=1;
                                        }
                                        float f= Float.parseFloat(po+"f");
                                        mmkv.setMMKV("speed",po);
                                        mmkv.setMMKV("speed_text",f+"x");
                                        speed_long=f;
                                        speed_long_text=f+"x";
                                        speed_text.setText(f+"x");
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });

                                //设置静音
                                yyjs_Switch.setChecked(mmkv.getMMKVBoolean("jy"));
                                yyjs_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (isChecked){
                                            //开启
                                            mmkv.setMMKV("jy",true);
                                            setMute(true);
                                        }else {
                                            //关闭
                                            mmkv.setMMKV("jy",false);
                                            setMute(false);
                                        }
                                    }
                                });

                                //跳过片头
                                tgpt_text.setText(mmkv.getMMKVString("pt"));
                                tgpt_progress.setProgress(mmkv.getMMKVInt("pt_jd"));
                                tgpt_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                                        String l=progress+"";
                                        l=l.replace(l.substring(0,1),l.substring(0,1)+":");
                                        l="00:0"+l+"0";
                                        tgpt_text.setText(l+"");
                                        mmkv.setMMKV("pt",l);
                                        mmkv.setMMKV("pt_jd",progress);
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });

                                //跳过片尾
                                tgpw_text.setText(mmkv.getMMKVString("pw"));
                                tgpw_progress.setProgress(mmkv.getMMKVInt("pw_jd"));
                                tgpw_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                        // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数progress，即当前滑块代表的进度值
                                        String l=progress+"";
                                        l=l.replace(l.substring(0,1),l.substring(0,1)+":");
                                        l="00:0"+l+"0";
                                        tgpw_text.setText(l+"");
                                        mmkv.setMMKV("pw",l);
                                        mmkv.setMMKV("pw_jd",progress);
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });

                                //////////////
                            }

                            if (id==2){
                                //定时关闭


                                View vw = View.inflate(getContext(), R.layout.view_layout, null);
                                LinearLayout linearLayout = vw.findViewById(R.id.line1);
                                AlertDialog tc = new AlertDialog.Builder(getContext())
                                        .setView(vw)
                                        .show();
                                tc.getWindow().setBackgroundDrawable(new ColorDrawable());
                                //setDialogSize(tc, 550); //设置弹窗大小
                                ArrayList<String> aar_timing = new ArrayList<>();
                                aar_timing.add("不启用");
                                aar_timing.add("播完当前");
                                aar_timing.add("30分钟");
                                aar_timing.add("60分钟");
                                for (int i = 0; i < aar_timing.size(); i++) {
                                    final TextView wn = new TextView(getContext());
                                    if (aar_timing.get(i).equals(timing_text)) {
                                        wn.setTextColor(COLOR);
                                    } else {
                                        wn.setTextColor(Color.WHITE);
                                    }
                                    wn.setTextSize(20);
                                    wn.setText(aar_timing.get(i));
                                    linearLayout.addView(wn);
                                    wn.setPadding(15,15,15,15);

                                    int id = i;

                                    wn.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (id ==0){
                                                timing_text="不启用";
                                                if (timing_text.contains("分钟")){
                                                    countDownTimer.cancel();
                                                }
                                            }
                                            if (id ==1){
                                                timing_text="播完当前";
                                                if (timing_text.contains("分钟")){
                                                    countDownTimer.cancel();
                                                }
                                            }
                                            if (id ==2){
                                                if (timing_text.contains("分钟")){
                                                    countDownTimer.cancel();
                                                }else {
                                                    countDownTimer = new CountDownTimer(1800000, 1000) {
                                                        @Override
                                                        public void onTick(long millisUntilFinished) {

                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            if (!timing_text.equals("不启用")) {
                                                                if (timing_text.equals("30分钟")) {
                                                                    getActivity().finish();
                                                                }
                                                            }
                                                        }
                                                    }.start();
                                                }
                                                timing_text="30分钟";
                                            }
                                            if (id ==3){
                                                if (timing_text.contains("分钟")){
                                                    countDownTimer.cancel();
                                                }else {
                                                    countDownTimer = new CountDownTimer(3600000, 1000) {
                                                        @Override
                                                        public void onTick(long millisUntilFinished) {

                                                        }

                                                        @Override
                                                        public void onFinish() {
                                                            if (!timing_text.equals("不启用")) {
                                                                if (timing_text.equals("60分钟")) {
                                                                    getActivity().finish();
                                                                }
                                                            }
                                                        }
                                                    }.start();
                                                }

                                                timing_text="60分钟";
                                            }
                                            tc.dismiss();
                                        }
                                    });
                                }
                                /////////
                            }

                        }
                    });

                }


            }
        });
        //选集
        VodControlView.setOnSelectClickListener(new VodControlView.OnSelectClickListener() {
            @Override
            public void onClick(View view) {
                onselect.onClick(view);
            }
        });
        //倍速
        VodControlView.setOnSpeedClickListener(new VodControlView.OnSpeedClickListener() {
            @Override
            public void onClick(View view) {
                View vw=View.inflate(getContext(),R.layout.view_layout,null);
                HorizontalScrollView scrollView=new HorizontalScrollView(getContext());
                ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(-1,-1);
                scrollView.setLayoutParams(layoutParams);
                scrollView.setHorizontalScrollBarEnabled(false);
                LinearLayout linear=new LinearLayout(getContext());
                linear.setLayoutParams(layoutParams);
                linear.setGravity(Gravity.CENTER);
                linear.setOrientation(LinearLayout.HORIZONTAL);
                scrollView.addView(linear);
                LinearLayout linearLayout=vw.findViewById(R.id.line1);
                linearLayout.addView(scrollView);
                AlertDialog tc=new AlertDialog.Builder(getContext())
                        .setView(vw)
                        .show();
                tc.getWindow().setBackgroundDrawable(new ColorDrawable());
                setDialogSize(tc, 550); //设置弹窗大小

                ArrayList<String> aar_speed=new ArrayList<>();
                aar_speed.add("0.75x");
                aar_speed.add("1.0x");
                aar_speed.add("1.25x");
                aar_speed.add("1.5x");
                aar_speed.add("1.75x");
                aar_speed.add("2.0x");
                aar_speed.add("2.5x");
                aar_speed.add("3.0x");
                aar_speed.add("3.5x");
                aar_speed.add("4.0x");
                aar_speed.add("5.0x");
                for (int i=0;i<aar_speed.size();i++){
                    final TextView wn=new TextView(getContext());
                    if (aar_speed.get(i).equals(speed_text)){
                        wn.setTextColor(COLOR);
                    }else {
                        wn.setTextColor(Color.WHITE);
                    }
                    wn.setTextSize(22);
                    wn.setPadding(2,2,2,2);
                    wn.setWidth(220);
                    wn.setMinWidth(180);
                    wn.setText(aar_speed.get(i));
                    linear.addView(wn);
                    wn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String getSpeed=wn.getText().toString();
                            if (getSpeed.equals("0.75x")){
                                setSpeed(0.75f);
                                speed_play=0.75f;
                            }
                            if (getSpeed.equals("1.0x")){
                                setSpeed(1.0f);
                                speed_play=1.0f;
                            }
                            if (getSpeed.equals("1.25x")){
                                setSpeed(1.25f);
                                speed_play=1.25f;
                            }
                            if (getSpeed.equals("1.5x")){
                                setSpeed(1.5f);
                                speed_play=1.5f;
                            }
                            if (getSpeed.equals("1.75x")){
                                setSpeed(1.75f);
                                speed_play=1.75f;
                            }
                            if (getSpeed.equals("2.0x")){
                                setSpeed(2.0f);
                                speed_play=2.0f;
                            }
                            if (getSpeed.equals("2.5x")){
                                setSpeed(2.5f);
                                speed_play=2.5f;
                            }
                            if (getSpeed.equals("3.0x")){
                                setSpeed(3.0f);
                                speed_play=3.0f;
                            }
                            if (getSpeed.equals("3.5x")){
                                setSpeed(3.5f);
                                speed_play=3.5f;
                            }
                            if (getSpeed.equals("4.0x")){
                                setSpeed(4.0f);
                                speed_play=4.0f;
                            }
                            if (getSpeed.equals("5.0x")){
                                setSpeed(5.0f);
                                speed_play=5.0f;
                            }
                            speed_text=getSpeed;
                            if (speed_text.equals("1.0x")){
                                VodControlView.speed.setText("倍速");  //设置播放速度显示到倍速按钮
                            }else {
                                VodControlView.speed.setText(speed_text);  //设置播放速度显示到倍速按钮
                            }
                            tc.dismiss();
                        }
                    });
                }
            }
        });
        //上一集
        VodControlView.setOnUpSetClickListener(new VodControlView.OnUpSetClickListener() {
            @Override
            public void onClick(View view) {
                onupset.onClick(view);
            }
        });
        //下一集
        VodControlView.setOnDownSetClickListener(new VodControlView.OnDownSetClickListener() {
            @Override
            public void onClick(View view) {
                ondownset.onClick(view);
            }
        });


        //长按3.0倍速
        StandardVideoController.setOnSpeedListener(new StandardVideoController.OnSpeedListener() {
            @Override
            public void onSpeed() {
                setSpeed(speed_long);
                StandardVideoController.setVisibilitySpeed(View.VISIBLE);
                if (speed_long==speed_play){
                    StandardVideoController.setTextSpeed("已经是 "+speed_long_text+" 倍速");
                }else {
                    StandardVideoController.setTextSpeed(speed_long_text+"倍速中");
                }
            }
        });
        //松开取消倍速
        StandardVideoController.setOnCancelSpeedListener(new StandardVideoController.OnCancelSpeedListener() {
            @Override
            public void onCancelSpeed() {
                setSpeed(speed_play);
                StandardVideoController.setVisibilitySpeed(View.GONE);
            }
        });
    }

    //小窗接口
    public interface OnWindowClickListener{
        void onClick(View view);
    }
    private OnWindowClickListener onwindow;
    public void setOnWindowClickListener(OnWindowClickListener onWindowClickListener){
        onwindow=onWindowClickListener;
    }

    //投屏接口
    public interface OnScreenClickListener{
        void onClick(View view);
    }
    private OnScreenClickListener onscreen;
    public void setOnScreenClickListener(OnScreenClickListener onScreenClickListener){
        onscreen=onScreenClickListener;
    }


    //选集接口
    public interface OnSelectClickListener{
        void onClick(View view);
    }
    private OnSelectClickListener onselect;
    public void setOnSelectClickListener(OnSelectClickListener onSelectClickListener){
        onselect=onSelectClickListener;
    }
    

    //上一集接口
    public interface OnUpSetClickListener{
        void onClick(View view);
    }
    private OnUpSetClickListener onupset;
    public void setOnUpSetClickListener(OnUpSetClickListener onUpSetClickListener){
        onupset=onUpSetClickListener;
    }


    //下一集接口
    public interface OnDownSetClickListener{
        void onClick(View view);
    }
    private OnDownSetClickListener ondownset;
    public void setOnDownSetClickListener(OnDownSetClickListener onDownSetClickListener){
        ondownset=onDownSetClickListener;
    }

    //隐藏ui按钮
    public void setVisibilityBottom(int a,int b,int c,int d){
        //隐藏底部按钮控制  8=隐藏 0=显示
        VodControlView.xuanji.setVisibility(a); //选集
        VodControlView.speed.setVisibility(b);  //倍速
        VodControlView.zplay.setVisibility(c);  //上一集
        VodControlView.yplay.setVisibility(d);  //下一集
    }
    public void setVisibilityTop(int a,int b,int c){
        //隐藏顶部按钮控制  8=隐藏 0=显示
        TitleView.xc.setVisibility(a); //小窗
        TitleView.tp.setVisibility(b); //投屏
        TitleView.sz.setVisibility(c); //设置
    }

    //Dialog弹窗大小
    private void setDialogSize(AlertDialog dialog_tc,int size){
        if (size!=0){
            dialog_tc.getWindow().setLayout(dp2px(size), LinearLayout.LayoutParams.WRAP_CONTENT);
        }else{
            dialog_tc.getWindow().setLayout(dp2px(300), LinearLayout.LayoutParams.WRAP_CONTENT);
        }
    }

    private int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, this.getResources().getDisplayMetrics());
    }

    //转换毫秒
    public long convertTimeToMilliseconds(String time) {
        long milliseconds = 0;

        try {
            String[] tokens = time.split(":");
            int hours = Integer.parseInt(tokens[0]);
            int minutes = Integer.parseInt(tokens[1]);
            int seconds = Integer.parseInt(tokens[2]);

            // 将小时、分钟和秒转换为毫秒
            milliseconds = (hours * 60 * 60 * 1000) + (minutes * 60 * 1000) + (seconds * 1000);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return milliseconds;
    }
}