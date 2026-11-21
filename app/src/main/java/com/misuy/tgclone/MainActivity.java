package com.misuy.tgclone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.webkit.*;

public class MainActivity extends Activity {
    WebView w;
    ValueCallback<Uri[]> cb;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(0xFF17212B);
        getWindow().setNavigationBarColor(0xFF17212B);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        w = new WebView(this);
        setContentView(w);
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        w.addJavascriptInterface(new Object() {
            @JavascriptInterface public void bar(final int c) {
                runOnUiThread(() -> { getWindow().setStatusBarColor(c); getWindow().setNavigationBarColor(c); });
            }
        }, "Android");
        w.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> c, FileChooserParams p) {
                if (cb != null) cb.onReceiveValue(null);
                cb = c;
                try { startActivityForResult(p.createIntent(), 1); } catch (Exception e) { cb = null; return false; }
                return true;
            }
        });
        w.loadUrl("file:///android_asset/index.html");
    }

    @Override protected void onActivityResult(int r, int res, Intent d) {
        if (r == 1 && cb != null) { cb.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(res, d)); cb = null; }
    }

    @Override public void onBackPressed() {
        w.evaluateJavascript("window.tgBack?window.tgBack():false", v -> { if (!"true".equals(v)) finish(); });
    }
}
