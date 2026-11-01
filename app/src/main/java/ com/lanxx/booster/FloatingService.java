package com.lanxx.booster;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.provider.Settings;
import android.view.*;
import android.widget.*;

public class FloatingService extends Service {
    private WindowManager wm;
    private View bubble, panel;
    private boolean open=false;

    private int dp(float v) { return (int)(v*getResources().getDisplayMetrics().density+0.5f); }
    private GradientDrawable shape(int color,int stroke,int radius) {
        GradientDrawable d=new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(radius));
        if(stroke!=0)d.setStroke(dp(1),stroke); return d;
    }
    @Override public void onCreate() {
        super.onCreate();
        if(!Settings.canDrawOverlays(this)){ stopSelf(); return; }
        wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        showBubble();
    }
    private WindowManager.LayoutParams params(int w,int h,int gravity) {
        int type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        return new WindowManager.LayoutParams(w,h,type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT);
    }
    private void showBubble() {
        TextView b=new TextView(this); b.setText("L"); b.setTextColor(Color.WHITE);
        b.setTextSize(21); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD); b.setGravity(Gravity.CENTER);
        b.setBackground(shape(0xFFE52348,0xFFFF8293,40));
        WindowManager.LayoutParams p=params(dp(54),dp(54),Gravity.TOP|Gravity.LEFT);
        p.gravity=Gravity.TOP|Gravity.LEFT; p.x=dp(8); p.y=dp(220);
        b.setOnClickListener(v -> togglePanel());
        b.setOnTouchListener(new View.OnTouchListener(){
            int sx,sy,ox,oy; long down;
            public boolean onTouch(View v,android.view.MotionEvent e){
                if(e.getAction()==MotionEvent.ACTION_DOWN){sx=(int)e.getRawX();sy=(int)e.getRawY();ox=p.x;oy=p.y;down=System.currentTimeMillis();return true;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){p.x=ox+(int)e.getRawX()-sx;p.y=oy+(int)e.getRawY()-sy;wm.updateViewLayout(b,p);return true;}
                if(e.getAction()==MotionEvent.ACTION_UP){if(Math.abs(e.getRawX()-sx)<12&&Math.abs(e.getRawY()-sy)<12)togglePanel();return true;}
                return false;
            }
        });
        bubble=b; wm.addView(bubble,p);
    }
    private void togglePanel() {
        if(open){removePanel();return;}
        LinearLayout box=new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(14),dp(14),dp(14),dp(14)); box.setBackground(shape(0xF20D0D13,0xFFFF405F,18));
        TextView title=new TextView(this); title.setText("LANXX  /  QUICK PANEL");
        title.setTextColor(Color.WHITE); title.setTextSize(13); title.setTypeface(null,Typeface.BOLD);
        box.addView(title);
        addLine(box,"BOOSTER STATUS","Panel aktif");
        addLine(box,"BATTERY","Lihat status di aplikasi utama");
        addLine(box,"DEVICE INFO","Buka dashboard perangkat");
        Button close=new Button(this); close.setText("TUTUP PANEL"); close.setTextColor(Color.WHITE);
        close.setBackground(shape(0xFFB91F3C,0,12)); box.addView(close);
        close.setOnClickListener(v -> removePanel());
        WindowManager.LayoutParams p=params(dp(235),-2,Gravity.TOP|Gravity.LEFT);
        p.gravity=Gravity.TOP|Gravity.LEFT;p.x=dp(12);p.y=dp(285);
        panel=box;wm.addView(panel,p);open=true;
    }
    private void addLine(LinearLayout parent,String a,String b) {
        TextView t=new TextView(this);t.setText(a+"\n"+b);t.setTextColor(0xFFE8DDE2);
        t.setTextSize(11);t.setPadding(0,dp(10),0,dp(10));parent.addView(t);
    }
    private void removePanel() {
        if(panel!=null){try{wm.removeView(panel);}catch(Exception ignored){}panel=null;}open=false;
    }
    @Override public int onStartCommand(Intent intent,int flags,int startId){return START_NOT_STICKY;}
    @Override public void onDestroy(){
        removePanel(); if(bubble!=null){try{wm.removeView(bubble);}catch(Exception ignored){}bubble=null;}
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent){return null;}
                             }
          
