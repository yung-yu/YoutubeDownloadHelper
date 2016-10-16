package youtubedownloadhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by andyli on 2016/8/27.
 */
public class LiveVideoManager {

    private static LiveVideoManager instance;

    public static LiveVideoManager getInstance(Context context) {
        if (instance == null) {
            instance = new LiveVideoManager(context);
        }
        return instance;
    }

    private Context context;
    private WebView webView;
    private WindowManager mWindowManager;

    static String videoId = "";

    public static String getVideoId() {
        return videoId;
    }

    public static void setVideoId(String videoId) {
        LiveVideoManager.videoId = videoId;
    }

    public LiveVideoManager(Context context) {
        this.context = context;

    }
    boolean touchconsumedbyMove = false;
    boolean touchconsumedbyZoom = false;
    int recButtonLastX;
    int recButtonLastY;
    int recButtonFirstX;
    int recButtonFirstY;
    WindowManager.LayoutParams  prms;
    View view;

    View.OnTouchListener OnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams prm = prms;
            if(prm == null){
                return true;
            }
            int totalDeltaX = recButtonLastX - recButtonFirstX;
            int totalDeltaY = recButtonLastY - recButtonFirstY;

            switch(event.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                    recButtonLastX = (int) event.getRawX();
                    recButtonLastY = (int) event.getRawY();
                    recButtonFirstX = recButtonLastX;
                    recButtonFirstY = recButtonLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int deltaX = (int) event.getRawX() - recButtonLastX;
                    int deltaY = (int) event.getRawY() - recButtonLastY;
                    recButtonLastX = (int) event.getRawX();
                    recButtonLastY = (int) event.getRawY();
                    if (Math.abs(totalDeltaX) >= 5  || Math.abs(totalDeltaY) >= 5) {
                        if (event.getPointerCount() == 1) {
                            prm.x += deltaX;
                            prm.y += deltaY;
                            touchconsumedbyMove = true;
                            mWindowManager.updateViewLayout(view, prm);
                        }
                        else{
                            touchconsumedbyMove = false;
                        }
                    }else{
                        touchconsumedbyMove = false;
                    }
                    break;
                default:
                    break;
            }
            return touchconsumedbyMove;
        }
    };
    View.OnTouchListener OnZoomListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            WindowManager.LayoutParams prm = prms;
            if(prm == null){
                return true;
            }

            switch(event.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int[] point = new int[2];
                    view.getLocationOnScreen(point);
                    int width = (int) event.getRawX()-point[0];
                    int height = (int) event.getRawY()-point[1];

                    if (width >= context.getResources().getDisplayMetrics().widthPixels/5 && width < context.getResources().getDisplayMetrics().widthPixels
                            && height >= context.getResources().getDisplayMetrics().heightPixels/5 && height < context.getResources().getDisplayMetrics().heightPixels) {
                        if (event.getPointerCount() == 1) {
                            prm.width = width;
                            prm.height = height;
                            touchconsumedbyZoom = true;
                            mWindowManager.updateViewLayout(view, prm);
                        }
                        else{
                            touchconsumedbyZoom = false;
                        }
                    }else{
                        touchconsumedbyZoom = false;
                    }
                    break;
                default:
                    break;
            }
            return touchconsumedbyZoom;
        }
    };
    public void open(String videoId){
        if(view != null || TextUtils.isEmpty(videoId))
            return;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        prms = new WindowManager.LayoutParams();
        prms.format = PixelFormat.TRANSLUCENT;
        prms.flags = WindowManager.LayoutParams.FORMAT_CHANGED;
        prms.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        prms.gravity = Gravity.CENTER |Gravity.TOP;
        prms.width =  context.getResources().getDisplayMetrics().widthPixels/3;
        prms.height =  context.getResources().getDisplayMetrics().heightPixels/3;
        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         view =LayoutInflater.inflate(R.layout.window_live_player, null);
        mWindowManager.addView(view, prms);
        Button move = (Button)view.findViewById(R.id.move);
        Button close = (Button)view.findViewById(R.id.close);
        Button small = (Button)view.findViewById(R.id.small);
        Button zoom = (Button)view.findViewById(R.id.zoom);
        zoom.setOnTouchListener(OnZoomListener);
        small.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(webView.getVisibility() == View.VISIBLE){
                    webView.setVisibility(View.GONE);
                }else{
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                context.stopService(new Intent(context, LiveService.class));
            }
        });
        move.setOnTouchListener(OnTouchListener);
        webView = (WebView) view.findViewById(R.id.webview);
        webView.setPadding(0, 0, 0, 0);
        webView.setBackgroundColor(Color.TRANSPARENT);
        WebSettings webSettings = webView.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());

        String html = "";
        html += "<html><body style=\"body ,iframe {\n" +
                "   margin: 0;\n" +
                "   padding: 0;\n" +
                "\"}>";
        html += "<iframe width=\"560\" height=\"315\"  src=\"https://www.youtube.com/embed/"+videoId+"\" frameborder=\"0\" allowfullscreen></iframe>";
        html += "</body></html>";
        webView.loadData(html, "text/html", "utf-8");


    }

    public void close(){
        if(mWindowManager != null && view != null){
            mWindowManager.removeView(view);
            if(webView != null){
                webView.destroy();
            }
            view = null;
        }
    }

}