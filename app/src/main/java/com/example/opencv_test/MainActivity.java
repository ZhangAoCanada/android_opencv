package com.example.opencv_test;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.icu.util.Output;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.os.Bundle;
import android.widget.FrameLayout;

import org.opencv.android.JavaCameraView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.*;

public class MainActivity extends Activity implements CvCameraViewListener2 {

    // Used for logging success or failure messages
    private static final String TAG = "OCVSample::Activity";

    // Loads camera view of OpenCV for us to use. This lets us see using OpenCV
    private CameraBridgeViewBase mOpenCvCameraView;
    private ImgProcExample imgPorcExp = new ImgProcExample();

    // Used in Camera selection from menu (when implemented)
    private boolean              mIsJavaCamera = true;
    private MenuItem             mItemSwitchCamera = null;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat current_frame;
    Mat previous_frame;
    Mat previous_frame1;
    Mat previous_frame2;
    Mat previous_frame3;
    Mat display_debug;
    Size scale_size;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public MainActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {

        current_frame = new Mat(height, width, CvType.CV_8UC4);
        previous_frame = new Mat(height, width, CvType.CV_8UC4);
        previous_frame1 = new Mat(height, width, CvType.CV_8UC4);
        previous_frame2 = new Mat(height, width, CvType.CV_8UC4);
        previous_frame3 = new Mat(height, width, CvType.CV_8UC4);
        display_debug = new Mat(height, width, CvType.CV_8UC4);

        scale_size = new Size(width/2, height);
    }

    public void onCameraViewStopped() {
        current_frame.release();
        previous_frame.release();
        previous_frame1.release();
        previous_frame2.release();
        previous_frame3.release();
    }

    /**
     * Change things or add functions here to see what you can do.
     */
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        //declaration
        Mat grayImg = new Mat();
        Mat sobel_raw = new Mat();
        int x_order = 1;
        int y_order = 0;
        // get the current frame using opencv
        current_frame = inputFrame.rgba();

       /** get current frame and previous frame, prepare for passing into func **/
//        if (previous_frame.empty()) {
//            display_debug = current_frame.clone();
//        }
//        else {
//            Mat current_resize  = new Mat();
//            Mat previous_resize  = new Mat();
//
//            List<Mat> src;
//            Imgproc.resize(current_frame, current_resize, scale_size);
//            Imgproc.resize(previous_frame, previous_resize, scale_size);
//            src = Arrays.asList(previous_resize, current_resize);
//            Core.hconcat(src, display_debug);
//            current_resize.release();
//            previous_resize.release();
//        }
//        previous_frame2.copyTo(previous_frame);
////        previous_frame2.copyTo(previous_frame3);
//        previous_frame1.copyTo(previous_frame2);
//        current_frame.copyTo(previous_frame1);

        /** try Imgproc **/
        Imgproc.cvtColor(current_frame, grayImg, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Sobel(grayImg, sobel_raw, CvType.CV_16S, x_order, y_order);
        Core.convertScaleAbs(sobel_raw, display_debug);
//        Core.normalize(display_debug, display_debug, 0, 255, Core.NORM_MINMAX);

        String test = Core.minMaxLoc(sobel_raw).toString();

        grayImg.release();
        sobel_raw.release();

        return display_debug; // This function must return
    }
}
