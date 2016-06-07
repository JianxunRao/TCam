package com.trojx.tcam.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.trojx.tcam.R;
import com.trojx.tcam.util.MotionDetectListener;
import com.trojx.tcam.util.MotionDetector;
import com.trojx.tcam.util.TextSpeaker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**测试openCV
 * Created by Trojx on 2016/6/6 0006.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView iv;
//    private MediaPlayer player;
    private TextSpeaker textSpeaker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.iv);
//        player=MediaPlayer.create(this,Uri.parse("http://120.26.202.43/alert.mp3"));

        textSpeaker = new TextSpeaker(this);
        textSpeaker.initAsync("DZTAGrcWPT46xnhcldOifDL4","f819e72022bebacee4534a4508179ca1");







    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.i("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        } else {
            Log.i("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);

            downLoadPicThread.start();
            detectThread.start();
            downLoadPicThread.setPriority(Thread.MAX_PRIORITY);
            detectThread.setPriority(Thread.MAX_PRIORITY);

        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    private void processGray() {
        Mat rgbMat=new Mat();
        Mat grayMat=new Mat();
        Bitmap rgbBitmap= BitmapFactory.decodeResource(getResources(),R.drawable.lena);
        Bitmap grayBitmap=Bitmap.createBitmap(rgbBitmap.getWidth(),rgbBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(rgbBitmap,rgbMat);
        Imgproc.cvtColor(rgbMat,grayMat,Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(grayMat,grayBitmap);
        iv.setImageBitmap(grayBitmap);
    }

    //移动侦测
    private void processDiff(){
//        Mat mat1=new Mat();
//        Mat mat2=new Mat();
//        Bitmap bitmap1=BitmapFactory.decodeFile("sdcard/pic_compare/cam_3.jpg");
//        Bitmap bitmap2=BitmapFactory.decodeFile("sdcard/pic_compare/cam_4.jpg");
//        Utils.bitmapToMat(bitmap1,mat1);
//        Utils.bitmapToMat(bitmap2,mat2);
//        Mat foreground = new Mat();
//        Core.absdiff(mat1,mat2, foreground);
//        Bitmap foreBitmap=Bitmap.createBitmap(bitmap1.getWidth(),bitmap1.getHeight(), Bitmap.Config.RGB_565);
//        Utils.matToBitmap(foreground,foreBitmap);
//        iv.setImageBitmap(foreBitmap);
//        Log.e("width=&height", foreground.width()+","+ foreground.height());
//        Log.e("get", foreground.get(1,1)[1]+"");
        MotionDetector detector=new MotionDetector();
        detector.setMotionDetectListener(new MotionDetectListener() {
            @Override
            public void onMotionDetected(Mat foreground, int diff) {
                Log.e("diff",diff+"");
//                player.start();
                Date date=new Date();
                SimpleDateFormat sdf=new SimpleDateFormat("hh:mm:ss");
//                textSpeaker.postText(sdf.format(date)+",有人闯入，快来抓贼啊");
                textSpeaker.postText("包伟，抓贼！");

            }

            @Override
            public void onMotionNotDetected(Mat foreground,int diff) {
                Log.i("diff","no diff:"+diff);


            }
        });
//        detector.compare("sdcard/pic_compare/cam_3.jpg","sdcard/pic_compare/cam_5.jpg");

        File file1=new File("sdcard/tcam/1_loaded.jpg");
        File file2=new File("sdcard/tcam/2_loaded.jpg");
        if(file1.exists()&&file2.exists()){
            detector.compare("sdcard/tcam/1_loaded.jpg","sdcard/tcam/2_loaded.jpg");
        }

    }

    Thread downLoadPicThread=new Thread(new Runnable() {
        @Override
        public void run() {
            int i=1;
            while(true){
                try {
                    URL url=new URL("http://192.168.31.55/tmpfs/auto.jpg?1464934461434");

                    URLConnection connection=url.openConnection();
                    connection.setRequestProperty("Authorization","Basic YWRtaW46YWRtaW4=");
                    InputStream in=connection.getInputStream();
                    File file=new File("sdcard/tcam/"+i+".jpg");
                    FileOutputStream fos=new FileOutputStream(file);
                    int len;
                    byte[] buff=new byte[1024];
                    while((len=in.read(buff))!=-1){
                        fos.write(buff,0,len);
                        fos.flush();
                    }
                    file.renameTo(new File("sdcard/tcam/"+i+"_loaded.jpg"));
                    if(i==1){
                        i=2;
                    }else {
                        i=1;
                    }
//                    try {
//                        Thread.sleep(500);//两张图片的抓拍间隔
//                    } catch (InterruptedException e) {
//                        Log.e("downLoadPicThread",e.toString());
//                    }
                } catch (java.io.IOException e) {
                    Log.e("downLoadPicThread",e.toString());
                }
            }
        }
    });

    Thread detectThread=new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                processDiff();
            }
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        downLoadPicThread.stop();
        detectThread.stop();
    }
}
