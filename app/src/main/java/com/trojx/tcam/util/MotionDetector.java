package com.trojx.tcam.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**移动侦测者
 * Created by Trojx on 2016/6/7 0007.
 */
public class MotionDetector  {
    private MotionDetectListener motionDetectListener;
    private int diffThreshold=5;

    public void MotionDetector(){

    }

    /**
     * 设定全图差异值的阈值
     * @param diffThreshold 阈值
     */
    public void setDiffThreshold(int diffThreshold){
        this.diffThreshold=diffThreshold;
    }

    public void setMotionDetectListener(MotionDetectListener listener){
        this.motionDetectListener=listener;
    }

    public int compare(Mat mat1,Mat mat2){
        int diff=0;
        Mat matGray1=new Mat();
        Mat matGray2=new Mat();
        Imgproc.cvtColor(mat1,matGray1,Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(mat2,matGray2,Imgproc.COLOR_RGB2GRAY);

        Mat foreground=new Mat();
        Core.absdiff(matGray1,matGray2,foreground);

        foreground.cols();

        int diffPixels=comparePixels(foreground);
        diff=100*diffPixels/(mat1.width()*mat1.height());


        if(diff>=diffThreshold){
            motionDetectListener.onMotionDetected(foreground,diff);
            return diff;
        }else {
            motionDetectListener.onMotionNotDetected(foreground,diff);
            return diff;
        }

    }

    public int compare(Bitmap bitmap1,Bitmap bitmap2){
        Mat mat1=new Mat();
        Mat mat2=new Mat();
        Utils.bitmapToMat(bitmap1,mat1);
        Utils.bitmapToMat(bitmap2,mat2);

        return compare(mat1,mat2);
    }

    public int compare(String filePath1,String filePath2){
        Bitmap bitmap1= BitmapFactory.decodeFile(filePath1);
        Bitmap bitmap2= BitmapFactory.decodeFile(filePath2);

        return compare(bitmap1,bitmap2);
    }

    //比较每一个像素点与黑色的差异
    private int comparePixels(Mat foreground){
        int diffPixels=0;
        int rows=foreground.rows();
        int cols=foreground.cols();

        for(int row=0;row<rows;row++){
            for(int col=0;col<cols;col++){
                double[] argb=foreground.get(row,col);
                double sum=0;
                int i=0;
                for(double d:argb){
                    sum+=d;
//                    Log.v("argb",argb[0]+","+argb[1]+","+argb[2]+","+argb[3]);//越界
//                    Log.v("argb"+i++,d+"");//因为是黑白的所以只有一个通道！
                }
                if (sum>10)//10可能要改
                    diffPixels++;
//                int rate=100*diffPixels/(rows*cols);
//                if(rate>10)//如果已经大于阈值，直接返回 提升效率
//                    return diffPixels;
            }
        }
        return diffPixels;
    }
}