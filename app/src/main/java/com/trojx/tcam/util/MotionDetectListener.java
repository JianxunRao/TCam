package com.trojx.tcam.util;

import org.opencv.core.Mat;

/**
 * Created by Trojx on 2016/6/7 0007.
 */
public interface MotionDetectListener {
    void  onMotionDetected(Mat foreground, int diff);
    void onMotionNotDetected(Mat foreground,int diff);
}
