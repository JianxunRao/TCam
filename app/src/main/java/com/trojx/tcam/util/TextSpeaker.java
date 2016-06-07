package com.trojx.tcam.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.PriorityQueue;

/**利用百度语音REST API 实现TTS
 * Created by Trojx on 2016/6/7 0007.
 */
public class TextSpeaker {
    private Context context;
    private PriorityQueue<String> textQueue;
    private String token;
    private boolean isInitialized=false;
    private static final String TAG="TextSpeaker";
    private final MediaPlayer player;

    public TextSpeaker(Context context){
        this.context=context;
        textQueue=new PriorityQueue<>();
        player = new MediaPlayer();

    }

    /**
     * 异步初始化 获取token
     */
    public void initAsync(final String AppId, final String AppKey){

        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuilder urlAddress=new StringBuilder();
                urlAddress.append("https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id=");
                urlAddress.append(AppId);
                urlAddress.append("&");
                urlAddress.append("client_secret=");
                urlAddress.append(AppKey);
                try {
                    URL url=new URL(urlAddress.toString());
                    InputStream in=url.openStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(in));
                    String response=br.readLine();
                    System.out.println(response);
                    JSONObject jsonObject= JSON.parseObject(response);
                    token=jsonObject.getString("access_token");
                    if(!token.isEmpty()){
                        isInitialized=true;
                        Log.i(TAG,"token:"+token);
                        looper.start();//开启轮询
                    }
                } catch (java.io.IOException e) {
                    Log.e(TAG,e.toString());
                }
            }
        });
        t.start();
    }

    /**
     * 提交需要发音的文字
     * @param textToSpeech 需要发音的字符串
     */
    public void postText(String textToSpeech){
        textQueue.add(textToSpeech);
    }

    Thread looper=new Thread(new Runnable() {
        @Override
        public void run() {
            if(isInitialized){
                while (true){
                    if(!textQueue.isEmpty()){
                        String text=textQueue.peek();
                        speechText(text);
                        try {
                            Thread.sleep(500);//轮询间隔，不sleep容易error
                        } catch (InterruptedException e) {
                            Log.e(TAG,"looper"+e.toString());
                        }
                    }
                }
            }
        }
    });

    private void speechText(String text){

        if(player.isPlaying())
            return;

        StringBuilder url=new StringBuilder();
        url.append("http://tsn.baidu.com/text2audio?tex=");
        url.append(text);
        url.append("&lan=zh&cuid=E8-4E-06-35-03-D5&ctp=1&per=0&spd=3&tok=");
        url.append(token);
        Log.d(TAG,"url:"+url.toString());
        try {
            if(!player.isPlaying()){
                player.setDataSource(context, Uri.parse(url.toString()));
                try {
                    player.prepare();
                } catch (IOException e) {
                    Log.e(TAG,e.toString());
                }
                player.start();
            }
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                textQueue.poll();
                player.reset();
            }
        });
    }
}
