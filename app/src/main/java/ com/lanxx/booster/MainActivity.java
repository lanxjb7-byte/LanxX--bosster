package com.lanxx.booster;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.*;
import android.view.animation.*;
import android.widget.*;
import android.text.TextUtils;

import rikka.shizuku.Shizuku;

public class MainActivity extends Activity {
    private static final int SHIZUKU_REQUEST = 7101;
    private LinearLayout root, content;
    private TextView shizukuStatus, batteryValue, ramValue, refreshValue;
    private boolean boosterEnabled = false;

    private final Shizuku.OnRequestPermissionResultListener permissionListener =
        (requestCode, grantResult) -> runOnUiThread(() -> {
            if (requestCode == SHIZUKU_REQUEST) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    setStatus("SHIZUKU IZIN DIBERIKAN");
                    activateOverlayFlow();
                } else {
                    setStatus("IZIN SHIZUKU DITOLAK");
                    toast("Izin Shizuku belum diberikan.");
                }
            }
        });

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(9,9,13));
        getWindow().setNavigationBarColor(Color.rgb(9,9,13));
        Shizuku.addRequestPermissionResultListener(permissionListener);
        showIntro();
    }

    @Override protected void onDestroy() {
        Shizuku.removeRequestPermissionResultListener(permissionListener);
        super.onDestroy();
    }

    private int dp(float v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
    private GradientDrawable bg(int color, int stroke, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius));
        if (stroke != 0) d.setStroke(dp(1), stroke);
        return d;
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size);
        t.setTextColor(color); t.setGravity(Gravity.CENTER_VERTICAL);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL); return l;
    }
    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    private void pad(View v, int l, int t, int r, int b) { v.setPadding(dp(l),dp(t),dp(r),dp(b)); }
    private void showIntro() {
        FrameLayout frame = new FrameLayout(this);
        frame.setBackgroundColor(Color.rgb(9,9,13));
        TextView logo = text("LANXX", 38, Color.WHITE, true);
        logo.setLetterSpacing(0.18f);
        logo.setShadowLayer(dp(18),0,0,0xFFFF304F);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-2,-2,Gravity.CENTER);
        frame.addView(logo, lp);
        TextView sub = text("BOOSTER  //  ANDROID GAMING UTILITY", 10, 0xFFB8AEB4, true);
        sub.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams sp = new FrameLayout.LayoutParams(-1,dp(32),Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        sp.topMargin = dp(72); frame.addView(sub,sp);
        setContentView(frame);
        logo.setTranslationX(-getResources().getDisplayMetrics().widthPixels);
        logo.setAlpha(0f);
        logo.animate().translationX(0).alpha(1f).setDuration(1450)
            .setInterpolator(new DecelerateInterpolator(1.8f)).withEndAction(() ->
                logo.animate().scaleX(1.06f).scaleY(1.06f).setDuration(260)
                    .withEndAction(() -> logo.animate().scaleX(1f).scaleY(1f).setDuration(420)
                        .withEndAction(() -> new Handler().postDelayed(this::showDashboard, 250)).start()).start()
            ).start();
    }

    private void showDashboard() {
        ScrollView scroll = new ScrollView(this);
        root = column(); root.setBackgroundColor(Color.rgb(9,9,13));
        pad(root,18,20,18,24);
        scroll.addView(root); setContentView(scroll);

        TextView brand = text("LANXX",30,Color.WHITE,true);
        brand.setLetterSpacing(0.16f);
        brand.setShadowLayer(dp(12),0,0,0xFFFF304F);
        root.addView(brand);
        TextView subtitle = text("BOOSTER CONTROL CENTER  •  JAVA EDITION",10,0xFF9B929A,true);
        root.addView(subtitle);
        addSpace(root,18);

        LinearLayout hero = card();
        TextView tag = text("SYSTEM STATUS",10,0xFFFF526B,true);
        hero.addView(tag);
        TextView heading = text("PERFORMA PERANGKAT",19,Color.WHITE,true);
        hero.addView(heading);
        shizukuStatus = text("SHIZUKU: BELUM DIPERIKSA",12,0xFFFF8797,true);
        hero.addView(shizukuStatus);
        addSpace(hero,12);
        Button activate = actionButton("AKTIFKAN LANXX BOOSTER");
        hero.addView(activate);
        activate.setOnClickListener(v -> requestShizuku());
        root.addView(hero);

        addSpace(root,14);
        TextView gamesTitle = text("GAME LAUNCHER",16,Color.WHITE,true); root.addView(gamesTitle);
        addSpace(root,8);
        LinearLayout games = row();
        Button ff = actionButton("FREE FIRE TH");
        Button ffm = actionButton("FREE FIRE MAX");
        LinearLayout.LayoutParams half = new LinearLayout.LayoutParams(0,dp(54),1);
        half.rightMargin=dp(6); games.addView(ff,half);
        LinearLayout.LayoutParams half2 = new LinearLayout.LayoutParams(0,dp(54),1);
        half2.leftMargin=dp(6); games.addView(ffm,half2);
        ff.setOnClickListener(v -> launchGame("com.dts.freefireth"));
        ffm.setOnClickListener(v -> launchGame("com.dts.freefiremax"));
        root.addView(games);

        addSpace(root,16);
        TextView monitorTitle = text("DEVICE MONITOR",16,Color.WHITE,true); root.addView(monitorTitle);
        addSpace(root,8);
        LinearLayout stats = row();
        batteryValue = statCard(stats,"BATTERY","--");
        ramValue = statCard(stats,"RAM AVAILABLE","--");
        refreshValue = statCard(stats,"DISPLAY HZ","--");
        root.addView(stats);
        addSpace(root,16);

        TextView toolsTitle = text("BOOSTER TOOLS",16,Color.WHITE,true); root.addView(toolsTitle);
        addSpace(root,8);
        addTool("FLOATING L PANEL","Buka panel pintasan mengambang", this::enableFloating);
        addTool("OPTIMIZE TIPS","Panduan pengaturan performa aman", this::showOptimizeInfo);
        addTool("CLEANER","Buka pengaturan penyimpanan Android", this::openStorage);
        addTool("SHIZUKU STATUS","Periksa koneksi dan izin Shizuku", this::requestShizuku);
        addSpace(root,18);
        TextView foot = text("LanxX Booster tidak mengubah file game atau menjamin kenaikan FPS. Data monitor mengikuti API yang tersedia pada perangkat.",10,0xFF8D858C,false);
        foot.setGravity(Gravity.CENTER); root.addView(foot);
        updateStats();
    }

    private LinearLayout card() {
        LinearLayout c=column(); pad(c,16,15,16,15);
        c.setBackground(bg(0xFF17131A,0xFF642536,18)); return c;
    }
    private void addSpace(LinearLayout p,int h) {
        View s=new View(this); p.addView(s,new LinearLayout.LayoutParams(1,dp(h)));
    }
    private Button actionButton(String label) {
        Button b=new Button(this); b.setText(label); b.setTextSize(11); b.setTextColor(Color.WHITE);
        b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        b.setBackground(bg(0xFFB91F3C,0xFFFF536D,14));
        return b;
    }
    private TextView statCard(LinearLayout parent,String label,String value) {
        LinearLayout c=column(); pad(c,9,10,7,10); c.setBackground(bg(0xFF17131A,0xFF38232B,12));
        TextView v=text(value,18,Color.WHITE,true); c.addView(v);
        TextView l=text(label,8,0xFFB7AAB1,true); c.addView(l);
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(dp(3),0,dp(3),0); parent.addView(c,p);
        return v;
    }
    private void addTool(String title,String desc,Runnable action) {
        LinearLayout item=card(); item.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout labels=column(); labels.addView(text(title,13,Color.WHITE,true));
        labels.addView(text(desc,10,0xFFAAA0A7,false));
        item.addView(labels,new LinearLayout.LayoutParams(0,-2,1));
        TextView go=text("OPEN  ›",10,0xFFFF526B,true); item.addView(go);
        item.setOnClickListener(v -> action.run());
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.bottomMargin=dp(8); root.addView(item,p);
    }

    private void requestShizuku() {
        try {
            if (Shizuku.isPreV11()) { setStatus("SHIZUKU API TIDAK DIDUKUNG"); return; }
            if (!Shizuku.pingBinder()) {
                setStatus("SHIZUKU BELUM BERJALAN");
                new AlertDialog.Builder(this).setTitle("Shizuku belum aktif")
                    .setMessage("Pasang dan jalankan aplikasi Shizuku terlebih dahulu. Pada Android 11 ke atas, ikuti petunjuk Wireless debugging di aplikasi Shizuku.")
                    .setNegativeButton("TUTUP",null)
                    .setPositiveButton("BUKA SHIZUKU", (d,w) -> openShizuku()).show();
                return;
            }
            if (Shizuku.checkSelfPermission()==PackageManager.PERMISSION_GRANTED) {
                setStatus("SHIZUKU TERHUBUNG • IZIN AKTIF");
                activateOverlayFlow();
            } else {
                setStatus("MEMINTA IZIN SHIZUKU...");
                Shizuku.requestPermission(SHIZUKU_REQUEST);
            }
        } catch (Exception e) {
            setStatus("SHIZUKU TIDAK SIAP");
            toast("Tidak dapat memeriksa Shizuku: "+e.getMessage());
        }
    }
    private void setStatus(String s) {
        if (shizukuStatus!=null) shizukuStatus.setText(s);
    }
    private void openShizuku() {
        try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://shizuku.rikka.app/"))); }
        catch(Exception ignored) {}
    }
    private void activateOverlayFlow() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this).setTitle("Izin floating window")
                .setMessage("Izinkan LanxX Booster tampil di atas aplikasi lain agar tombol L dapat digunakan.")
                .setNegativeButton("NANTI",null)
                .setPositiveButton("BUKA PENGATURAN",(d,w) -> {
                    Intent i=new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:"+getPackageName()));
                    startActivity(i);
                }).show();
            return;
        }
        boosterEnabled=true;
        setStatus("LANXX BOOSTER AKTIF");
        startFloatingService();
        toast("LanxX Booster aktif. Panel L siap digunakan.");
    }
    @Override protected void onResume() {
        super.onResume();
        if (Settings.canDrawOverlays(this) && boosterEnabled) startFloatingService();
        updateStats();
    }
    private void enableFloating() {
        if (!Settings.canDrawOverlays(this)) {
            activateOverlayFlow();
        } else {
            startFloatingService();
            toast("Panel L ditampilkan.");
        }
    }
    private void startFloatingService() {
        if (Build.VERSION.SDK_INT>=26) startService(new Intent(this,FloatingService.class));
        else startService(new Intent(this,FloatingService.class));
    }
    private void launchGame(String pkg) {
        Intent launch=getPackageManager().getLaunchIntentForPackage(pkg);
        if (launch==null) {
            toast("Game belum terpasang atau package tidak ditemukan.");
            return;
        }
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try { startActivity(launch); }
        catch(Exception e) { toast("Game tidak dapat dibuka."); }
    }
    private void updateStats() {
        if (batteryValue==null) return;
        try {
            IntentFilter f=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent b=registerReceiver(null,f);
            if (b!=null) {
                int level=b.getIntExtra("level",-1), scale=b.getIntExtra("scale",100);
                batteryValue.setText((level*100/Math.max(1,scale))+"%");
            }
            ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo();
            ((ActivityManager)getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(mi);
            ramValue.setText(String.format(java.util.Locale.US,"%.1f GB",mi.availMem/1073741824.0));
            float hz=getWindowManager().getDefaultDisplay().getRefreshRate();
            refreshValue.setText(String.format(java.util.Locale.US,"%.0f Hz",hz));
        } catch(Exception ignored) {}
    }
    private void showOptimizeInfo() {
        new AlertDialog.Builder(this).setTitle("OPTIMIZE TIPS")
            .setMessage("• Tutup aplikasi yang tidak sedang dipakai melalui pengaturan Android.\\n\\n• Gunakan pengaturan grafis game yang sesuai kemampuan perangkat.\\n\\n• Pastikan ruang penyimpanan cukup dan perangkat tidak terlalu panas.\\n\\nCatatan: Android membatasi aplikasi pihak ketiga untuk mengubah FPS atau GPU game lain secara langsung.")
            .setPositiveButton("MENGERTI",null).show();
    }
    private void openStorage() {
        try { startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)); }
        catch(Exception e) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
    }
    private void toast(String s) { Toast.makeText(this,s,Toast.LENGTH_LONG).show(); }
  }
                  
