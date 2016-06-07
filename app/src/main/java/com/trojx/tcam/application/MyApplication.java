package com.trojx.tcam.application;

import android.app.Application;
import android.util.Log;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

/**MyApplication 一些初始化
 * Created by Trojx on 2016/6/7 0007.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

//        initOpenCV();

    }

    private void initOpenCV() {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, new LoaderCallbackInterface() {
                            @Override
                            public void onManagerConnected(int status) {
                                switch (status){
                                    case SUCCESS:
                                        Log.i("opcv","ManagerConnected.");
                                        break;
                                    default:
                                        Log.e("opcv","fail!");
                                        break;
                }
            }

            @Override
            public void onPackageInstall(int operation, InstallCallbackInterface callback) {

            }
        });
    }
}
