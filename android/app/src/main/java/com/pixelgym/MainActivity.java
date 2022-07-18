package com.pixelgym;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends Activity {
    private WebView webView;
    private PermissionRequest mPermissionRequest;
    private WebViewAssetLoader assetLoader;
    private ContextThemeWrapper themedContext;
    public static Activity myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myContext = this;

        themedContext = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

        assetLoader = new WebViewAssetLoader.Builder().addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();

        webView = (WebView) findViewById(R.id.webView1);
        webView.setBackgroundColor(Color.rgb(0,0,0));
        webView.addJavascriptInterface(new JavaScriptShareInterface(), "AndroidShareHandler");

        webView.setWebViewClient(new WebViewClient()
            {
            @Override @RequiresApi(21) public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
                {
                return assetLoader.shouldInterceptRequest(request.getUrl());
                }

            @Override @SuppressWarnings("deprecation") public WebResourceResponse shouldInterceptRequest(WebView view, String request)
                {
                return assetLoader.shouldInterceptRequest(Uri.parse(request));
                }

            @Override public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request)
                {
                clickInPrivacy();
                return true;
                }
            });

        webView.setWebChromeClient(new WebChromeClient()
            {
            @Override public void onPermissionRequest(final PermissionRequest request)
                {
                mPermissionRequest = request;

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 123);
                    }
                    else
                    {
                    request.grant(request.getResources());
                    }
                }
            });

        WebSettings webViewSettings = webView.getSettings();
        webViewSettings.setMediaPlaybackRequiresUserGesture(false);
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setDomStorageEnabled(true);

        webView.loadUrl("https://appassets.androidplatform.net/assets/PixelGymGame.htm");
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
        {
        if (requestCode==123 && grantResults.length > 0)
            {
            mPermissionRequest.grant(mPermissionRequest.getResources());
            }
            else
            {
            mPermissionRequest.deny();
            }
        }

    @Override
    public void onDestroy()
        {
        super.onDestroy();
        enableAndSaveGameData();
        }

    @Override
    public void onPause()
        {
        super.onPause();
        enableAndSaveGameData();
        }

    @Override
    public void onBackPressed()
        {
        enableAndSaveGameData();
        System.exit(0);
        }

    private void enableAndSaveGameData()
        {
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().acceptCookie();
        CookieManager.getInstance().flush();
        }

    private void clickInPrivacy()
        {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view=inflater.inflate(R.layout.privacy, null);

        new AlertDialog.Builder(themedContext).setTitle(getResources().getString(R.string.textPrivacyTitle)).setView(view).setCancelable(false).setPositiveButton(getResources().getString(R.string.textPrivacyOK), new DialogInterface.OnClickListener()
            {
            public void onClick(DialogInterface dialog, int whichButton)
                {
                }
            }).show();
        }
    }