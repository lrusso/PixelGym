package com.pixelgym;

import android.content.Intent;
import android.webkit.JavascriptInterface;

public class JavaScriptShareInterface
    {
    @JavascriptInterface
    public void share(String appDescription)
        {
        try
            {
            Intent share = new Intent(android.content.Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, appDescription);
            MainActivity.myContext.startActivity(Intent.createChooser(share, MainActivity.myContext.getResources().getString(R.string.textShare)));
            }
            catch(Exception e)
            {
            }
        }
    }