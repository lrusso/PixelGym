package com.pixelgym;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private WebView webView;
    private PermissionRequest mPermissionRequest;
    private ContextThemeWrapper themedContext;
    public static Activity myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myContext = this;

        themedContext = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);

        enableAndSaveGameData();

        webView = (WebView) findViewById(R.id.webView1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        if (appWasUpdated()==true)
            {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            }
            else
            {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }

        webView.setBackgroundColor(Color.rgb(0,0,0));
        webView.addJavascriptInterface(new JavaScriptShareInterface(), "AndroidShareHandler");
        webView.setWebViewClient(new WebViewClient()
            {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error)
                {
                super.onReceivedError(view, request, error);
                errorLoading();
                }

            @Override public void onReceivedHttpError (WebView view, WebResourceRequest request, WebResourceResponse errorResponse)
                {
                super.onReceivedHttpError(view, request, errorResponse);
                errorLoading();
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
        webView.loadUrl("https://www.pixelgym.com/");
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

    private void errorLoading()
        {
        webView.setVisibility(View.GONE);

        new AlertDialog.Builder(themedContext).setTitle(getResources().getString(R.string.textErrorTitle)).setCancelable(false).setMessage(getResources().getString(R.string.textErrorText)).setPositiveButton(getResources().getString(R.string.textErrorOK),new DialogInterface.OnClickListener()
            {
            public void onClick(DialogInterface dialog,int which)
                {
                System.exit(0);
                }
            }).show();
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

    private boolean appWasUpdated()
        {
        SharedPreferences sp = getSharedPreferences("PixelGymStoredPrefs", Activity.MODE_PRIVATE);
        int lastVersion = sp.getInt("lastVersion", -1);

        try
            {
            PackageInfo pInfo = myContext.getPackageManager().getPackageInfo(myContext.getPackageName(), 0);
            int currentVersion = pInfo.versionCode;

            if (currentVersion>lastVersion)
                {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("lastVersion", currentVersion);
                editor.commit();
                return true;
                }
            }
            catch (Exception e)
            {
            }

        return false;
        }

    private String loadAssetTextAsString(String name)
        {
        BufferedReader in = null;
        try
            {
            StringBuilder buf = new StringBuilder();
            InputStream is = getAssets().open(name);
            in = new BufferedReader(new InputStreamReader(is));

            String str;
            boolean isFirst = true;
            while ((str=in.readLine())!=null)
                {
                if (isFirst)
                    {
                    isFirst = false;
                    }
                    else
                    {
                    buf.append("\n");
                    }
                buf.append(str);
                }
            return buf.toString();
            }
            catch (IOException e)
            {
            }
            finally
            {
            if (in!=null)
                {
                try
                    {
                    in.close();
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
        return null;
        }
    }