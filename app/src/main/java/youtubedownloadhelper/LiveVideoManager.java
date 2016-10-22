package youtubedownloadhelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;

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
    private ImageButton move ;
    private ImageButton close ;
    private ImageButton small ;
    private RelativeLayout tools;
    private Button zoom ;
    static String videoId = "";
    private static final int MIN_WIDTH = 450;
    private static final int MIN_HEIGHT = 360;
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
    private HashMap<String,View>  cache = new HashMap<>();

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
                    webView.onPause();
                    recButtonLastX = (int) event.getRawX();
                    recButtonLastY = (int) event.getRawY();
                    recButtonFirstX = recButtonLastX;
                    recButtonFirstY = recButtonLastY;
                    break;
                case MotionEvent.ACTION_UP:
                    webView.onResume();
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
            WindowManager.LayoutParams parm = prms;
            if(parm == null){
                return true;
            }

            switch(event.getActionMasked())
            {
                case MotionEvent.ACTION_DOWN:
                    webView.onPause();
                    break;
                case MotionEvent.ACTION_UP:
                    webView.onResume();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int[] point = new int[2];
                    view.getLocationOnScreen(point);
                    int width = (int) event.getRawX()-point[0];
                    int height = (int) event.getRawY()-point[1];

                    if (width >= MIN_WIDTH && height >= MIN_HEIGHT) {
                        if (event.getPointerCount() == 1) {
                            parm.width = width;
                            parm.height = height;
                            mWindowManager.updateViewLayout(view, parm);
                            touchconsumedbyZoom = true;
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

    private boolean isHidden = false;
    private int curHeight = 0;
    public void open(String videoId){
        if(TextUtils.isEmpty(videoId)){
            return;
        }
        if(view != null){
            close();
        }
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        prms = new WindowManager.LayoutParams();
        prms.format = PixelFormat.TRANSLUCENT;
        prms.flags = WindowManager.LayoutParams.FORMAT_CHANGED;
        prms.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        prms.gravity = Gravity.CENTER |Gravity.TOP;
        prms.width = MIN_WIDTH;
        prms.height = MIN_HEIGHT;
        LayoutInflater LayoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = LayoutInflater.inflate(R.layout.window_live_player, null);
        mWindowManager.addView(view, prms);
        move = (ImageButton)view.findViewById(R.id.move);
        close = (ImageButton)view.findViewById(R.id.close);
        small = (ImageButton)view.findViewById(R.id.small);
        tools = (RelativeLayout) view.findViewById(R.id.tools);
        zoom = (Button) view.findViewById(R.id.zoom);
        zoom.setOnTouchListener(OnZoomListener);
        small.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!isHidden){
                    isHidden = true;
                    webView.setVisibility(View.GONE);
                    zoom.setVisibility(View.GONE);
                    curHeight = prms.height;
                    prms.height = tools.getHeight()+20;
                    mWindowManager.updateViewLayout(view, prms);
                }else{
                    isHidden = false;
                    webView.setVisibility(View.VISIBLE);
                    zoom.setVisibility(View.VISIBLE);
                    prms.height = curHeight;
                    mWindowManager.updateViewLayout(view, prms);
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
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        WebSettings webSettings = webView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setNeedInitialFocus(true);
        webView.setWebChromeClient(new WebChromeClient());

        String html = "";
        html += "<html>" +
                "<body style=\"body ,iframe {\n" +
                "   margin: 0;\n" +
                "   padding: 0;\n" +
                "\"}>";
        html += "<iframe width=\"450\" height=\"360\"  src=\"https://www.youtube.com/embed/"+videoId+"\" frameborder=\"0\" allowfullscreen></iframe>";
        html += "</body></html>";
        webView.loadData(html, "text/html", "utf-8");
    }

    public void close(){
        if(mWindowManager != null && view != null){
            mWindowManager.removeView(view);
            if(webView != null){
                webView.clearCache(true);
                webView.destroy();
            }
            view = null;
        }
    }

}