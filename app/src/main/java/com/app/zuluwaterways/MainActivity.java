package com.app.zuluwaterways;


import static android.app.DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.navionics.android.nms.NMSCameraPosition;
import com.navionics.android.nms.NMSEnum;
import com.navionics.android.nms.NMSGeoObject;
import com.navionics.android.nms.NMSInitializationListener;
import com.navionics.android.nms.NMSMapSettings;
import com.navionics.android.nms.NMSMapSettingsEdit;
import com.navionics.android.nms.NMSMapView;
import com.navionics.android.nms.NMSMarker;
import com.navionics.android.nms.NMSMutablePath;
import com.navionics.android.nms.NMSPolyline;
import com.navionics.android.nms.NMSSettings;
import com.navionics.android.nms.NavionicsMobileServices;
import com.navionics.android.nms.core.NSError;
import com.navionics.android.nms.model.CGPoint;
import com.navionics.android.nms.model.NMSColor;
import com.navionics.android.nms.model.NMSLocationCoordinate2D;
import com.navionics.android.nms.ui.NMSMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.library.BuildConfig;
import org.osmdroid.tileprovider.modules.OfflineTileProvider;
import org.osmdroid.tileprovider.tilesource.FileBasedTileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;


public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private MapView map = null;
    private NMSMapView nMap = null;
    private DatabaseHelper mDbHelper;
    public static DatabaseHelper pdb;
    private ScaleBarOverlay mScaleBarOverlay;
    private StringBuilder  res = new StringBuilder();
    private Map<String, Drawable> Symbols = new HashMap<>();
    private Integer totalTypes=-1;
    private boolean isFetching = false;
    private BoundingBox fetchedGrid = new BoundingBox(0,0,0,0);
    private final ArrayList<Marker> items = new ArrayList<>();
    private MyLocationNewOverlay myLocation;
    private int multiDownloads = 0;
    private Toast notes;
    private String token="";
    private String premuim = "";
    private final DisplayMetrics displayMetrics = new DisplayMetrics();
    private Set<String> filter = Collections.emptySet();
    private final Map<String,String> filtered = new HashMap<>();
    private int iconSize = 200;
    private String CurrentMaptype = "/data/ZuluBase.sqlite";
    private String satDatabase = "/data/ZuluBase.sqlite";
    private String streetDatabase = "/data/ZuluStreet.sqlite";
    private String overlayDatabase = "/data/ZuluOverlay.sqlite";
    private final String satProvider = "USGS National Map Satellite";
    public int downloadsLeft = 100;
    private static final int DOWNLOADS_PER_MONTH=100;
    public static int DOWNLOADS_MARKER_REWARD=10;
    public static int DOWNLOADS_COMMENT_REWARD=5;
    private final ArrayList<NMSMarker> navionicsMarkers = new ArrayList<>();
    private float currentNavZoom = 0;
    private RadiusMarkerClusterer markers_clusters;
    private final ArrayList<Marker> measureMarkers= new ArrayList<>();
    private final ArrayList<Marker> measureMarkertexts= new ArrayList<>();
    private Polyline measureLine = new Polyline();
    private final ArrayList<NMSMarker> measureNMarkers= new ArrayList<>();
    private final ArrayList<NMSMarker> measureNMarkertexts= new ArrayList<>();
    private NMSPolyline measureNLine;
    private PopupWindow infoW;
    private GeoPoint infoWP = new GeoPoint(0.0 ,0.0);
    private String pleaseOpen = "";
    private int MIN_CLUSTER_ZOOM = 10;
    private final ArrayList<Overlay> overlays = new ArrayList<>();
    private long pressTimer = 0;
    private float pressX;
    private float pressY;
    private DownloadManager mgr=null;
    private Locale currentLang;
    private PopupMenu longp;
    private PopupMenu nlongp;
    private ViewPager tuteVP;
    private LinearLayout Layout_bars;
    private int[] screens;
    private static Context context;
    private String lastMessage = "";
    private String newDatabase = "";
    private int requestTimeout = 5;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private boolean NavA = false;//activate this if you have set up navionics

    @Override public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        try {
            CurrentMaptype = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluBase.sqlite";
            satDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluBase.sqlite";
            streetDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluStreet.sqlite";
            overlayDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluOverlay.sqlite";
            if(!getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].canWrite())throw new Exception();
        }catch (Exception e){
            //displayMessage("Issue accessing external storage. Using internal storage.");
            Log.d("##########","using internal dbs");
            CurrentMaptype = getFilesDir().getAbsolutePath() + "/ZuluBase.sqlite";
            satDatabase = getFilesDir().getAbsolutePath() + "/ZuluBase.sqlite";
            streetDatabase = getFilesDir().getAbsolutePath() + "/ZuluStreet.sqlite";
            overlayDatabase = getFilesDir().getAbsolutePath() + "/ZuluOverlay.sqlite";
        }
        token = getResources().getString(R.string.token);
        //mAuth = FirebaseAuth.getInstance();

        MainActivity.context = getApplicationContext();
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)getAppContext(). getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        activityManager.getMemoryClass();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(NavA) {
            NMSSettings settings = NMSSettings.settings();
            settings.setLanguage(NMSEnum.NMSLanguage.NMSLanguageAuto);
            settings.setMode(NMSEnum.NMSFrameworkMode.NMSFrameworkModeProduction);
            settings.setProjectToken(getResources().getString(R.string.project));
            settings.setPrivateKey(getResources().getString(R.string.privateKey));
            settings.setConfigurationToken(getResources().getString(R.string.config));

            NavionicsMobileServices.initializeWithSettings(settings, new NMSInitializationListener() {
                @Override
                public void onSuccess() {
                    //init(savedInstanceState);
                }

                @Override
                public void onError(NSError nsError) {
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Mobile SDK Initialization error");
                    alertDialog.setMessage("The mobile sdk cannot be initialized, please check the configuration");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    System.exit(0);
                                }
                            });
                    alertDialog.show();
                }
            });
        }
        //Log.d("toc",""+(new Date()).toString());
        org.osmdroid.config.IConfigurationProvider osmConf = org.osmdroid.config.Configuration.getInstance();
        File basePath = new File(getCacheDir().getAbsolutePath(), "osmdroid");
        osmConf.setOsmdroidBasePath(basePath);
        File tileCache = new File(osmConf.getOsmdroidBasePath().getAbsolutePath(), "tile");
        osmConf.setOsmdroidTileCache(tileCache);
        setContentView(R.layout.activity_main);
        //Log.d("died?","nope!");
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mDbHelper = new DatabaseHelper(this);
        pdb=mDbHelper;
        map = findViewById(R.id.map);
        map.setUseDataConnection(false);
        map.setTileSource(TileSourceFactory.USGS_SAT);
        map.setVerticalMapRepetitionEnabled(false);
        //setOrientation();
        //((Toolbar)findViewById(R.id.toolbar)).setTitle("");

        map.setMultiTouchControls(true);
        notes = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        registerReceiver(onNotificationClick,
                new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));


        map.getController().setZoom(9.0);
        map.getController().setCenter(new GeoPoint(-30.0, 153.0));
        //((FloatingActionButton) findViewById(R.id.fab)).setImageResource(R.drawable.ic_add_foreground);


        final Context context = this.getApplicationContext();
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(map);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10);

        //String copyrightNotice = map.getTileProvider().getTileSource().getCopyrightNotice();
        CopyrightOverlay copywrite = new CopyrightOverlay(this);
        copywrite.setCopyrightNotice("Am I here?");
        map.getOverlays().add(copywrite);


        Paint bg = new Paint();
        bg.setColor(Color.parseColor("#55555555"));
        Paint tx = new Paint();
        tx.setColor(Color.WHITE);



        CompassOverlay mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this),
                map);
        mCompassOverlay.setOptionsMenuEnabled(true);
        mCompassOverlay.setPointerMode(true);
        mCompassOverlay.setEnabled(true);
        map.getOverlays().add(mCompassOverlay);
        fetchedGrid = map.getBoundingBox();

        checkFirstRun();

        //updateDB();
        try {
            loadIconData();
            loadIcons();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[] reqPermission = new String[] { Manifest.permission.ACCESS_FINE_LOCATION };
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            myLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getApplicationContext()),map);
            myLocation.enableMyLocation();
            map.getOverlays().add(myLocation);
            if (findViewById(R.id.permission2)!=null)((CheckBox)findViewById(R.id.permission2)).setChecked(true);
        }
//        (new Handler()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                throw new BufferOverflowException();
//            }
//        }, 5000);


        map.getOverlays().add(new MapEventsOverlay(mReceive));

        final int DEFAULT_INACTIVITY_DELAY_IN_MILLISECS = 200;

        // If there is more than 200 millisecs no zoom/scroll update markers
        map.addMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                if (map == null) return false;
                //long tic = System.currentTimeMillis();
                try {
                    DecimalFormat df = new DecimalFormat("#.#######");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    String ll = df.format(map.getMapCenter().getLatitude()) + ", " + df.format(map.getMapCenter().getLongitude());
                    ((TextView) findViewById(R.id.LatLong)).setText(ll);
                    //Log.d("moved", "srolled");
                    loadIcons();
                } catch (Exception e) {
                    e.printStackTrace();
                    isFetching = false;
                }
                //Log.d("tocs",""+(System.currentTimeMillis()-tic));
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                //Log.d("moved", "zoomed");
                InfoWindow.closeAllInfoWindowsOn(map);
                //fetchedGrid = new BoundingBox(0,0,0,0);
                loadIcons();
                return false;
            }


        }, DEFAULT_INACTIVITY_DELAY_IN_MILLISECS));


        if (Looper.myLooper() == null) Looper.prepare();

        if(NavA) {
            nMap = ((NMSMapFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.mapFragment))).getMapView();
            nMap.setOnCameraMove(new NMSMapView.OnCameraMoveListener() {
                @Override
                public void onCameraChange(NMSCameraPosition nmsCameraPosition) {
                    if (!CurrentMaptype.equals("navionics")) return;
                    DecimalFormat df = new DecimalFormat("#.#####");
                    df.setRoundingMode(RoundingMode.HALF_UP);
                    String ll = df.format(nMap.getCamera().getTarget().getLatitude()) + "\n" + df.format(nMap.getCamera().getTarget().getLongitude());
                    if (nMap != null) ((TextView) findViewById(R.id.LatLong)).setText(ll);
                    if (infoW != null) infoW.dismiss();
                    if (navionicsMarkers.isEmpty()) loadIcons();
                }
            });
            NavionicsMobileServices.setOnObjectsAtPoint(list -> {
                try {
                    PopupMenu m = new PopupMenu(getBaseContext(), findViewById(R.id.long_press_spacer));
                    for (NMSGeoObject o : list) {
                        try {
                            o.loadDetails();

                            for (String s : o.getDetails().keySet()) {
                                Object ss = o.getDetails().get(s);
                                if (ss != null) m.getMenu().add(ss.toString());
                                //Log.d("deets",s+","+ss.toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    m.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    displayMessage(e.getMessage());
                }
            });
            loadPreferences();
            nMap.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, final MotionEvent event) {
                    v.performClick();

                    double d = Math.sqrt(Math.pow(event.getX() - pressX, 2) + Math.pow(event.getY() - pressY, 2));
                    if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        //Log.d("Touch detected", event.getX() + "," + event.getY());
                        //Log.d("Touch Time", (System.currentTimeMillis()-pressTimer)+"ms");
                        //Log.d("Touch Time", event.getDownTime()+"ms");
                        //Log.d("Touch distance", d+"m");
                        //Log.d("screens", "" + nMap.coordinateForPoint(new Point((int) event.getX(), (int) event.getY())));
                        final GeoPoint p = new GeoPoint(
                                nMap.coordinateForPoint(new Point((int) event.getX(), (int) event.getY())).getLatitude(),
                                nMap.coordinateForPoint(new Point((int) event.getX(), (int) event.getY())).getLongitude());
                        final Point ep = new Point((int) event.getX(), (int) event.getY());


                        if ((System.currentTimeMillis() - pressTimer) > 500 && d < 10) { //long press
                            if (!measureNMarkers.isEmpty()) {
                                addNMeasure(p);
                                return false;
                            }
                            final View spacer = findViewById(R.id.long_press_spacer);
                            CoordinatorLayout.LayoutParams l = (CoordinatorLayout.LayoutParams) spacer.getLayoutParams();
                            l.setMargins((int) event.getX(), (int) event.getY(), 0, 0);
                            findViewById(R.id.long_press_spacer).setLayoutParams(l);
                            nlongp = new PopupMenu(getApplicationContext(), spacer);
                            nlongp.getMenu().add(getResources().getString(R.string.download_maps)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    if (NavionicsMobileServices.isNavionicsUserLoggedIn()) {
                                        NavionicsMobileServices.enableDownloadAreaSelector();
                                        findViewById(R.id.navionics_download).setVisibility(View.VISIBLE);
                                        findViewById(R.id.close).setVisibility(View.VISIBLE);
                                    } else {
                                        NavionicsMobileServices.navionicsUser();
                                    }
                                    return false;
                                }
                            });
                            nlongp.getMenu().add(getResources().getString(R.string.query_location)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    NavionicsMobileServices.geoObjectsAtPoint(ep);
                                    return false;
                                }
                            });
                            nlongp.getMenu().add(getResources().getString(R.string.measure)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    addNMeasure(p);
                                    return false;
                                }
                            });
                            nlongp.show();

                        }
                        loadIcons();
                        if ((System.currentTimeMillis() - pressTimer) < 30 || d > 10) {
                            //Log.d("pressed",(System.currentTimeMillis()-pressTimer)+","+d);
                            return true;
                        }
                        NMSLocationCoordinate2D l = nMap.coordinateForPoint(new Point((int) event.getX(), (int) event.getY()));
                        if (infoW != null) infoW.dismiss();
                        for (final NMSMarker m : navionicsMarkers) {
                            if (TileSystem.inImage(m.getPosition().getLatitude(), m.getPosition().getLongitude(),
                                    (int) nMap.getCamera().getZoom(), m.getImage().getHeight() / 2, m.getImage().getWidth() / 2,
                                    l.getLatitude(), l.getLongitude())) {
                                //Log.d("image", "HIT!!!!");
                                //Log.d("image", m.getTitle());
                                showIW(m.getTitle(), m.getPosition().getLatitude(), m.getPosition().getLongitude(), m.getImage());

                                return false;
                            }
                        }
                    }
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        pressTimer = System.currentTimeMillis();
                        pressX = event.getX();
                        pressY = event.getY();

                    }

                    return false;
                }
            });
        }
        loadPreferences();
        if (savedInstanceState != null)
            ((WebView)findViewById(R.id.onlineWeb)).restoreState(savedInstanceState);
        else {
            Log.d("loading","url");
            ((WebView) findViewById(R.id.onlineWeb)).loadUrl("https://www.zuluwaterways.com/");
        }
        markers_clusters = new RadiusMarkerClusterer(this);
        markers_clusters.setRadius(iconSize);
        markers_clusters.mTextAnchorU = 0.5f;
        markers_clusters.mTextAnchorV = 0.5f;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.marker_cluster);
        markers_clusters.setIcon(bm.createScaledBitmap(bm, iconSize, iconSize, false));
        markers_clusters.getTextPaint().setColor(Color.WHITE);
        map.getOverlays().add(markers_clusters);

        mScaleBarOverlay.setTextPaint(tx);
        // mScaleBarOverlay.setBarPaint(tx);
        mScaleBarOverlay.setTextSize(Math.round(iconSize / 4.0));
        mScaleBarOverlay.setBackgroundPaint(bg);
        map.getOverlays().add(mScaleBarOverlay);

        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
//        String appLinkAction = appLinkIntent.getAction();
//        Uri appLinkData = appLinkIntent.getData();
        handleIntent(appLinkIntent);
        //FacebookSdk.sdkInitialize(this);
        FirebaseAuth.getInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(onComplete);
        unregisterReceiver(onNotificationClick);
    }
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);

                }
            }
    );

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    public void showMainMenu(View view) {
        PopupMenu p = new PopupMenu(this, view);
        getMenuInflater().inflate(R.menu.menu_main,p.getMenu());
        prepareMainMenu(p.getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                handleMainMenuClicks(item);
                return false;
            }
        });
        p.show();
    }

    public boolean prepareMainMenu (final Menu menu){
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.navionics_options_menu).setVisible(findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            menu.findItem(R.id.loginMenu).setTitle(R.string.sign_out);
        }else{
            menu.findItem(R.id.loginMenu).setTitle(R.string.login);
        }

        menu.getItem(1).getSubMenu().clear();
        SortedSet<String> vals = new TreeSet<>(filtered.values());
        MenuItem fa = menu.getItem(1).getSubMenu().add(R.string.select_all);
        fa.setCheckable(true);
        fa.setChecked(filter.isEmpty());
        fa.setTitleCondensed("notme");
        try {
            Log.d("ietms",menu.getItem(8).toString());
            menu.getItem(8).getSubMenu().getItem(5).setTitle("Version "+ this.getPackageManager().getPackageInfo(getPackageName(),0).versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }


        for(int i = 0;i<filtered.size();i++) {
            MenuItem f = menu.getItem(1).getSubMenu().add(vals.toArray()[i].toString());
            f.setCheckable(true);
            for (Map.Entry<String, String> entry : filtered.entrySet()) {
                if (entry.getValue().equals(vals.toArray()[i])) {
                    f.setTitleCondensed(entry.getKey());
                    f.setIcon(Symbols.get(entry.getKey()));
                    f.setChecked(!filter.contains(entry.getKey()));
                }
            }
            f.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    //Log.d("item",item.getTitle().toString()+"");
                    //item.setVisible(false);
                    item.setChecked(!item.isChecked());
                    try {
                        if (item.isChecked()) {
                            //Log.d("removing",filter.size()+"");
                            filter.remove(item.getTitleCondensed().toString());
                            //Log.d("removed",filter.size()+"");
                        } else {
                            filter.add(item.getTitleCondensed().toString());
                            //Log.d("added",filter.size()+"");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }


                    item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                    item.setActionView(new View(getApplicationContext()));
                    item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                        @Override
                        public boolean onMenuItemActionExpand(MenuItem item) {
                            return false;
                        }

                        @Override
                        public boolean onMenuItemActionCollapse(MenuItem item) {
                            return false;
                        }
                    });
                    map.invalidate();
                    fetchedGrid=new BoundingBox(0,0,0,0);
                    loadIcons();

                    return false;
                }
            });
        }

        fa.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Log.d("item",item.getTitle().toString()+"");
                //item.setVisible(false);

                if(item.isChecked()){
                    //Log.d("adding",filter.size()+"");
                    for(int i = 1;i<menu.getItem(1).getSubMenu().size();i++) {
                        try {
                            filter.add(menu.getItem(1).getSubMenu().getItem(i).getTitleCondensed().toString());
                            menu.getItem(1).getSubMenu().getItem(i).setChecked(false);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                    item.setChecked(false);
                    //Log.d("added",filter.size()+"");
                }else{
                    //Log.d("removing",filter.size()+"");
                    filter.clear();
                    for(int i = 1;i<menu.getItem(1).getSubMenu().size();i++) {
                        menu.getItem(1).getSubMenu().getItem(i).setChecked(true);
                    }
                    //Log.d("removed",filter.size()+"");
                    item.setChecked(true);
                }
                map.invalidate();
                fetchedGrid=new BoundingBox(0,0,0,0);
                loadIcons();

                item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                item.setActionView(new View(getApplicationContext()));
                item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
                return false;
            }
        });
        return true;
    }

    public boolean handleMainMenuClicks(MenuItem item) {
        if(item==null || item.getTitle()==null)return false;
        if(item.getTitle().equals(getResources().getString(R.string.more_settings)))startActivity((new Intent(MainActivity.this, Settings.class)));
        if(item.getTitle().equals(getResources().getString(R.string.online_mode))){
            //String ll = findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE?
            //(nMap.getCamera().getTarget().getLatitude()+"/"+nMap.getCamera().getTarget().getLongitude()):
            //map.getMapCenter().getLatitude()+"/"+map.getMapCenter().getLongitude();

            // ((WebView)findViewById(R.id.onlineWeb)).loadUrl("https://www.zuluwaterways.com/#/app/"+ll);
            if(infoW!=null && infoW.isShowing())infoW.dismiss();
            ((WebView)findViewById(R.id.onlineWeb)).evaluateJavascript("map.setCenter(["+map.getMapCenter().getLongitude()+","+map.getMapCenter().getLatitude()+"]);",null);
            ((WebView)findViewById(R.id.onlineWeb)).evaluateJavascript("map.setZoom("+(map.getZoomLevelDouble()-2)+");",null);
            findViewById(R.id.onlineCont).setVisibility(View.VISIBLE);
            findViewById(R.id.onlineWeb).setVisibility(View.VISIBLE);
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zuluwaterways.com/#/app/"+ll));
            //ntent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            //defaultBrowser.setData(browserIntent.getData());
            //startActivity(defaultBrowser);
        }
        if(item.getTitle().equals(getResources().getString(R.string.map_store))){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zuluwaterways.com/shop/"));
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(browserIntent.getData());
            startActivity(defaultBrowser);
        }
        if(item.getTitle().equals(getResources().getString(R.string.facebook))){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/groups/cruisingguide/"));
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(browserIntent.getData());
            startActivity(defaultBrowser);
        }
        if(item.getTitle().equals("YouTube")){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UCBv25JEsLSnNYgnL4ZQDfCA"));
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(browserIntent.getData());
            startActivity(defaultBrowser);
        }
        if(item.getTitle().equals(getResources().getString(R.string.patreon))){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.patreon.com/zuluwaterways"));
            Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
            defaultBrowser.setData(browserIntent.getData());
            startActivity(defaultBrowser);
        }
        if(item.getTitle().equals(getResources().getString(R.string.email))){
            Intent email = new Intent(android.content.Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL,"zuluwaterways@gmail.com");
            email.putExtra(Intent.EXTRA_SUBJECT,"Zulu offshore help request");
            //email.setType("application/octet-stream");
            email.putExtra(Intent.EXTRA_EMAIL, new String[]{"zuluwaterways@gmail.com"});
            email.setType("message/rfc822");
            startActivity(email);
        }
        if(item.getTitle().equals(getResources().getString(R.string.downloading_single_maps_video))) startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://youtu.be/-r_-It8oxes")));
        if(item.getTitle().equals(getResources().getString(R.string.installing_map_packs_video))) startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://youtu.be/v8qf-cQJF0Y")));
        if(item.getTitle().equals(getResources().getString(R.string.navionics_video))) startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://youtu.be/dQezUVR0SGM")));
        if(item.getTitle().equals(getResources().getString(R.string.advanced_function_video))) startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://youtu.be/vZvmeogROwU")));
        if(item.getTitle().equals(getResources().getString(R.string.rate_the_app))) rateApp(null);
        if(item.getTitle().equals(getResources().getString(R.string.search))){
            //findViewById(R.id.close).setVisibility(View.VISIBLE);
            final EditText txtUrl = new EditText(this);
            txtUrl.setHint("Search here");
            final LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.addView(txtUrl);
            ScrollView sv = new ScrollView(this);
            sv.addView(ll);
            final AlertDialog ad =  new AlertDialog.Builder(this)
                    .setTitle("Search")
                    .setView(sv).show();
            txtUrl.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
                @Override
                public void afterTextChanged(final Editable editable) { Log.d("searhing",txtUrl.toString());
                    for(int j=0;j<ll.getChildCount();j++){
                        if (ll.getChildAt(j)!=txtUrl){
                            ll.removeView(ll.getChildAt(j));
                            j--;
                        }
                    }

                    if (editable.length()>2) {
                        try{
                            String[] latlon = editable.toString().split(",");
                            if(latlon.length==2){
                                double lat = Double.parseDouble(latlon[0]);
                                double lon = Double.parseDouble(latlon[1]);
                                if (lat!=0 && lon!=0){
                                    TextView t = new TextView(getAppContext());
                                    t.setTextSize(25);
                                    //t.append("Go to:"+editable.toString());
                                    final GeoPoint g = new GeoPoint(lat, lon);
                                    final NMSLocationCoordinate2D ng = new NMSLocationCoordinate2D(lat,lon);
                                    t.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            map.getController().setZoom(15.0);
                                            map.getController().animateTo(g);
                                            if(NavA)nMap.moveToLocation(ng,15,true);
                                            ad.dismiss();
                                        }
                                    });
                                    ll.addView(t);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        ArrayList<String> sm = mDbHelper.searchMarkers(editable.toString());
                        for(int j = 0;j<sm.size();j++){
                            final TextView t = new TextView(getAppContext());
                            t.setTextSize(25);
                            LinearLayout lh = new LinearLayout(getAppContext());


                            ll.addView(lh);
                            String[] ms = sm.get(j).split(":");
                            t.append(ms.length < 5 ? ms[0] : ms[4]);
                            ImageView iv = new ImageView(getAppContext());
                            //iv.setImageDrawable(Symbols.get(ms[1]));
                            //Bitmap b = ((BitmapDrawable)Symbols.get(ms[1])).getBitmap();
                            if(Symbols.get(ms[1])!=null) {
                                int th = t.getLineHeight() > 0 ? t.getLineHeight() : 20;
                                Drawable dd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable) Symbols.get(ms[1])).getBitmap(), th, th, false));
                                iv.setImageDrawable(dd);
                                lh.addView(iv);
                            }
                            lh.addView(t);
                            final GeoPoint g = new GeoPoint(Double.parseDouble(ms[2]), Double.parseDouble(ms[3]));
                            final NMSLocationCoordinate2D ng = new NMSLocationCoordinate2D(Double.parseDouble(ms[2]), Double.parseDouble(ms[3]));
                            t.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    map.getController().setZoom(15.0);
                                    map.getController().animateTo(g);
                                    if(NavA)nMap.moveToLocation(ng,15,true);
                                    ad.dismiss();
                                }
                            });
                        }

                    }
                }
            });



        }
        if(item.getTitle().equals(getResources().getString(R.string.activate_a_code)))askForCode();
        if(item.getTitle().equals(getResources().getString(R.string.download_region)))getCode(null);
        if(item.getTitle().equals("Frequently Asked Questions"))startActivityForResult(new Intent(getApplicationContext(), faq.class),3);
        if(item.getTitle().equals(getResources().getString(R.string.login)))     showLogin();;
        if(item.getTitle().equals(getResources().getString(R.string.navionics_login)))NavionicsMobileServices.navionicsUser();
        if(item.getTitle().equals(getResources().getString(R.string.hints)))showTute();
        if(NavA&&item.getTitle().equals(getResources().getString(R.string.navionics_charts))){
            NMSMapSettings sett = nMap.getSettings();
            NMSMapSettingsEdit  editor= new NMSMapSettingsEdit(sett);
            editor.setMapMode(NMSEnum.NMSMapMode.NMSMapModeDefault);
            nMap.setSettings(editor);
        }
        if(NavA&&item.getTitle().equals(getResources().getString(R.string.sonar_charts))){
            NMSMapSettings sett = nMap.getSettings();
            NMSMapSettingsEdit  editor= new NMSMapSettingsEdit(sett);
            editor.setMapMode(NMSEnum.NMSMapMode.NMSMapModeSonarCharts);
            nMap.setSettings(editor);
        }
        if(NavA&&item.getTitle().equals(getResources().getString(R.string.download_maps))){
            if( NavionicsMobileServices.isNavionicsUserLoggedIn()){
                NavionicsMobileServices.enableDownloadAreaSelector();
                findViewById(R.id.navionics_download).setVisibility(View.VISIBLE);
                findViewById(R.id.close).setVisibility(View.VISIBLE);
            }else{
                NavionicsMobileServices.navionicsUser();
            }
        }
        final MainActivity app = this;
        if(item.getTitle().equals(getResources().getString(R.string.sync))){
            //updateDB();
            //if(true)return false;
            displayMessage("Now Syncing");
            //Toast.makeText(this,"fsf",Toast.LENGTH_LONG).show();
            Log.d("@@@@@@@@@@@@","syncing?");
            if (System.currentTimeMillis()-PreferenceManager.getDefaultSharedPreferences(app).getLong("lastSync",0)<600000){
                displayMessage("Database is up to date!");
                Toast.makeText(this,"Database is up to date!",Toast.LENGTH_LONG).show();
                Log.d("@@@@@@@@@@@@","sync now?");
                return false;
            }
            new testInternet(this, new Callable<String>() { //Next get all location points
                public String call() {
                    findViewById(R.id.syndOverlay).setVisibility(View.VISIBLE);
                    findViewById(R.id.fab).setVisibility(View.GONE);
                    findViewById(R.id.locNow).setVisibility(View.GONE);
                    findViewById(R.id.layerSelect).setVisibility(View.GONE);
                    //app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    ((ProgressBar) findViewById(R.id.syncProgressBar)).setProgress(10);
                    if(PreferenceManager.getDefaultSharedPreferences(app).getBoolean("sync_upload",true)) {
                        Log.d("@@@@@@@@@@@@","syncdd");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        //Log.d("@@@@@@@@@login result",user.getDisplayName()+"");
                        String un = user==null?"":user.getDisplayName();
                        app.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        findViewById(R.id.syndOverlay).setVisibility(View.GONE);
                        findViewById(R.id.fab).setVisibility(View.VISIBLE);
                        findViewById(R.id.locNow).setVisibility(View.VISIBLE);
                        findViewById(R.id.layerSelect).setVisibility(View.VISIBLE);
                        if (mDbHelper.SyncData(token, un)) {
                            displayMessage(getResources().getString(R.string.cant_upload));
                            Toast.makeText(getApplicationContext(),getResources().getString(R.string.cant_upload),Toast.LENGTH_LONG).show();
                        }else{
                            displayMessage(getResources().getString(R.string.sync_complete));
                        }

                        Log.d("@@@@@@@@@@@@","sync complete!");
                    }else{
                        updateDB();
                    }
                    return null;
                }
            }).execute();
        }
        if(item.getTitle().equals(getResources().getString(R.string.action_sign_out_short))) {
            token = getResources().getString(R.string.token);
            FirebaseAuth.getInstance().signOut();
            item.setTitle(R.string.login);
        }

        return false;
    }
    public void showLogin(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.AppleBuilder().build());

// Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        signInLauncher.launch(signInIntent);
    }
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            WebView wv =  ((WebView)findViewById(R.id.onlineWeb));
            wv.evaluateJavascript("closeModal();",null);
            wv.evaluateJavascript("currentUser=\""+FirebaseAuth.getInstance().getCurrentUser().getEmail()+"\";",null);
            wv.evaluateJavascript("currentUserName=\""+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"\";" ,null);
            wv.evaluateJavascript("currentUserId=\""+FirebaseAuth.getInstance().getCurrentUser().getUid()+"\";",null);
            wv.evaluateJavascript("document.getElementById(\"loginButton\").innerHTML=currentUserName;",null);
            wv.evaluateJavascript("document.getElementById(\"signOutButton\").style.display=\"\";",null);

            Log.d("@@@@@@@@@login result",user.getDisplayName()+"");
            new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
                public String call() {
                    //Log.d("update","got Back locs:"+res.toString());

                    if (res.toString().isEmpty()) return null;
                    String p = res.toString();
                    premuim = p;
                    Log.d("update", "got Back premuim:" + p);
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("premuim",p).apply();
                    return null;
                }
            }).execute("https://api.zuluwaterways.com/premium.php?tk=JeGyXMsSpiQJqe5es2akYzWldQD3&ids=" + user.getEmail());
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
        Log.d("@@@@@@@@@login result",result.getResultCode()+"");
    }

    private void askForCode() {
        final EditText txtUrl = new EditText(this);
        txtUrl.setHint(R.string.paste_code);

        new AlertDialog.Builder(this)
                .setTitle(R.string.activate_a_code)
                .setView(txtUrl)
                .setPositiveButton(getString(R.string.activate), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        activateCode(txtUrl.getText().toString());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .show();
    }

    private void gotoOnline(){


        if(NavA&&findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE){
            ((WebView) findViewById(R.id.onlineWeb)).evaluateJavascript("map.setCenter([" + nMap.getCamera().getTarget().getLongitude() + "," + nMap.getCamera().getTarget().getLatitude() + "]);", null);
            ((WebView) findViewById(R.id.onlineWeb)).evaluateJavascript("map.setZoom(" + (nMap.getCamera().getZoom() -2) + ");", null);
        }else {
            ((WebView) findViewById(R.id.onlineWeb)).evaluateJavascript("map.setCenter([" + map.getMapCenter().getLongitude() + "," + map.getMapCenter().getLatitude() + "]);", null);
            ((WebView) findViewById(R.id.onlineWeb)).evaluateJavascript("map.setZoom(" + (map.getZoomLevelDouble() - 2) + ");", null);
        }
        if(infoW.isShowing()) {
            Log.e("iw",((TextView) infoW.getContentView().findViewById(R.id.bubble_oldID)).getText().toString().trim());
            ((WebView) findViewById(R.id.onlineWeb)).evaluateJavascript("toOpen='" + ((TextView) infoW.getContentView().findViewById(R.id.bubble_oldID)).getText().toString().trim() + "';loadIcons();console.log(\"opening:\"+toOpen);", null);
        }
        if(infoW!=null && infoW.isShowing())infoW.dismiss();
        findViewById(R.id.onlineCont).setVisibility(View.VISIBLE);
        findViewById(R.id.onlineWeb).setVisibility(View.VISIBLE);

//        String ll = findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE?
//                (nMap.getCamera().getTarget().getLatitude()+"/"+nMap.getCamera().getTarget().getLongitude()):
//                map.getMapCenter().getLatitude()+"/"+map.getMapCenter().getLongitude();
//        String dd = "https://www.zuluwaterways.com/#/app/"+ll;
//        if(!InfoWindow.getOpenedInfoWindowsOn(map).isEmpty()){
//            try {
//                dd= "https://www.zuluwaterways.com/#/"+((TextView) findViewById(R.id.bubble_oldID)).getText()+"/"+ll;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        if(infoW!=null && infoW.isShowing()){
//            try {
//                dd= "https://www.zuluwaterways.com/#/"+((TextView) infoW.getContentView().findViewById(R.id.bubble_oldID)).getText()+"/"+ll;
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dd));
//        Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
//        defaultBrowser.setData(browserIntent.getData());
//        startActivity(defaultBrowser);
    }

    private void activateCode(String code){
        if(code.length()!=11){
            displayMessage("Error invalid code, please try again");
            Toast.makeText(this,"Error invalid code, please try again",Toast.LENGTH_LONG).show();
            return;
        }
        try {
            ((TextView) findViewById(R.id.activateCode)).setText(code);
            findViewById(R.id.activateButton).setEnabled(code.length() == 11);
            String suf = code.substring(code.length() - 3);
            String infoText = "";
            if (code.charAt(7) == 'd') {
                int dp = Integer.parseInt(suf) * 10;
                infoText += "This code is will increase your download limit by:" + dp;
            }
            if (code.charAt(7) == 'm') {
                infoText += "This code is for the map pack:" + suf + ". This pack contains map imagery for all markers in the area. This will be a large download so we recommend strong wifi.";
            }
            if (code.charAt(7) == 'p') {
                infoText += "This code is for the premium guide pack:" + suf + ". This code is tied to your email login so you may need to log back in again for the purchase to take effect.";
                //token = "";
            }
            infoText += "\n Codes are single use and can only be activated on one device.";
            ((TextView) findViewById(R.id.activateText)).setText(infoText);
            findViewById(R.id.codeActivateContainer).setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
            displayMessage("Error activating code, please try again");
            Toast.makeText(this,"Error activating code, please try again",Toast.LENGTH_LONG).show();
        }
    }

    private void validateCode(String code){
        if(code.charAt(7)=='d') {
            res.delete(0, res.length());
            final int amount = Integer.parseInt(code.substring(code.length()-3))*10;
            new HTTPGetRequest(res, new Callable<String>() { //first update location types
                public String call() {
                    try {
                        //Log.d("activated return", res.toString());
                        //JSONObject activated = new JSONObject(res.toString());
                        if (res.toString().equals("{\"message\":\"success!\"}")) {
                            if (downloadsLeft < 0) downloadsLeft = 0;
                            downloadsLeft += amount;
                            displayMessage("Code Activated!\n Downloads remaining:" + downloadsLeft);
                            getIntent().setData(null);
                        } else {
                            displayMessage("Could not activate code.\n Please try again");
                        }

                    } catch (Exception e) {
                        displayMessage("Error Could not activate code.\n Please try again");
                        e.printStackTrace();
                    }
                    return null;
                }
            }).execute("https://www.zuluwaterways.com/offline/validateCode/", "activate", "?code=" + code);
        }else{
            activatePack(code);
        }
    }
    public void updateDB(){
        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        ((ProgressBar)findViewById(R.id.syncProgressBar)).setProgress(30);
        String ld =pdb.getLastdate();
        if(ld==""){
            getNewDB();
        }else {
            new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
                public String call() {
                    //Log.d("update","got Back locs:"+res.toString());
                    try {
                        if (res.toString().isEmpty()) return null;
                        JSONArray activated = new JSONArray(res.toString());
                        Log.d("update", "got Back locs2:" + activated.toString());
                        pdb.doUpdate(activated);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }).execute("https://api.zuluwaterways.com/sync.php?last=" + ld);
        }
        getPreferences(MODE_PRIVATE).edit().putLong("lastSync",System.currentTimeMillis()-4*24*60*60*1000).apply();

    }
    public void getNewDB() {
        res.delete(0,res.length());
        Uri uri=Uri.parse("https://api.zuluwaterways.com/sync.php");
        String dlfn = getExternalFilesDir("Downloads")==null?getFilesDir().getAbsolutePath():getExternalFilesDir("Downloads").getAbsolutePath();
        File f = new File(dlfn +"/ndb.sqlite");
        if(f.exists())f.delete();
        //lastDownload=

        mgr.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                .setTitle("Zulu Waterways")
                .setDescription("Downloading Community Guides")
                .setDestinationInExternalFilesDir(this,"Downloads","ndb.sqlite")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        );

        multiDownloads = 1;


        ((ProgressBar)findViewById(R.id.mapDownloadProgress)).setProgress(0);
        checkDownload();
    }


    public void loadIconData(){
        Log.d("loading icon data","loaded");
        Map<String, byte[]> typesIcons = mDbHelper.getTypesIconMap();
        Map<String, String> typesNames = mDbHelper.getTypesNameMap();
        totalTypes = typesIcons.size();
        Symbols = new HashMap<>();
        for(Map.Entry<String, byte[]> entry : typesIcons.entrySet()) {
            try{
                Drawable dd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray( entry.getValue(),
                        0,entry.getValue().length), iconSize, iconSize, false));
                Symbols.put(entry.getKey(), dd);
                filtered.put(entry.getKey(), typesNames.get(entry.getKey()));
                //Log.d(entry.getKey(),typesNames.get(entry.getKey()));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        Log.d("loading icon data","loaded");
    }
    public void loadIcons(){
        //Log.d("LoadingIcons","Start");
        //Long tic = System.currentTimeMillis();
        if(isFetching){Log.d("not loading","fetching");return;}//dont try to load icons if you are already loading icons
        //Log.d("LoadingIcons","fetching");
        if(map==null || map.getBoundingBox()==null ||markers_clusters==null){Log.d("not loading","map null");return;} //dont try to load icons if the map is not loaded yet
        //Log.d("LoadingIcons","map");
        if(Symbols.size()!=totalTypes){Log.d("not loading","symbols");return;} //dont try to load icons if the icon files have not loaded yet
        //Log.d("LoadingIcons","icons");
        //fetchedGrid = new BoundingBox(0,0,0,0);
        if(findViewById(R.id.map).getVisibility()==View.VISIBLE) {
            if (currentNavZoom==(int)map.getZoomLevelDouble() &&
                    map.getBoundingBox().getLonWest() > fetchedGrid.getLonWest() &&
                    map.getBoundingBox().getLonEast() < fetchedGrid.getLonEast() &&
                    map.getBoundingBox().getLatNorth() < fetchedGrid.getLatNorth() &&
                    map.getBoundingBox().getLatSouth() > fetchedGrid.getLatSouth()
            ) {
                Log.d("inBox", "Dont fetch");
                return; //if we are still inside the fetched envelope then dont bother refetching
            }
            currentNavZoom=(int)map.getZoomLevelDouble();
            map.getOverlayManager().removeAll(items);
            double wp = (map.getBoundingBox().getLonEast()-map.getBoundingBox().getLonWest())/3;
            double hp = (map.getBoundingBox().getLatSouth()-map.getBoundingBox().getLatNorth())/3;
            //Log.d("current box",fetchedGrid.toString());
            //Log.d("screen",map.getBoundingBox().toString());

            fetchedGrid = new BoundingBox( //current screen plus a screen in all directions
                    Math.max(Math.min(map.getBoundingBox().getLatNorth() - hp,TileSystem.maxLatitude),TileSystem.minLatitude),
                    Math.max(Math.min(map.getBoundingBox().getLonEast() + wp,TileSystem.maxLongitude),TileSystem.minLongitude),
                    Math.max(Math.min(map.getBoundingBox().getLatSouth() + hp,TileSystem.maxLatitude),TileSystem.minLatitude),
                    Math.max(Math.min(map.getBoundingBox().getLonWest() - wp,TileSystem.maxLongitude),TileSystem.minLongitude));
            //Log.d("new box",fetchedGrid.toString());
            if(fetchedGrid.getLonEast()<fetchedGrid.getLonWest())fetchedGrid =
                    new BoundingBox(fetchedGrid.getLatNorth(),fetchedGrid.getLonWest(),
                            fetchedGrid.getLatSouth(),fetchedGrid.getLonEast());
            if(fetchedGrid.getLatNorth()<fetchedGrid.getLatSouth())fetchedGrid =
                    new BoundingBox(fetchedGrid.getLatSouth(),fetchedGrid.getLonEast(),
                            fetchedGrid.getLatNorth(),fetchedGrid.getLonWest());
            //Log.d("altered box",fetchedGrid.toString());

        }else{
            Log.d("not loading","map not visible");
        }
        if(NavA&&findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE){
            BoundingBox screen = new BoundingBox(nMap.coordinateForPoint(new Point(0,0)).getLatitude(),
                    nMap.coordinateForPoint(new Point(displayMetrics.widthPixels-20,0)).getLongitude(),
                    nMap.coordinateForPoint(new Point(0,displayMetrics.heightPixels-20)).getLatitude(),
                    nMap.coordinateForPoint(new Point(0,0)).getLongitude()
            );
            if (currentNavZoom==(int)nMap.getCamera().getZoom() && screen.getLonWest() > fetchedGrid.getLonWest() &&
                    screen.getLonEast()< fetchedGrid.getLonEast() &&
                    screen.getLatNorth()< fetchedGrid.getLatNorth() &&
                    screen.getLatSouth() > fetchedGrid.getLatSouth()

            ) {
                //Log.d("in screen", "Dont fetch");
                return; //if we are still inside the fetched envelope then dont bother refetching
            }
            currentNavZoom=(int)nMap.getCamera().getZoom();
            //Log.d("current box",fetchedGrid.toString());
            //Log.d("screen",screen.toString());
            //Log.d("camera",nMap.getCamera().getTarget().toString());
            fetchedGrid = new BoundingBox(Math.max(Math.min(nMap.coordinateForPoint(new Point(0,-1000)).getLatitude(),TileSystem.maxLatitude),TileSystem.minLatitude),
                    Math.max(Math.min(nMap.coordinateForPoint(new Point(displayMetrics.widthPixels+1000,0)).getLongitude(),TileSystem.maxLongitude),TileSystem.minLongitude),
                    Math.max(Math.min(nMap.coordinateForPoint(new Point(0,displayMetrics.heightPixels+1000)).getLatitude(),TileSystem.maxLatitude),TileSystem.minLatitude),
                    Math.max(Math.min(nMap.coordinateForPoint(new Point(-1000,0)).getLongitude(),TileSystem.maxLongitude),TileSystem.minLongitude)
            );
            if(fetchedGrid.getLonEast()<fetchedGrid.getLonWest())fetchedGrid.setLonEast(TileSystem.maxLongitude);//fix for the dateline
//            fetchedGrid = TileSystem.boundsFromCenter(nMap.getCamera().getTarget().getLatitude(),
//                    nMap.getCamera().getTarget().getLongitude(),
//                    (int)nMap.getCamera().getZoom(),displayMetrics.heightPixels+1000,displayMetrics.widthPixels+1000);
        }
        isFetching = true;
        //Log.d("new box",fetchedGrid.toString());
        //Log.d("toc1",""+(System.currentTimeMillis()-tic));
        ArrayList<String> locs = mDbHelper.getLocations(map.getBoundingBox());
        if(locs.size()<490){
            locs = mDbHelper.getLocations(fetchedGrid);
        }else{
            fetchedGrid = map.getBoundingBox();
        }

        //Log.d("toc2",""+(System.currentTimeMillis()-tic));
        for (NMSMarker m :navionicsMarkers)m.setMap(null);//remove all the old markers from the map
        navionicsMarkers.clear();
        //Log.d("loading locs",locs.size()+":"+fetchedGrid.toString());
        markers_clusters.getItems().clear();
        markers_clusters.invalidate();
        //boolean foundCenter = false;
        //Log.d("toc3",""+(System.currentTimeMillis()-tic));
        int maxMarkers = 500;
        for(int i=locs.size()-1;i>=0;i--){
            final String[] mark = ((String)locs.get(i)).split(":");
            //Log.d(mark[3],mark[2]);
            if(Symbols.containsKey(mark[2])) {
                if(filter.contains(mark[2]))continue;//dont add filtered markers

                if(findViewById(R.id.map).getVisibility()==View.VISIBLE) {
                    GeoPoint p = new GeoPoint(Double.parseDouble(mark[0]), Double.parseDouble(mark[1]));
                    if(map==null)return;
                    Marker startMarker;
                    try{
                        startMarker = new Marker(map);
                    }catch (Exception e){
                        e.printStackTrace();
                        return;
                    }
                    startMarker.setIcon(Symbols.get(mark[2]));
                    startMarker.setPosition(p);
                    startMarker.setId(mark[3]);
                    startMarker.setImage(Symbols.get(mark[2]));

                    startMarker.setTextLabelBackgroundColor(Color.TRANSPARENT);
                    startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    items.add(startMarker);
                    startMarker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker, MapView mapView) {
                            try {
                                InfoWindow.closeAllInfoWindowsOn(map);
                                showIW(marker.getId(),marker.getPosition().getLatitude(),marker.getPosition().getLongitude(),((BitmapDrawable)marker.getImage()).getBitmap());

                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            return true;
                        }
                    });
                    if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("icon_cluster",false) && map.getZoomLevelDouble()<MIN_CLUSTER_ZOOM ) {
                        markers_clusters.add(startMarker);
                    }else{
                        if(maxMarkers-->0){
                            map.getOverlays().add(startMarker);
                        }
                        else {
                            i = locs.size()+1;
                        }
                    }
                    if(pleaseOpen.equals(startMarker.getId())){
                        InfoWindow.closeAllInfoWindowsOn(map);
                        showIW(startMarker.getId(),startMarker.getPosition().getLatitude(),startMarker.getPosition().getLongitude(),((BitmapDrawable)startMarker.getImage()).getBitmap());
                        pleaseOpen = "";
                    }
                }
                if(NavA&&findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE){
                    if(maxMarkers-->0) {
                        NMSLocationCoordinate2D markerLocation = new NMSLocationCoordinate2D(Double.parseDouble(mark[0]), Double.parseDouble(mark[1]));
                        NMSMarker marker = NMSMarker.markerWithPosition(markerLocation);
                        marker.setImage(((BitmapDrawable) Objects.requireNonNull(Symbols.get(mark[2]))).getBitmap());
                        marker.setMap(nMap);
                        marker.setAnchor(CGPoint.CGPointMake(0.5f, 0.5f));
                        marker.setOpacity(0.9f);
                        marker.setTitle(mark[3]);
                        navionicsMarkers.add(marker);
                    }
                }

            }

        }
        //Log.d("toc4",""+(System.currentTimeMillis()-tic));
        //if(maxMarkers<=0){
        //displayMessage(getResources().getString(R.string.limiting_icons));
        //}
        //if(foundCenter)showInfoWIndow = false;
        markers_clusters.invalidate();
        Log.d("loading ",locs.size()+" markers");
        isFetching = false;
        //Log.d("toc5",""+(System.currentTimeMillis()-tic));
    }
    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
    private void showIW(final String id, Double lat, Double lon, Bitmap pic){
        ArrayList<String> locData = mDbHelper.getLocationData(id);
        if(locData.isEmpty()){
            displayMessage("No data found! Try a sync");
            Toast.makeText(this,"No Data Found!",Toast.LENGTH_LONG).show();
            return;
        }
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View popupView = inflater.inflate(getResources().getConfiguration().orientation == 1 ? R.layout.popup_main : R.layout.popup_main_landscape, null);
        int width = context.getResources().getDisplayMetrics().widthPixels-6;
        width= Math.min(width, 1000);
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        try {
            ((WebView) popupView.findViewById(R.id.webDesc)).loadDataWithBaseURL(null, prettyHtml(locData.get(2)), "text/html; charset=utf-8", "UTF-8", null);
        }catch (Exception e){
            e.printStackTrace();
        }
        ((TextView) popupView.findViewById(R.id.bubble_title)).setText(locData.get(0));
        ((TextView) popupView.findViewById(R.id.bubble_oldID)).setText(locData.get(3));
        final String s = locData.get(1) + ",   " + lat + ", " + lon+"\n"+locData.get(5);
        ((TextView) popupView.findViewById(R.id.bubble_subdescription)).setText(s);
        infoW = new PopupWindow(popupView,width , height, true);
        infoW.showAtLocation(map, Gravity.CENTER, 0, 0);
        infoWP = new GeoPoint(lat,lon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            infoW.setWindowLayoutType(WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL);
        }
        popupView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            v.setVisibility(View.GONE);
                        }catch(Exception e){
                            e.printStackTrace();
                            displayMessage(e.getMessage());
                        }
                    }
                }, 800);
            }
        });
        ((ImageView)  popupView.findViewById(R.id.bubble_image)).setImageBitmap(Bitmap.createScaledBitmap(pic, iconSize, iconSize, true));

        popupView.findViewById(R.id.bubble_add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbHelper.addComment(id, ((EditText) popupView.findViewById(R.id.bubble_comments_add_comment)).getText().toString(),FirebaseAuth.getInstance().getCurrentUser()==null?"":FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                ArrayList<String> cc = mDbHelper.getComments(id);
                StringBuilder co = new StringBuilder();
                for (String value : cc) {
                    String[] cs = value.split(";");
                    String byline = "<p>"+cs[1]+"@"+cs[0]+"</p>";
                    cs[0] = "";
                    cs[1] = "";
                    co.append(Html.fromHtml(TextUtils.join("", cs) + byline+"<hr>"));
                }
                downloadsLeft+=DOWNLOADS_COMMENT_REWARD;
                ((TextView) popupView.findViewById(R.id.bubble_comments)).setText(co.toString());
                ((TextView) popupView.findViewById(R.id.bubble_comments)).setMovementMethod(LinkMovementMethod.getInstance());
                popupView.findViewById(R.id.bubble_add_comment_container).setVisibility(View.GONE);
            }
        });

        ArrayList<String> cc = mDbHelper.getComments(id);
        StringBuilder co = new StringBuilder();
        for (String value : cc) {
            String[] cs = value.split(";");
            String byline = "<p>"+cs[1]+"@"+cs[0]+"</p>";
            cs[0] = "";
            cs[1] = "";
            co.append(Html.fromHtml(TextUtils.join("", cs) + byline+"<hr>"));
        }
        ((TextView) popupView.findViewById(R.id.bubble_comments)).setText(co.toString());
        ((TextView) popupView.findViewById(R.id.bubble_comments)).setMovementMethod(LinkMovementMethod.getInstance());


        popupView.findViewById(R.id.bubble_firstImage).setVisibility(View.GONE);
        popupView.findViewById(R.id.bubble_secondImage).setVisibility(View.GONE);
        popupView.findViewById(R.id.bubble_thirdImage).setVisibility(View.GONE);

        String pp = locData.get(4);
        String[] photos = pp.split(":");
        if (photos.length > 0) {
            File f = new File(getFilesDir() + "/.image" + "/" + photos[0]);
            if (f.exists()) { //already downloaded
                Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                ((ImageView) popupView.findViewById(R.id.bubble_firstImage)).setImageBitmap(myBitmap);
            } else { //need to download image first
                getPicture(f.getPath(), (ImageView) popupView.findViewById(R.id.bubble_firstImage));
            }
            popupView.findViewById(R.id.bubble_firstImage).setVisibility(View.VISIBLE);
        }
        if (photos.length > 1) {
            File f = new File(getFilesDir() + "/.image" + "/" + photos[1]);
            if (f.exists()) { //already downloaded
                Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                ((ImageView) popupView.findViewById(R.id.bubble_secondImage)).setImageBitmap(myBitmap);
            } else { //need to download image first
                getPicture(f.getPath(), (ImageView) popupView.findViewById(R.id.bubble_secondImage));
            }
            popupView.findViewById(R.id.bubble_secondImage).setVisibility(View.VISIBLE);
        }
        if (photos.length > 2) {
            File f = new File(getFilesDir() + "/.image" + "/" + photos[2]);
            if (f.exists()) { //already downloaded
                Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
                ((ImageView) popupView.findViewById(R.id.bubble_thirdImage)).setImageBitmap(myBitmap);
            } else { //need to download image first
                getPicture(f.getPath(), (ImageView) popupView.findViewById(R.id.bubble_thirdImage));
            }
            popupView.findViewById(R.id.bubble_thirdImage).setVisibility(View.VISIBLE);
        }
    }
    private String prettyHtml(String data){
        Log.d("premuim","."+premuim);
        String[] pc = premuim.trim().split(",");
        String s = "<style>body{background:#F5F2EB;font-family: serif;} img{max-width:100%;} ";
        if(!premuim.trim().isEmpty()) for (String value : pc) {
            s += " .zwPremium." + value + "{display:block;} .pblurb." + value + "{display:none}";
        }
        s = s +" .zwPremium{background:lavender;padding:1px;width:auto;min-height:20px;display:none;} .pblurb {background: beige;padding:1px;width:auto;min-height:20px;}</style>"+data;
        Log.d("desc",s);
        return s;
    }

    private void requestWritePermission(boolean force) {
        // define permission to request
        Log.d("##########","getting write permission:"+ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE));
        if(!getSharedPreferences("MyPrefsFile", MODE_PRIVATE).contains("perms")&& !force) return;
        if (findViewById(R.id.permission1)!=null) ((CheckBox)findViewById(R.id.permission1)).setChecked(true);
        try {
            CurrentMaptype = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluBase.sqlite";
            satDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluBase.sqlite";
            streetDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluStreet.sqlite";
            overlayDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluOverlay.sqlite";
            if(!getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].canWrite())throw new Exception();
            Log.d("##########","using external dbs");
        }catch (Exception e){
            //displayMessage("Issue accessing external storage. Using internal storage.");
            Log.d("##########","using internal dbs");
            CurrentMaptype =  getFilesDir().getAbsolutePath()+"/ZuluBase.sqlite";
            satDatabase = getFilesDir().getAbsolutePath()+"/ZuluBase.sqlite";
            streetDatabase = getFilesDir().getAbsolutePath()+"/ZuluStreet.sqlite";
            overlayDatabase = getFilesDir().getAbsolutePath()+"/ZuluOverlay.sqlite";
        }
        try {
            Log.d("##########","got write permission");

            File f = new File(satDatabase);//
            Log.d("dirs",satDatabase+f.canWrite());
            if(!f.exists()){
                copyAssets();
                changeMap(streetDatabase);
                changeMap(satDatabase);
            }

            Log.d("##########","gots read permission");
            requestReadPermission();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void copyAssets() {
        Log.d("copying assets:",satDatabase);
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        for(String filename : files) {
            if(!filename.contains("sqlite"))continue;
            InputStream in;
            OutputStream out;
            String outDir = "";
            try{
                if(!getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].canWrite()){
                    Log.d("external fail!",getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].canWrite()+"");
                    throw new Exception();
                }
                outDir = getExternalFilesDirs(null)[getExternalFilesDirs(null).length-1].getAbsolutePath() ;
                File testfile = new File(outDir, filename);
                if(testfile.exists()){
                    Log.d("Cant copy File!!!!","File Exists!!!!"+testfile.canWrite());
                    return;
                }
                Log.d("perm","using external!!!!"+outDir);
            }catch (Exception e){
                e.printStackTrace();
                Log.d("perm","using internal!!!!");
                outDir = getFilesDir().getAbsolutePath() ;
            }
            try {
                in = assetManager.open(filename);
                File outFile = new File(outDir, filename);
                Log.e("copying", " " + filename);
                Log.e("copying", " " + outFile.getAbsolutePath());
                Log.e("copying", " " + outFile.canWrite());
                out = new FileOutputStream(outFile);
                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    private void requestReadPermission() {
        String[] reqPermission = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE};
        int requestCode = 2;
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            //Log.d("##########","getting read permission");
            //requestLocPermission();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, reqPermission, requestCode);
        }
    }
    private void requestLocPermission() {
        // define permission to request
        String[] reqPermission = new String[] { Manifest.permission.ACCESS_FINE_LOCATION };
        int requestCode = 2;
        // For API level 23+ request permission at runtime
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                reqPermission[0]) == PackageManager.PERMISSION_GRANTED) {
            if( myLocation==null){
                myLocation = new MyLocationNewOverlay(new GpsMyLocationProvider(this.getApplicationContext()),map);
                myLocation.enableMyLocation();
                map.getOverlays().add(myLocation);
            }
            if (findViewById(R.id.permission2)!=null)((CheckBox)findViewById(R.id.permission2)).setChecked(true);
        } else {
            // request permission
            ActivityCompat.requestPermissions(MainActivity.this, reqPermission, requestCode);
        }
    }

    public void onResume(){
        super.onResume();
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
        loadPreferences();
        //((Toolbar)findViewById(R.id.toolbar)).setTitle("");
        loadIconData();
        //fetchedGrid.set(0,0,0,0);
        loadIcons();
        handleIntent(getIntent());
        if(NavA){
            NMSMapSettingsEdit naviEditor = new NMSMapSettingsEdit(nMap.getSettings());
            naviEditor.setMapMode(NMSEnum.NMSMapMode.NMSMapModeSonarCharts);
        }


    }

    public void onPause(){
        super.onPause();

        savePreferences();
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    public void onStop() {
        super.onStop();
        savePreferences();
    }

    private void savePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if((int)map.getMapCenter().getLatitude()!=0)editor.putString("lat",map.getMapCenter().getLatitude()+"");
        if((int)map.getMapCenter().getLongitude()!=0)editor.putString("lon",map.getMapCenter().getLongitude()+"");
        //Log.d("center saved",map.getMapCenter().toString());
        editor.putString("zed",map.getZoomLevelDouble()+"");
        if(NavA&&findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE){
            //Log.d("saving location",nMap.getCamera().getTarget().getLongitude()+","+nMap.getCamera().getTarget().getLatitude());
            if(nMap.getCamera().getTarget().getLatitude()!=0)editor.putString("lat",nMap.getCamera().getTarget().getLatitude()+"");
            if(nMap.getCamera().getTarget().getLongitude()!=0)editor.putString("lon",nMap.getCamera().getTarget().getLongitude()+"");
            if(nMap.getCamera().getZoom()!=0)editor.putString("zed",nMap.getCamera().getZoom()+"");
            editor.putString("nMapType",nMap.getSettings().getMapMode().name());
        }
        //editor.putString("username",userName);
        //editor.putString("userID",userID);
        editor.putString("token",token);
        editor.putString("premuim",premuim);
        editor.putStringSet("filters",filter);
        if(currentLang!=null)editor.putString("language",currentLang.getLanguage());
        if(currentLang!=null)editor.putString("languagec",currentLang.getCountry());
        editor.putString("mapType",CurrentMaptype);
        editor.putInt("downloads",downloadsLeft);
        editor.apply();
    }

    @SuppressLint("SimpleDateFormat")
    public void loadPreferences(){
        //Log.d("nav","loading prefs");

        //if(getExternalFilesDirs(null)==null||getExternalFilesDirs(null).length==0)requestWritePermission();
        try {
            CurrentMaptype = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluBase.sqlite";
            satDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluBase.sqlite";
            streetDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluStreet.sqlite";
            overlayDatabase = getExternalFilesDirs(null)[getExternalFilesDirs(null).length - 1].getAbsolutePath() + "/ZuluOverlay.sqlite";
        }catch (Exception e){
            //displayMessage("Issue accessing external storage. Using internal storage.");
            CurrentMaptype =  getFilesDir().getAbsolutePath()+"/ZuluBase.sqlite";
            satDatabase = getFilesDir().getAbsolutePath()+"/ZuluBase.sqlite";
            streetDatabase = getFilesDir().getAbsolutePath()+"/ZuluStreet.sqlite";
            overlayDatabase = getFilesDir().getAbsolutePath()+"/ZuluOverlay.sqlite";
        }

        final SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        GeoPoint g= new GeoPoint(Double.parseDouble(sharedPreferences.getString("lat","-25")),Double.parseDouble(sharedPreferences.getString("lon","145")));
        if(g.getLongitude()==0 || g.getLatitude()==0)g = new GeoPoint(-25.0,145.0);
        map.getController().setCenter(g);
        map.getController().setZoom(Double.parseDouble(sharedPreferences.getString("zed","5")));
        //Log.d("nav","loadmoving");
        final double lat =  Double.parseDouble(sharedPreferences.getString("lat","-25"));
        final double lon = Double.parseDouble(sharedPreferences.getString("lon","145"));
        WebView wv = findViewById(R.id.onlineWeb);
        wv.getSettings().setDomStorageEnabled(true);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setAllowFileAccess(true);
        wv.getSettings().setAllowContentAccess(true);
        wv.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        wv.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        wv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        //wv.setWebContentsDebuggingEnabled(true);
        wv.requestFocus(View.FOCUS_DOWN);
        wv.setVisibility(View.GONE);
        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call() {
                wv.setVisibility(View.VISIBLE);
                return null;
            }
        }).execute();
        WebChromeClient wvc = new WebChromeClient() {
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    Log.d("progress","compelte");
                } else {
                    Log.d("progress",newProgress+"");
                }
                super.onProgressChanged(view, newProgress);
            }

            //For Android API < 11 (3.0 OS)
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                openImageChooserActivity();
            }

            //For Android API >= 11 (3.0 OS)
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                openImageChooserActivity();
            }

            //For Android API >= 21 (5.0 OS)
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                        Log.e("TAG", "Unable to create Image File", ex);
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, 7);

                return true;
            }
        };

        wv.setWebChromeClient(wvc);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("webview",url);
                if (url.startsWith("https://play.google.com")) {
                    try {
                        wv.evaluateJavascript("document.getElementsByClassName(\"checkhide\")[0].checked = false;",null);
                        wv.evaluateJavascript("map.getCenter().lat+\"/\"+map.getCenter().lng+\"/\"+map.getZoom()", value -> {
                            try {
                                value = value.replaceAll("\"", "");
                                Log.d("hi", value);
                                GeoPoint g = new GeoPoint(Double.parseDouble(value.split("/")[0]), Double.parseDouble(value.split("/")[1]));
                                if (g.getLongitude() == 0 || g.getLatitude() == 0)
                                    g = new GeoPoint(-25.0, 145.0);
                                map.getController().setCenter(g);
                                map.getController().setZoom(2 + Double.parseDouble(value.split("/")[2]));
                            }catch (Exception e){e.printStackTrace();}
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.d("webview2",url);
                    wv.setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            loadIconData();
                            fetchedGrid = new BoundingBox(0,0,0,0);
                            loadIcons();
                        }
                    }, 500);
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            loadIconData();
                            fetchedGrid = new BoundingBox(0,0,0,0);
                            loadIcons();
                        }
                    }, 3000);
                    return true;
                }

                if (url.startsWith("https://www.zuluwaterways.com/views/close.html")) {
                    if(url.startsWith("https://www.zuluwaterways.com/views/close.html?")){
                        Log.d("webview3","close");
                        String[] sp = url.split("\\?");
                        String[] pp = sp[sp.length-1].split("/");
                        Log.d("webview4",""+pp.length);

                        if(pp.length!=3){
                            Log.d("webview5","close");
                            return super.shouldOverrideUrlLoading(view, url);
                        }
                        Log.d("webview6",""+pp[2]);
                        GeoPoint p = new GeoPoint(Double.parseDouble(pp[0]),Double.parseDouble(pp[1]));
                        Log.d("webview7",""+p.toString());
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(Integer.parseInt(pp[2])==0)downloadHybridTiles(p);
                                if(Integer.parseInt(pp[2])==1)downloadStreetTiles(p);
                                if(Integer.parseInt(pp[2])==2){changeMap("navionics");downloadNavionics(null);}
                                if(Integer.parseInt(pp[2])==3)downloadOSMTiles(p);
                                if(Integer.parseInt(pp[2])==4)downloadARCGISTiles(p);
                            }
                        }, 500);
                        wv.setVisibility(View.GONE);
                        return true;
                    }else{
                        try {
                            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                                Log.d("hi", "login?" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                wv.evaluateJavascript("closeModal();", null);
                                wv.evaluateJavascript("currentUser=\"" + FirebaseAuth.getInstance().getCurrentUser().getEmail() + "\";", null);
                                wv.evaluateJavascript("currentUserName=\"" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + "\";", null);
                                wv.evaluateJavascript("currentUserId=\"" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\";", null);
                                wv.evaluateJavascript("document.getElementById(\"loginButton\").innerHTML=currentUserName;", null);
                                wv.evaluateJavascript("document.getElementById(\"signOutButton\").style.display=\"\";", null);
                            } else {
                                wv.evaluateJavascript("closeModal()", null);
                                showLogin();
                                Log.d("hi", "nologin?");
                            }
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
                defaultBrowser.setData(browserIntent.getData());
                startActivity(defaultBrowser);

                return true;
            }
            private boolean handleUri(final Uri uri) {

                if (uri != null) {

                    Log.i("TAG", "Uri =" + uri);

                    //return true;
                }

                return false;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("loading","finished");
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    wv.evaluateJavascript(        "currentUser=\""+FirebaseAuth.getInstance().getCurrentUser().getEmail()+"\";",null);
                    wv.evaluateJavascript(        "currentUserName=\""+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"\";" ,null);
                    wv.evaluateJavascript(        "currentUserId=\""+FirebaseAuth.getInstance().getCurrentUser().getUid()+"\";",null);
                    wv.evaluateJavascript("document.getElementById(\"loginButton\").innerHTML=currentUserName;",null);
                    wv.evaluateJavascript("document.getElementById(\"signOutButton\").style.display=\"\";",null);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                //view.loadUrl("about:blank");
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            Log.d("hi", "login?"+FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            wv.evaluateJavascript("closeModal();",null);
            wv.evaluateJavascript(        "currentUser=\""+FirebaseAuth.getInstance().getCurrentUser().getEmail()+"\";",null);
            wv.evaluateJavascript(        "currentUserName=\""+FirebaseAuth.getInstance().getCurrentUser().getDisplayName()+"\";" ,null);
            wv.evaluateJavascript(        "currentUserId=\""+FirebaseAuth.getInstance().getCurrentUser().getUid()+"\";",null);
            wv.evaluateJavascript("document.getElementById(\"loginButton\").innerHTML=currentUserName;",null);
            wv.evaluateJavascript("document.getElementById(\"signOutButton\").style.display=\"\";",null);
        }
        //Log.d("nav",lat+","+lon);
        //Log.d("loadinglang",sharedPreferences.getString("language","en")+","+sharedPreferences.getString("languagec",""));
        currentLang = new Locale(sharedPreferences.getString("language","en"),sharedPreferences.getString("languagec",""));
        setLocale(this,currentLang);
        if(NavA)nMap.moveToLocation(new NMSLocationCoordinate2D(lat,lon),
                (float)Double.parseDouble(sharedPreferences.getString("zed","5")) ,false);
        //Log.d("loading location",nMap.getCamera().getTarget().getLongitude()+","+nMap.getCamera().getTarget().getLatitude());
        //userName = sharedPreferences.getString("username","");
        //token = sharedPreferences.getString("token","");
        //userID = sharedPreferences.getString("userID","");
        premuim = sharedPreferences.getString("premuim","");
        //if(!userName.trim().isEmpty())optionsMenu.getItem(2).setTitle("Sign Out");
        CurrentMaptype = "";
        String mt = sharedPreferences.getString("mapType",satDatabase);
        if( mt.trim().isEmpty()) mt = satDatabase;
        changeMap(mt);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if(NavA) nMap.moveToLocation(new NMSLocationCoordinate2D(lat,lon),(float)Double.parseDouble(sharedPreferences.getString("zed","5")),true);
                if(NavA)nMap.moveToCameraPosition(NMSCameraPosition.cameraWithLatitude(lat,lon, (float)Double.parseDouble(sharedPreferences.getString("zed","5"))),true);
                loadIcons();
            }
        }, 1000);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadIcons();
            }
        }, 1500);

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadIcons();
            }
        }, 2000);
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadIcons();
            }
        }, 3000);
        downloadsLeft = sharedPreferences.getInt("downloads", 100);
        mScaleBarOverlay.setUnitsOfMeasure( PreferenceManager.getDefaultSharedPreferences(this).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[0])? ScaleBarOverlay.UnitsOfMeasure.metric:
                PreferenceManager.getDefaultSharedPreferences(this).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[1])?ScaleBarOverlay.UnitsOfMeasure.imperial:
                        ScaleBarOverlay.UnitsOfMeasure.nautical);
        if(!sharedPreferences.getString("downloadsMonth", "").equals((new SimpleDateFormat("MMM",Locale.ENGLISH)).format(new Date()))){ //new month so give users more downloads
            if(res==null)res=new StringBuilder();
            res.delete(0,res.length());
            Log.d("month!!!", ":"+sharedPreferences.getString("downloadsMonth", ""));
            try {
                new HTTPGetRequest(res, new Callable<String>() {
                    public String call() {
                        Log.d("timeresults", ":"+res.toString());
                        if(res.length()>15||res.toString()=="")return null;
                        try {
                            String serverDate = (new SimpleDateFormat("MMM", Locale.ENGLISH)).format(new Date(Long.parseLong(res.toString())));
                            if (serverDate.equals((new SimpleDateFormat("MMM", Locale.ENGLISH)).format(new Date()))) {
                                Log.d("######NEW MONTH#######", "adding new data!!!!!!!");
                                downloadsLeft = Math.max(DOWNLOADS_PER_MONTH, downloadsLeft);
                                sharedPreferences.edit().putString("downloadsMonth", serverDate).apply();
                                sharedPreferences.edit().putInt("downloads", downloadsLeft).apply();
                            } else {
                                Log.d("######not! MONTH#######", "adding new data!!!!!!!");
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return null;
                    }
                }).execute("https://www.zuluwaterways.com/offline/time/", "testLogin","");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        try {
            if(NavA) {
                NMSMapSettingsEdit editor = new NMSMapSettingsEdit(nMap.getSettings());
                editor.setMapMode(sharedPreferences.getString("nMapType", "NMSMapModeDefault").equals("NMSMapModeDefault") ? NMSEnum.NMSMapMode.NMSMapModeDefault : NMSEnum.NMSMapMode.NMSMapModeSonarCharts);
                editor.setDistanceUnit(PreferenceManager.getDefaultSharedPreferences(this).getString("units", "Metric").equals(getResources().getStringArray(R.array.units)[0]) ? NMSEnum.NMSDistanceUnit.NMSDistanceUnitKilometers :
                        PreferenceManager.getDefaultSharedPreferences(this).getString("units", "Metric").equals(getResources().getStringArray(R.array.units)[1]) ? NMSEnum.NMSDistanceUnit.NMSDistanceUnitStatuteMiles :
                                NMSEnum.NMSDistanceUnit.NMSDistanceUnitNauticalMiles);
                editor.setDepthUnit(PreferenceManager.getDefaultSharedPreferences(this).getString("units", "Metric").equals(getResources().getStringArray(R.array.units)[0]) ? NMSEnum.NMSDepthUnit.NMSDepthUnitMeters :
                        PreferenceManager.getDefaultSharedPreferences(this).getString("units", "Metric").equals(getResources().getStringArray(R.array.units)[1]) ? NMSEnum.NMSDepthUnit.NMSDepthUnitFeet :
                                NMSEnum.NMSDepthUnit.NMSDepthUnitFathoms);
                editor.setEasyViewEnabled(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("nav_easy", false));
                editor.setSpeedUnit(PreferenceManager.getDefaultSharedPreferences(this).getString("units", "Metric").equals(getResources().getStringArray(R.array.units)[0]) ? NMSEnum.NMSSpeedUnit.NMSSpeedUnitKPH :
                        PreferenceManager.getDefaultSharedPreferences(this).getString("units", "Metric").equals(getResources().getStringArray(R.array.units)[1]) ? NMSEnum.NMSSpeedUnit.NMSSpeedUnitMPH :
                                NMSEnum.NMSSpeedUnit.NMSSpeedUnitKnots);

                editor.setShallowDepthLimit(Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("shallow_depth", "10")));
                nMap.setSettings(editor);
            }
        }catch (Exception e){
            //editor.setShallowDepthLimit(10);
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("shallow_depth","10").apply();
        }
        try{
            MIN_CLUSTER_ZOOM =Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("minCluster", "10"));
        }catch(Exception e){
            MIN_CLUSTER_ZOOM =10;
            PreferenceManager.getDefaultSharedPreferences(this).edit().putString("minCluster","10").apply();
        }

        int ii = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("iconSize","12"));
        iconSize = Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) / ii;
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.marker_cluster);
        if(markers_clusters!=null)markers_clusters.setIcon(bm.createScaledBitmap(bm, iconSize, iconSize, false));

        //Log.d("saving Dir",ContextCompat.getExternalFilesDirs(this,null).toString());
        filter = sharedPreferences.getStringSet("filters",Collections.<String>emptySet());

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                long lsy = sharedPreferences.getLong("lastSync",0);
                if(lsy!=0 &&lsy<System.currentTimeMillis()-7*24*60*60*1000){
                    (new Handler()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            if(mDbHelper.SyncData(token, FirebaseAuth.getInstance().getCurrentUser()==null?"Dogify":FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {//do a sneaky sync
                                updateDB();
                            }//displayMessage("Its been a while since your last sync. Download the latest updates when you get the chance!");
                            sharedPreferences.edit().putLong("lastSync",System.currentTimeMillis()-4*24*60*60*1000).apply();
                        }
                    }, 60000);
                }
            }
        }, 200000);


        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("screenOn",false))getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void onBackPressed(){
        Log.d("back",""+(new Date()).toString());
        if(findViewById(R.id.onlineWeb).getVisibility()==View.VISIBLE){
            WebView wv = findViewById(R.id.onlineWeb);
            wv.evaluateJavascript("map.getCenter().lat+\"/\"+map.getCenter().lng+\"/\"+map.getZoom()", value -> {
                value = value.replaceAll("\"", "");
                if(value.length()!=3)return;
                GeoPoint g = new GeoPoint(0,0);
                try {
                    g = new GeoPoint(Double.parseDouble(value.split("/")[0]), Double.parseDouble(value.split("/")[1]));
                }catch (Exception e){e.printStackTrace();}
                if (g.getLongitude() == 0 || g.getLatitude() == 0)
                    g = new GeoPoint(-25.0, 145.0);
                map.getController().setCenter(g);
                try {
                    map.getController().setZoom(2 + Double.parseDouble(value.split("/")[2]));
                }catch (Exception e){e.printStackTrace();}
            });
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    loadIconData();
                    fetchedGrid = new BoundingBox(0,0,0,0);
                    loadIcons();
                }
            }, 500);
            handler.postDelayed(new Runnable() {
                public void run() {
                    loadIconData();
                    fetchedGrid = new BoundingBox(0,0,0,0);
                    loadIcons();
                }
            }, 3000);
            findViewById(R.id.onlineWeb).setVisibility(View.GONE);
            return;
        }
        if(InfoWindow.getOpenedInfoWindowsOn(map).isEmpty()){
            super.onBackPressed();
            return;
        }
        InfoWindow.closeAllInfoWindowsOn(map);
        if(infoW!=null)infoW.dismiss();
        Log.d("back",""+(new Date()).toString());
    }


    public void moreOptions(final View v) {
        //Log.d("info","more info pressed");
        try {
            View spacer = findViewById(R.id.long_press_spacer);
            int[] location = new int[2];
            v.getLocationOnScreen(location);
            CoordinatorLayout.LayoutParams l = (CoordinatorLayout.LayoutParams) spacer.getLayoutParams();
            l.setMargins(location[0], location[1]+40, 0, 0);
            spacer.setLayoutParams(l);
            final PopupMenu p = new PopupMenu(MainActivity.this, spacer);
            p.getMenuInflater().inflate(R.menu.marker_options, p.getMenu());
            p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    try {
                        if (item == null) return false;
                        if (item.getTitle().equals(getResources().getString(R.string.hybrid_map)))downloadHybridTiles(null);
                        if (item.getTitle().equals(getResources().getString(R.string.sat_map))) downloadSatTiles(null);
                        if (item.getTitle().equals(getResources().getString(R.string.hd_satellite_map))) downloadHDTiles(null);
                        if (item.getTitle().equals(getResources().getString(R.string.street_map)))downloadStreetTiles(null);
                        //if (item.getTitle().equals(getResources().getString(R.string.bing_map)))downloadBingTiles(null);
                        //if (item.getTitle().equals(getResources().getString(R.string.arcgis_map))) downloadARCGISTiles(null);
                        if (item.getTitle().equals(getResources().getString(R.string.navionics_charts))) {
                            changeMap("navionics");
                            if( NavionicsMobileServices.isNavionicsUserLoggedIn()){
                                NavionicsMobileServices.enableDownloadAreaSelector();
                                findViewById(R.id.navionics_download).setVisibility(View.VISIBLE);
                                findViewById(R.id.close).setVisibility(View.VISIBLE);
                            }else{
                                NavionicsMobileServices.navionicsUser();
                            }
                            if(infoW!=null && infoW.isShowing())infoW.dismiss();
                        }
                        if (item.getTitle().equals(getResources().getString(R.string.download_osm)))downloadOSMTiles(null);
                        if (item.getTitle().equals(getResources().getString(R.string.edit_marker_online))) gotoOnline();
                        if(item.getTitle().equals(getResources().getString(R.string.download_region)))getCode(null);
                        if (item.getTitle().equals(getResources().getString(R.string.share)))shareThisMarker(infoWP);
                        return false;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            });
            p.getMenu().getItem(0).setTitle(getResources().getString(R.string.download)+" ("+downloadsLeft+")");

            if (findViewById(R.id.bubble_oldID) !=null && ((TextView) findViewById(R.id.bubble_oldID)).getText().toString().trim().isEmpty())
                p.getMenu().getItem(p.getMenu().size() - 1).setVisible(true);

            p.show();
        }catch (Exception e){
            e.printStackTrace();
            displayMessage(e.getMessage());
        }
    }
    private void downloadOSMTiles(GeoPoint p) {
        fetchedGrid=new BoundingBox(0,0,0,0);
        changeMap("OSM");
        if(infoW!=null && infoW.isShowing()){
            p = infoWP;
            infoW.dismiss();
        }
        final GeoPoint fp = p;
        if(p==null)return;

        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call() {
                findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.copyWriteMessage)).setText(R.string.mapbox_copy);
                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("altTiles",false)) {
//                    downloadZTile(fp, "osm", 8, 0, "png", overlayDatabase, "sat", true);
//                    downloadZTile(fp, "osm", 15, 1, "png", overlayDatabase, "sat", true);
//                    downloadZTile(fp, "osm", 12, 1, "png", overlayDatabase, "sat", true);
                    downloadTile(fp,"https://t1.openseamap.org/seamark/",".png",8,0,"png",overlayDatabase,"sat",true);
                    downloadTile(fp,"https://t1.openseamap.org/seamark/",".png",12,1,"png",overlayDatabase,"sat",true);
                    downloadTile(fp,"https://t1.openseamap.org/seamark/",".png",15,1,"png",overlayDatabase,"sat",true);
                }else{
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/openSeaMaps/webmercator/",".png",8,0,"png",overlayDatabase,"sat",true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/openSeaMaps/webmercator/",".png",12,1,"png",overlayDatabase,"sat",true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/openSeaMaps/webmercator/",".png",15,1,"png",overlayDatabase,"sat",true);
                }

                return null;
            }
        }).execute();
    }

    private final MapEventsReceiver mReceive = new MapEventsReceiver(){

        @Override
        public boolean singleTapConfirmedHelper(GeoPoint p) {
            if(InfoWindow.getOpenedInfoWindowsOn(map).size()>0)
                InfoWindow.closeAllInfoWindowsOn(map);
            else
                map.getController().animateTo(p);
            return false;
        }
        @Override
        public boolean longPressHelper(final GeoPoint p) {
            View spacer = findViewById(R.id.long_press_spacer);
            if(!measureMarkers.isEmpty()){
                addMeasure(p);
                rebuildMeasure();
                return true;
            }
            CoordinatorLayout.LayoutParams l = (CoordinatorLayout.LayoutParams) spacer.getLayoutParams();
            int x = (int)((p.getLongitude()-map.getBoundingBox().getLonWest())/(map.getBoundingBox().getLonEast()-map.getBoundingBox().getLonWest())*displayMetrics.widthPixels);
            int y = (int)((p.getLatitude()-map.getBoundingBox().getLatNorth())/(map.getBoundingBox().getLatSouth()-map.getBoundingBox().getLatNorth())*displayMetrics.heightPixels);
            //Log.d("displaing",x+","+y);
            l.setMargins(x, y, 0, 0);
            findViewById(R.id.long_press_spacer).setLayoutParams(l);
            longp = new PopupMenu(MainActivity.this,spacer);
            longp.inflate(R.menu.long_menu);
            longp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item==null || item.getTitle()==null)return false;
                    if(item.getTitle().equals(getResources().getString(R.string.sat_map)))downloadSatTiles(p);
                    if(item.getTitle().equals(getResources().getString(R.string.hybrid_map)))downloadHybridTiles(p);
                    if(item.getTitle().equals(getResources().getString(R.string.download_osm)))downloadOSMTiles(p);
                    if(item.getTitle().equals(getResources().getString(R.string.street_map)))downloadStreetTiles(p);
                    if(item.getTitle().equals(getResources().getString(R.string.hd_satellite_map)))downloadHDTiles(p);
                    if(item.getTitle().equals(getResources().getString(R.string.download_region)))getCode(null);
                    //if(item.getTitle().equals(getResources().getString(R.string.bing_map)))downloadBingTiles(p);
                    if(item.getTitle().equals(getResources().getString(R.string.share)))shareThisMarker(p);
                    if(item.getTitle().equals(getResources().getString(R.string.zoom_in)))map.getController().zoomIn();
                    if(item.getTitle().equals(getResources().getString(R.string.zoom_out)))map.getController().zoomOut();
                    if(item.getTitle().equals(getResources().getString(R.string.add_marker))){
                        map.getController().animateTo(p);
                        addNew(item.getActionView());
                    }
                    if(item.getTitle().equals(getResources().getString(R.string.measure))){
                        addMeasure(p);
                        rebuildMeasure();
                        displayMessage("long press to add more measurement points");
                        Toast.makeText(getApplicationContext(),"long press to add more measurement points",Toast.LENGTH_LONG).show();
                    }
                    if (item.getTitle().equals(getResources().getString(R.string.navionics_charts))) {
                        changeMap("navionics");
                        if( NavionicsMobileServices.isNavionicsUserLoggedIn()){
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    NavionicsMobileServices.enableDownloadAreaSelector();
                                    findViewById(R.id.navionics_download).setVisibility(View.VISIBLE);
                                    findViewById(R.id.close).setVisibility(View.VISIBLE);
                                }
                            }, 1000);

                        }else{
                            NavionicsMobileServices.navionicsUser();
                        }
                        if(infoW!=null && infoW.isShowing())infoW.dismiss();
                    }

                    return true;
                }
            });
            longp.getMenu().getItem(0).setTitle(getResources().getString(R.string.download)+" ("+downloadsLeft+")");
            longp.show();
            return false;
        }
    };


    public void addNew(View view) {
        //Log.d("info","adding new");
        if(findViewById(R.id.testImage).getVisibility()==View.GONE){
            findViewById(R.id.testImage).setVisibility(View.VISIBLE);
            findViewById(R.id.close).setVisibility(View.VISIBLE);
            return;
        }
        if(totalTypes<20||Symbols.size()<20){
            displayMessage("Please sync before adding a new marker");
            Toast.makeText(this,"Please sync before adding a new marker",Toast.LENGTH_LONG).show();
            return;
        }
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            displayMessage("Please Login before adding a new marker");
            showLogin();
            return;
        }
        ((FloatingActionButton)findViewById(R.id.fab)).setImageResource( R.drawable.ic_add_foreground);
        findViewById(R.id.testImage).setVisibility(View.GONE);
        findViewById(R.id.close).setVisibility(View.GONE);
        Intent addNewMarker = new Intent(getApplicationContext(), addMarker.class);
        double lat = findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE?
                nMap.getCamera().getTarget().getLatitude():map.getMapCenter().getLatitude();
        double lon = findViewById(R.id.navionicsContainer).getVisibility()==View.VISIBLE?
                nMap.getCamera().getTarget().getLongitude():map.getMapCenter().getLongitude();
        lat = lat>TileSystem.maxLatitude?TileSystem.maxLatitude: Math.max(lat, TileSystem.minLatitude);
        lon = lon>180?180:lon<-180?-180:lon;
        addNewMarker.putExtra("addLat",lat);
        addNewMarker.putExtra("addLon",lon);
        addNewMarker.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        startActivityForResult(addNewMarker,1);
        onStop();
        //Toast.makeText(this,"To Do: Add new maker",Toast.LENGTH_LONG);
    }

    private double bearing(double startLat, double startLng, double endLat, double endLng){
        double latitude1 = Math.toRadians(startLat);
        double latitude2 = Math.toRadians(endLat);
        double longDiff= Math.toRadians(endLng - startLng);
        double y= Math.sin(longDiff)*Math.cos(latitude2);
        double x=Math.cos(latitude1)*Math.sin(latitude2)-Math.sin(latitude1)*Math.cos(latitude2)*Math.cos(longDiff);

        return (Math.toDegrees(Math.atan2(y, x))+360)%360;
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==7&&mFilePathCallback != null){
            Uri[] results = null;
            //Uri results = data == null || resultCode != RESULT_OK ? null : data.getData();
            //Log.d("upload data", result.toString());
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                }
            } else {
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        }
        if (data != null) {
            if (requestCode == 1) { // received data back from the create marker screen
                findViewById(R.id.testImage).setVisibility(View.VISIBLE);
                try {
                    Log.d("back", data.toString());
                    String lat = data.getStringExtra("lat");
                    String lon = data.getStringExtra("lon");
                    if (lat == null || lon == null) return;
                    map.getController().animateTo(new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon)));
                    //Log.d("nav","actmoving");
                    if(NavA)nMap.moveToLocation(new NMSLocationCoordinate2D(Double.parseDouble(lat), Double.parseDouble(lon)),nMap.getCamera().getZoom(),false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            downloadsLeft+=DOWNLOADS_MARKER_REWARD;
        }
    }



    public void zoomToGPS(View view) {
        //Log.d("info","zooming to gps");
        requestLocPermission();
        NavionicsMobileServices.enableGPSServices();
        String bars = "";
        ((TextView)findViewById(R.id.SpeedBaring)).setText("");
        if(myLocation!=null && myLocation.getMyLocationProvider().getLastKnownLocation()!=null && myLocation.getMyLocationProvider().getLastKnownLocation().hasBearing())bars = myLocation.getMyLocationProvider().getLastKnownLocation().getBearing()+"°";
        if(myLocation!=null && myLocation.getMyLocationProvider().getLastKnownLocation()!=null && myLocation.getMyLocationProvider().getLastKnownLocation().hasSpeed())bars = bars + ", "+myLocation.getMyLocationProvider().getLastKnownLocation().getSpeed()+"";
        ((TextView)findViewById(R.id.SpeedBaring)).setText(bars);
        //Log.d("navpos",nMap.getGpsMode().name());
        if(NavA) switch (nMap.getGpsMode()) {
            case NMSGPSModeUnlinked:{
                nMap.setGpsMode(NMSEnum.NMSGPSMode.NMSGPSModeCourseUpUnlinked);
                break;
            }
            case NMSGPSModeCourseUpUnlinked: {
                nMap.setGpsMode(NMSEnum.NMSGPSMode.NMSGPSModeNorthUp);
                Log.d("navpos","unlinked");
                break;
            }
            case NMSGPSModeNorthUp:
            {
                nMap.setGpsMode(NMSEnum.NMSGPSMode.NMSGPSModeCompass);
                Log.d("navpos","compass");
                break;
            }
            case NMSGPSModeCompass:
            {
                nMap.setGpsMode(NMSEnum.NMSGPSMode.NMSGPSModeCourseUp);
                break;
            }
            case NMSGPSModeCourseUp:
            {
                nMap.setGpsMode(NMSEnum.NMSGPSMode.NMSGPSModeCourseUpUnlinked);
                break;
            }
            default:
                break;
        }

        //Log.d("nav","gpsmoving");
        //if(myLocation!=null && myLocation.getMyLocation()!=null)nMap.moveToLocation(new NMSLocationCoordinate2D(myLocation.getMyLocation().getLatitude(),myLocation.getMyLocation().getLongitude()),nMap.getCamera().getZoom(),false);

        if(myLocation!=null){
            map.getController().animateTo(myLocation.getMyLocation());
        }else {
            Log.d("nav","my location is null?");
        }

    }
    public void displayMessage(final String msg){
        try {
            //Log.d("displaying",msg);
            if (findViewById(R.id.tute_container).getVisibility() == View.VISIBLE) return;
            //if (findViewById(R.id.syndOverlay).getVisibility() == View.VISIBLE) return;
            if (lastMessage.equals(msg)) return;
            if (Looper.myLooper() == null) Looper.prepare();
            //Log.d("displaying2",msg);
            //Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
            lastMessage = msg;
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Log.d("showing1",msg);
                    try {
                        if (notes == null || notes.getView() == null) {
                            Toast.makeText(map.getContext(), lastMessage, Toast.LENGTH_LONG).show();
                            return;
                        }
                        notes.setText(lastMessage);
                        ViewGroup group = (ViewGroup) notes.getView();
                        TextView messageTextView = (TextView) group.getChildAt(0);
                        messageTextView.setTextSize(25);
                        messageTextView.setGravity(Gravity.CENTER);
                        notes.setDuration(Toast.LENGTH_LONG);
                        notes.show();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    //Log.d("showing",msg);
                }
            }, 100);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void addMeasure(GeoPoint p){

        Marker m= new Marker(map);
        m.setDraggable(true);
        m.setPosition(new GeoPoint(p.getLatitude()>TileSystem.maxLatitude?
                TileSystem.maxLatitude: Math.max(p.getLatitude(), TileSystem.minLatitude),
                p.getLongitude()>180?180:p.getLongitude()<-180?-180:p.getLongitude()));
        BitmapDrawable bd = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(),R.drawable.measure_marker,null);
        Drawable dd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bd.getBitmap(), (int) (iconSize/2*displayMetrics.density), (int) (iconSize*3/4*displayMetrics.density), false));
        m.setIcon(ResourcesCompat.getDrawable(getResources(),R.drawable.measure_icon,null));

        //Log.d("sbounds",m.getIcon().getDirtyBounds()+"");
        m.setAnchor(.5f,1f);

        m.setPanToView(true);
        InfoWindow i = new InfoWindow(R.layout.measure_close, map) {
            @Override
            public void onOpen(Object item) {}
            @Override
            public void onClose() {}
        };
        i.setRelatedObject(m);
        m.setTitle("X");
        m.setInfoWindow(i);
        //m.setAnchor(0.5f, Marker.ANCHOR_BOTTOM);
        m.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) { }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                rebuildMeasure();
            }
            @Override
            public void onMarkerDragStart(Marker marker) {}
        });
        measureMarkers.add(m);
        rebuildMeasure();
    }

    private void addNMeasure(GeoPoint p){
        findViewById(R.id.close).setVisibility(View.VISIBLE);
        NMSLocationCoordinate2D markerLocation = new NMSLocationCoordinate2D(p.getLatitude(), p.getLongitude());
        NMSMarker m = NMSMarker.markerWithPosition(markerLocation);
        BitmapDrawable dd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.measure_marker)).getBitmap(), iconSize*3/4, iconSize*4/3, false));
        m.setImage(dd.getBitmap());
        if(NavA)m.setMap(nMap);
        m.setAnchor(CGPoint.CGPointMake(0.5f, 1f));
        m.setOpacity(0.9f);
        measureNMarkers.add(m);
        //m.setTitle("X");
        rebuildNMeasure();
    }

    private void rebuildNMeasure(){

//        for (NMSMarker m : measureNMarkers){
//            nMap.removeOverlay(m);
//        }
        for (NMSMarker m : measureNMarkertexts){
            if(NavA)nMap.removeOverlay(m);
        }
        if(NavA)if (measureNLine!=null)nMap.removeOverlay(measureNLine);
        measureNMarkertexts.clear();
        if (measureNMarkers.isEmpty())return;
        String measureUnit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[0])?
                "km":PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[1])?
                "m":"nm";
        Double measureUnitOffset = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[0])?
                1:PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[1])?
                0.621371:0.539957;
        NMSMutablePath p = new NMSMutablePath();
        int tl = 0;
        for(int i = 0;i<measureNMarkers.size();i++){
            //map.getOverlays().add(measureMarkers.get(i));
            p.addCoordinate(measureNMarkers.get(i).getPosition());
            if (i > 0) { //dont draw lines for the first one!
                //Log.d("markers","unbetween");
                //Log.d("m1",measureNMarkers.get(i-1).getPosition().toString());
                //Log.d("m2",measureNMarkers.get(i).getPosition().toString());

                NMSLocationCoordinate2D markerLocation = new NMSLocationCoordinate2D((
                        measureNMarkers.get(i).getPosition().getLatitude()+measureNMarkers.get(i-1).getPosition().getLatitude())/2,
                        (measureNMarkers.get(i).getPosition().getLongitude()+measureNMarkers.get(i-1).getPosition().getLongitude())/2);
                NMSMarker md = NMSMarker.markerWithPosition(markerLocation);

                //BitmapDrawable dd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(((BitmapDrawable) getResources().getDrawable(R.drawable.measure_marker)).getBitmap(), iconSize/2, iconSize*3/4, false));
                //md.setImage(measureNMarkers.get(i).getImage());
                Location l1 = new Location("");
                l1.setLatitude(measureNMarkers.get(i-1).getPosition().getLatitude());
                l1.setLongitude(measureNMarkers.get(i-1).getPosition().getLongitude());
                Location l2 = new Location("");
                l2.setLatitude(measureNMarkers.get(i).getPosition().getLatitude());
                l2.setLongitude(measureNMarkers.get(i).getPosition().getLongitude());
                tl+=l1.distanceTo(l2);
                int di = (int) (tl*measureUnitOffset);
                String distance = di/1000.0+measureUnit;
                int brng = (int) (l1.bearingTo(l2)<0?l1.bearingTo(l2)+360:l1.bearingTo(l2));

                //Log.d("marker",distance+","+brng);
                md.setImage(drawText(distance+ ", \n" + brng + "º",200,Color.GRAY));
                md.setTitle(distance+ ", \n" + brng + "º");
                md.setAnchor(CGPoint.CGPointMake(0.5f, 0.5f));
                md.setOpacity(0.9f);
                if(NavA)md.setMap(nMap);
                measureNMarkertexts.add(md);
                Log.d("md",md.getPosition().toString());
            }
        }

        measureNLine = NMSPolyline.polylineWithPath(p);
        measureNLine.setStrokeColor(NMSColor.colorWithAlphaComponent(Color.BLUE,0.5f));
        //measureNLine.setStrokeColor(NMSColor.colorWithAlphaComponent(Color.GREEN,0.5f));
        measureNLine.setStrokeWidth(5.0f);
        measureNLine.setTitle("mmm");
        if(NavA)measureNLine.setMap(nMap);
    }
    public Bitmap drawText(String text, int textWidth, int color) {
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setTextSize(iconSize/3+10);
        //textPaint.setTextAlign(Paint.Align.CENTER);
        StaticLayout mTextLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        Bitmap b = Bitmap.createBitmap(textWidth, mTextLayout.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas c = new Canvas(b);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        c.drawPaint(paint);
        c.save();
        c.translate(0, 0);
        mTextLayout.draw(c);
        c.restore();

        return b;
    }


    private void rebuildMeasure(){
        findViewById(R.id.close).setVisibility(View.VISIBLE);
        for (Marker m : measureMarkers){
            map.getOverlayManager().remove(m);
        }
        for (Marker m : measureMarkertexts){
            map.getOverlayManager().remove(m);
        }
        map.getOverlayManager().remove(measureLine);
        measureLine.getActualPoints().clear();
        measureMarkertexts.clear();
        if (measureMarkers.isEmpty())return;
        String measureUnit = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[0])?
                "km":PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[1])?
                "m":"nm";
        Double measureUnitOffset = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[0])?
                1:PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("units","Metric").equals(getResources().getStringArray(R.array.units)[1])?
                0.621371:0.539957;
        for(int i = 0;i<measureMarkers.size();i++){
            //map.getOverlays().add(measureMarkers.get(i));
            measureLine.addPoint(measureMarkers.get(i).getPosition());
            if (i > 0) { //dont draw lines for the first one!
                Marker md = new Marker(map);
                md.setTextIcon(" ");
                md.setPosition(new GeoPoint((measureMarkers.get(i).getPosition().getLatitude() + measureMarkers.get(i-1).getPosition().getLatitude()) / 2, (measureMarkers.get(i).getPosition().getLongitude() + measureMarkers.get(i-1).getPosition().getLongitude()) / 2));
                md.setTextLabelForegroundColor(Color.WHITE);
                md.setTextLabelBackgroundColor(Color.GRAY);
                md.setTextLabelFontSize(iconSize/3+8);
                md.setOnMarkerClickListener(null);
                md.setInfoWindow(null);
                int di = (int) (measureLine.getDistance()*measureUnitOffset);
                String distance = di/1000.0+measureUnit;
                int brng = (int) bearing(measureMarkers.get(i-1).getPosition().getLatitude(), measureMarkers.get(i-1).getPosition().getLongitude(), measureMarkers.get(i).getPosition().getLatitude(), measureMarkers.get(i).getPosition().getLongitude());
                md.setTextIcon(distance+ ", \n" + brng + "º");
                //map.getOverlayManager().add(md);
                measureMarkertexts.add(md);
            }
        }
        measureLine.getOutlinePaint().setColor(Color.BLUE);
        map.getOverlayManager().add(measureLine);
        map.getOverlayManager().addAll(measureMarkers);
        map.getOverlayManager().addAll(measureMarkertexts);
    }



    //    private void downloadBingTiles(GeoPoint p) {
//        if(infoW!=null && infoW.isShowing()) p=infoWP;
//        if(p==null)return;
//        if(downloadsLeft--<=1){//user has no more downloads left
//            displayMessage(getResources().getString(R.string.run_out));
//            showDownloadsDialog();
//            return;
//        }
//        ((TextView)findViewById(R.id.copyWriteMessage)).setText(R.string.copyBing);
//        if (!CurrentMaptype.equals(satDatabase))changeMap(satDatabase);
//        String session = ""+(int)(Math.random()*5000);
//        String zxy = getTileNumber(p.getLatitude(),p.getLongitude(),8);
//        String bingq = getBingTileString(p.getLatitude(),p.getLongitude(),8,0,0);
//        long z = Long.parseLong(zxy.split("/")[0]);
//        long x = Long.parseLong(zxy.split("/")[1]);
//        long y = Long.parseLong(zxy.split("/")[2]);
//        long index = ((z << z) + x << z) + y;
//        final IGeoPoint loc = p;
//        multiDownloads = 16;
//        new HTTPGetRequest(res, new Callable<String>() {
//            public String call() {
//                return null;
//            }
//        }).execute("https://ecn.t1.tiles.virtualearth.net/tiles/a"+bingq+".jpeg?g="+session,"tile",""+index,"jpg",satDatabase,satProvider);
//        //Log.d("downing","https://ecn.t1.tiles.virtualearth.net/tiles/a"+bingq+".jpeg?g="+session);
//        zxy = getTileNumber(p.getLatitude(),p.getLongitude(),12);
//        z = Long.parseLong(zxy.split("/")[0]);
//        x = Long.parseLong(zxy.split("/")[1]);
//        y = Long.parseLong(zxy.split("/")[2]);
//        for(int i=-1;i<2;i++){
//            for(int j=-1;j<2;j++){
//                index = ((z << z) + (x+i) << z) + (y+j);
//                bingq = getBingTileString(p.getLatitude(),p.getLongitude(),12,i,j);
//                new HTTPGetRequest(res, new Callable<String>() {
//                    public String call() {
//                        return null;
//                    }
//                }).execute("https://ecn.t1.tiles.virtualearth.net/tiles/a"+bingq+".jpeg?g="+session,"tile",""+index,"jpg",satDatabase,satProvider);
//            }
//        }
//
//        zxy = getTileNumber(p.getLatitude(),p.getLongitude(),15);
//        z = Long.parseLong(zxy.split("/")[0]);
//        x = Long.parseLong(zxy.split("/")[1]);
//        y = Long.parseLong(zxy.split("/")[2]);
//        for(int i=-1;i<2;i++){
//            for(int j=-1;j<2;j++){
//                index = ((z << z) + x+i << z) + y+j;
//                bingq = getBingTileString(p.getLatitude(),p.getLongitude(),15,i,j);
//                new HTTPGetRequest(res, new Callable<String>() {
//                    public String call() {
//                        multiDownloads--;
//                        if(multiDownloads==0){
//                            displayMessage(getResources().getString(R.string.download_complete)+"\n"+downloadsLeft+" "+getResources().getString(R.string.downloads_remaining));
//                            map.getController().animateTo(loc,15.0, (long) 3);
//                        }
//                        return null;
//                    }
//                }).execute("https://ecn.t1.tiles.virtualearth.net/tiles/a"+bingq+".jpeg?g="+session,"tile",""+index,"jpg",satDatabase,satProvider);
//            }
//        }
//    }
    private void downloadHybridTiles(GeoPoint p) {
        if (!CurrentMaptype.equals(satDatabase))changeMap(satDatabase);
        if(infoW!=null && infoW.isShowing()){
            p = infoWP;
            infoW.dismiss();
        }
        final GeoPoint fp = p;
        if(p==null)return;
        //Log.d("downloading maps for",""+fp.toString());

        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call() {
                //downloadTile(fp,"https://"+getSetver()+".tiles.mapbox.com/v4/tmcw.map-j5fsp01s/",".jpg?access_token="+HTTPGetRequest.mapboxToken,8,0,"jpg",satDatabase,satProvider,true);
                if(downloadsLeft--<=1){//user has no more downloads left
                    displayMessage(getResources().getString(R.string.run_out));
                    showDownloadsDialog();
                    return null;
                }
                findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.copyWriteMessage)).setText(R.string.mapbox_copy);


                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("altTiles",false)) {
                    downloadZTile(fp, "hybrid", 8, 0, "jpg", satDatabase, satProvider, true);
                    downloadZTile(fp, "hybrid", 15, 1, "jpg", satDatabase, satProvider, true);
                    downloadZTile(fp, "hybrid", 12, 1, "jpg", satDatabase, satProvider, true);
                }else{
                    downloadTile(fp,"https://api.mapbox.com/styles/v1/mapbox/satellite-streets-v10/tiles/","?access_token="+getResources().getString(R.string.mapboxToken),8,1,"jpg",satDatabase,satProvider,true);
                    downloadTile(fp,"https://api.mapbox.com/styles/v1/mapbox/satellite-streets-v10/tiles/","?access_token="+getResources().getString(R.string.mapboxToken),12,1,"jpg",satDatabase,satProvider,true);
                    downloadTile(fp,"https://api.mapbox.com/styles/v1/mapbox/satellite-streets-v10/tiles/","?access_token="+getResources().getString(R.string.mapboxToken),15,1,"jpg",satDatabase,satProvider,true);
                    downloadTile(fp,"https://api.mapbox.com/styles/v1/mapbox/satellite-streets-v10/tiles/","?access_token="+getResources().getString(R.string.mapboxToken),18,1,"jpg",satDatabase,satProvider,true);
                }
                return null;
            }
        }).execute();
    }
    private void downloadHDTiles(GeoPoint p) {
        if (!CurrentMaptype.equals(satDatabase))changeMap(satDatabase);
        if(infoW!=null && infoW.isShowing()){
            p = infoWP;
            infoW.dismiss();
        }
        final GeoPoint fp = p;
        if(p==null)return;
        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call() {
                if(downloadsLeft--<=1){//user has no more downloads left
                    displayMessage(getResources().getString(R.string.run_out));
                    showDownloadsDialog();
                    return null;
                }
                downloadsLeft--;
                findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.copyWriteMessage)).setText(R.string.mapbox_copy);
                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("altTiles",false)) {
//                    downloadZTile(fp, "hd", 8, 0, "jpg", satDatabase, satProvider, true);
//                    downloadZTile(fp, "hd", 15, 1, "jpg", satDatabase, satProvider, true);
//                    downloadZTile(fp, "hd", 12, 1, "jpg", satDatabase, satProvider, true);
//                    downloadZTile(fp, "hd", 18, 2, "jpg", satDatabase, satProvider, true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",8,0,"png",satDatabase,satProvider,true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",12,1,"png",satDatabase,satProvider,true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",15,1,"png",satDatabase,satProvider,true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",18,2,"png",satDatabase,satProvider,true);
                }else {
                    downloadTile(fp,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",8,0,"png",satDatabase,satProvider,false);
                    downloadTile(fp,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",12,1,"png",satDatabase,satProvider,false);
                    downloadTile(fp,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",15,1,"png",satDatabase,satProvider,false);
                    downloadTile(fp,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",18,2,"png",satDatabase,satProvider,false);
                }
                return null;
            }
        }).execute();
    }
    private void downloadSatTiles(GeoPoint p) {
        if (!CurrentMaptype.equals(satDatabase))changeMap(satDatabase);
        if(infoW!=null && infoW.isShowing()){
            p = infoWP;
            infoW.dismiss();
        }
        final GeoPoint fp = p;
        if(p==null)return;

        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call() {

                if(downloadsLeft--<=1){//user has no more downloads left
                    displayMessage(getResources().getString(R.string.run_out));
                    showDownloadsDialog();
                    return null;
                }
                findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.copyWriteMessage)).setText(R.string.mapbox_copy);


                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("altTiles",false)) {
                    downloadZTile(fp, "sat", 8, 0, "jpg", satDatabase, satProvider, true);
                    downloadZTile(fp, "sat", 15, 1, "jpg", satDatabase, satProvider, true);
                    downloadZTile(fp, "sat", 12, 1, "jpg", satDatabase, satProvider, true);
                    downloadZTile(fp, "sat", 18, 2, "jpg", satDatabase, satProvider, true);

                }else{
                    downloadTile(fp,"https://"+getSetver()+".tiles.mapbox.com/v4/mapbox.satellite/",".jpg?access_token="+getResources().getString(R.string.mapboxToken),8,0,"jpg",satDatabase,satProvider,true);
                    downloadTile(fp,"https://"+getSetver()+".tiles.mapbox.com/v4/mapbox.satellite/",".jpg?access_token="+getResources().getString(R.string.mapboxToken),12,1,"jpg",satDatabase,satProvider,true);
                    downloadTile(fp,"https://"+getSetver()+".tiles.mapbox.com/v4/mapbox.satellite/",".jpg?access_token="+getResources().getString(R.string.mapboxToken),15,1,"jpg",satDatabase,satProvider,true);
                    downloadTile(fp,"https://"+getSetver()+".tiles.mapbox.com/v4/mapbox.satellite/",".jpg?access_token="+getResources().getString(R.string.mapboxToken),18,2,"jpg",satDatabase,satProvider,true);
                }
                return null;
            }
        }).execute();
    }
    private void downloadStreetTiles(GeoPoint p) {
        if (!CurrentMaptype.equals(streetDatabase))changeMap(streetDatabase);
        ((TextView)findViewById(R.id.copyWriteMessage)).setText(R.string.mapbox_copy);
        if(infoW!=null && infoW.isShowing()){
            p = infoWP;
            infoW.dismiss();
        }
        if(p==null)return;
        final GeoPoint fp = p;
        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call() {
                //
                //if(downloadsLeft--<=1){//user has no more downloads left
                //    displayMessage(getResources().getString(R.string.run_out));
                //    showDownloadsDialog();
                //    return null;
                //}
                //Log.d("downloadsLeft",""+downloadsLeft);
                findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);

                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("altTiles",false)){
//                downloadZTile(fp,"street",8,0,"png",streetDatabase, "OSMPublicTransport",true);
//                downloadZTile(fp,"street",15,1,"png",streetDatabase, "OSMPublicTransport",true);
//                downloadZTile(fp,"street",12,1,"png",streetDatabase, "OSMPublicTransport",true);
                    downloadTile(fp,"https://maps.wikimedia.org/osm-intl/",".png",8,0,"png",streetDatabase, "OSMPublicTransport",true);
                    downloadTile(fp,"https://maps.wikimedia.org/osm-intl/",".png",12,1,"png",streetDatabase, "OSMPublicTransport",true);
                    downloadTile(fp,"https://maps.wikimedia.org/osm-intl/",".png",15,1,"png",streetDatabase, "OSMPublicTransport",true);
                }else {
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/osm/webmercator/",".png",8,0,"png",streetDatabase, "OSMPublicTransport",true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/osm/webmercator/",".png",12,1,"png",streetDatabase, "OSMPublicTransport",true);
                    downloadTile(fp,"https://tiles.zuluwaterways.com/wmts/osm/webmercator/",".png",15,1,"png",streetDatabase, "OSMPublicTransport",true);
                }

                if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("high_res",false)){
                    downloadTile(fp,"https://maps.wikimedia.org/osm-intl/",".png",18,1,"png",streetDatabase, "OSMPublicTransport",true);
                    //downloadsLeft--;
                }
                return null;
            }
        }).execute();
    }

    private void downloadARCGISTiles(GeoPoint p) {
        if (!CurrentMaptype.equals(satDatabase))changeMap(satDatabase);
        if(infoW!=null && infoW.isShowing()){
            p = infoWP;
            infoW.dismiss();
        }

        if(p==null)return;
        if(downloadsLeft--<=1){//user has no more downloads left
            displayMessage(getResources().getString(R.string.run_out));
            showDownloadsDialog();
            return;
        }
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("altTiles",false)) {
//            downloadZTile(p, "hd", 8, 0, "png", satDatabase, satProvider, false);
//            downloadZTile(p, "hd", 12, 1, "png", satDatabase, satProvider, false);
//            downloadZTile(p, "hd", 15, 1, "png", satDatabase, satProvider, false);
            downloadTile(p,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",8,0,"png",satDatabase,satProvider,false);
            downloadTile(p,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",12,1,"png",satDatabase,satProvider,false);
            downloadTile(p,"https://server.arcgisonline.com/arcgis/rest/services/World_Imagery/MapServer/tile/",".png",15,1,"png",satDatabase,satProvider,false);
        }else {
            downloadTile(p,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",8,0,"png",satDatabase,satProvider,false);
            downloadTile(p,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",12,1,"png",satDatabase,satProvider,false);
            downloadTile(p,"https://tiles.zuluwaterways.com/wmts/mapBoxHybrid/webmercator/",".png",15,1,"png",satDatabase,satProvider,false);

        }
    }

    public void downloadTile(final GeoPoint p, String url, String url2, int zoom, final int spread, String type, String db, String provider, boolean xy){
        //final Marker m = (Marker) InfoWindow.getOpenedInfoWindowsOn(map).get(0).getRelatedObject();
        String zxy = getTileNumber(p.getLatitude(),p.getLongitude(),zoom);
        InfoWindow.closeAllInfoWindowsOn(map);
        if(infoW!=null && infoW.isShowing())infoW.dismiss();
        long z = Long.parseLong(zxy.split("/")[0]);
        long x = Long.parseLong(zxy.split("/")[1]);
        long y = Long.parseLong(zxy.split("/")[2]);
        for(int i=spread*-1;i<=spread;i++){
            for(int j=-1*spread;j<=spread;j++){
                long index = ((z << z) + x+i << z) + y+j;
                multiDownloads++;
                final int k = i;
                final int l = j;
                String u = xy?(url+z+"/"+(x+i)+"/"+(y+j)+url2):url+z+"/"+(y+j)+"/"+(x+i)+url2;
                new HTTPGetRequest(res, new Callable<String>() {
                    public String call() {
                        multiDownloads--;
                        ((ProgressBar)findViewById(R.id.mapDownloadProgress)).incrementProgressBy(spread>1?1:4);
                        if(res!=null && !res.toString().equals("null")){

                            String v = "https://"+getSetver()+".tiles.mapbox.com/v4/mapbox.satellite/"+z+"/"+(x+k)+"/"+(y+l)+".jpg?access_token="+getResources().getString(R.string.mapboxToken);
                            Log.d("getting mapbox tiles!"+res.toString(),v);
                            multiDownloads++;
                            new HTTPGetRequest(res, new Callable<String>() {
                                public String call() {
                                    multiDownloads--;
                                    ((ProgressBar)findViewById(R.id.mapDownloadProgress)).incrementProgressBy(spread>1?1:4);
                                    if(multiDownloads==0){
                                        downloadsLeft++;
                                        displayMessage(getResources().getString(R.string.download_complete)+"\n"+downloadsLeft+" "+getResources().getString(R.string.downloads_remaining));
                                        Log.d("downloaded!",""+zoom);
                                        map.getController().animateTo(p,zoom*1.0+1, (long) 3);
                                        ((ProgressBar)findViewById(R.id.mapDownloadProgress)).setProgress(0);
                                        findViewById(R.id.mapDownload).setVisibility(View.GONE);
                                    }
                                    return null;
                                }
                            }).execute(v,"tile",""+index,type, db,provider);
                        }
                        if(multiDownloads==0){
                            if(res!=null)downloadsLeft++;
                            displayMessage(getResources().getString(R.string.download_complete)+"\n"+downloadsLeft+" "+getResources().getString(R.string.downloads_remaining));
                            Log.d("downloaded!",""+zoom);
                            map.getController().animateTo(p,zoom*1.0+1, (long) 3);
                            ((ProgressBar)findViewById(R.id.mapDownloadProgress)).setProgress(0);
                            findViewById(R.id.mapDownload).setVisibility(View.GONE);
                        }
                        return null;
                    }
                }).execute(u,"tile",""+index,type, db,provider);
            }
        }
    }
    public void downloadZTile(final GeoPoint p, String kind, int zoom, final int spread, String type, String db, String provider, boolean xy){
        //final Marker m = (Marker) InfoWindow.getOpenedInfoWindowsOn(map).get(0).getRelatedObject();
        String zxy = getTileNumber(p.getLatitude(),p.getLongitude(),zoom);
        InfoWindow.closeAllInfoWindowsOn(map);
        if(infoW!=null && infoW.isShowing())infoW.dismiss();
        long z = Long.parseLong(zxy.split("/")[0]);
        long x = Long.parseLong(zxy.split("/")[1]);
        long y = Long.parseLong(zxy.split("/")[2]);
        for(int i=spread*-1;i<=spread;i++){
            for(int j=-1*spread;j<=spread;j++){
                long index = ((z << z) + x+i << z) + y+j;
                multiDownloads++;
                String u = "https://www.zuluwaterways.com/offline/tiles/"+kind+"/"+z+"/"+(xy?(y+j):(x+i))+"/"+(xy?(x+i):(y+j));
                Log.d("requesting",u);
                new HTTPGetRequest(res, new Callable<String>() {
                    public String call() {
                        multiDownloads--;
                        ((ProgressBar)findViewById(R.id.mapDownloadProgress)).incrementProgressBy(spread>1?1:4);
                        if(multiDownloads==0){
                            displayMessage(getResources().getString(R.string.download_complete)+"\n"+downloadsLeft+" "+getResources().getString(R.string.downloads_remaining));
                            map.getController().animateTo(p,15.0, (long) 3);
                            ((ProgressBar)findViewById(R.id.mapDownloadProgress)).setProgress(0);
                            findViewById(R.id.mapDownload).setVisibility(View.GONE);
                        }
                        return null;
                    }
                }).execute(u,"tile",""+index,type, db,provider);
            }
        }
    }

    private static String getTileNumber(final double lat, final double lon, final int zoom) {
        //Log.d("xyz",lat+","+lon+","+zoom);
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) ) ;
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) ) ;

        //Log.d("tt",""+Math.tan(Math.toRadians(lat)));
        //Log.d("ct",""+(1 / Math.cos(Math.toRadians(lat))));
        //Log.d("dz",""+(2 * (1<<zoom)));
        //Log.d("lt",""+Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))));
        //Log.d("lt",""+(1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom));

        //Log.d("dz",""+(int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1<<zoom) );

        if (xtile < 0)
            xtile=0;
        if (xtile >= (1<<zoom))
            xtile=((1<<zoom)-1);
        if (ytile < 0)
            ytile=0;
        if (ytile >= (1<<zoom))
            ytile=((1<<zoom)-1);
        //Log.d("xyz",zoom + "/" + xtile + "/" + ytile);
        return("" + zoom + "/" + xtile + "/" + ytile);
    }
    public static String getBingTileString(double lat, double lon, int detail, int xo, int yo){
        double x = (lon + 180) / 360;
        double sinLatitude = Math.sin(lat * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);
        int mapSize = 256 << detail;
        int X =xo+(int) Math.min(Math.max(x * mapSize + 0.5, 0), mapSize - 1)/256;
        int Y =yo+ (int) Math.min(Math.max(y * mapSize + 0.5, 0), mapSize - 1)/256;
        StringBuilder quadKey = new StringBuilder();
        for (int i = detail; i > 0; i--)        {
            char digit = '0';
            int mask = 1 << (i - 1);
            if ((X & mask) != 0) digit++;
            if ((Y & mask) != 0){
                digit++;
                digit++;
            }
            quadKey.append(digit);
        }
        return quadKey.toString();
    }

    public String getSetver(){
        return Math.random()>0.75?"a":Math.random()>0.66?"b":Math.random()>0.5?"c":"d";
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        //Log.d("menu","Clicked");
        return false;
    }


    public void getPicture(final String f, final ImageView v){
        //Toast.makeText(map.getContext(),"Downloading picture"+f,Toast.LENGTH_SHORT).show();

        String name = f.split("/")[f.split("/").length-1];
        //Log.d("doqnloading",name);
        new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
            public String call() {
                Bitmap myBitmap = BitmapFactory.decodeFile(f);
                v.setImageBitmap(myBitmap);
                v.setVisibility(View.VISIBLE);
                //Toast.makeText(map.getContext(),"Download Compete",Toast.LENGTH_SHORT).show();
                return null;
            }
        }).execute("https://api.zuluwaterways.com/getImg.php?tk=35q25wa5wsvi7y8s83wy53wy5nkyzs20lzsb23&fn="+name,"pic",name,getFilesDir().getAbsolutePath());
    }

    public void showComments() {
        String id = ((MarkerInfoWindow)InfoWindow.getOpenedInfoWindowsOn(map).get(0)).getMarkerReference().getId();
        ArrayList<String> cc = mDbHelper.getComments(id);
        StringBuilder co = new StringBuilder();
        for (String value : cc) {
            String[] cs = value.split(";");
            String byline = "<p>"+cs[1]+"@"+cs[0]+"</p>";
            cs[0] = "";
            cs[1] = "";
            co.append(Html.fromHtml(TextUtils.join("", cs) + byline+"<hr>"));
        }
        ((TextView)findViewById(R.id.bubble_comments)).setText(co.toString());
        ((TextView)findViewById(R.id.bubble_comments)).setMovementMethod(LinkMovementMethod.getInstance());
    }


    public void shareThisMarker(GeoPoint p) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://www.zuluwaterways.com/#/appshare/"+
                (p.getLatitude()>TileSystem.maxLatitude?
                        TileSystem.maxLatitude:p.getLatitude()<TileSystem.minLatitude?TileSystem.minLatitude:p.getLatitude())+"/"+
                (p.getLongitude()>180?180:p.getLongitude()<-180?-180:p.getLongitude()));
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    public void addComment(MenuItem item) {
        infoW.getContentView().findViewById(R.id.bubble_add_comment_container).setVisibility(View.VISIBLE);
        infoW.getContentView().findViewById(R.id.bubble_comments_add_comment).requestFocus();
    }

    public void addComment(View view) {
        Log.d("add","comment");
        if(!mDbHelper.addComment(((MarkerInfoWindow)InfoWindow.getOpenedInfoWindowsOn(map).get(0)).getMarkerReference().getId()+"",((EditText)findViewById(R.id.bubble_comments_add_comment)).getText().toString(),FirebaseAuth.getInstance().getCurrentUser()==null?"":FirebaseAuth.getInstance().getCurrentUser().getDisplayName())){
            displayMessage(getResources().getString(R.string.comment_error));
        }

        showComments();
        findViewById(R.id.bubble_add_comment_container).setVisibility(View.GONE);
    }

    public void zoomImage(View view) {
        if(infoW!=null)infoW.dismiss();
        if(map!=null)InfoWindow.closeAllInfoWindowsOn(map);
        findViewById(R.id.close).setVisibility(View.VISIBLE);
        findViewById(R.id.bubble_image_zoom).setVisibility(View.VISIBLE);
        findViewById(R.id.bubble_image_zoom).setX(0);
        findViewById(R.id.bubble_image_zoom).setTop(0);
        ((MyImageView)findViewById(R.id.bubble_image_zoom)).setImageDrawable(((ImageView)view).getDrawable());
    }
    public void changeLayer(View view) {
        PopupMenu p = new PopupMenu(this,view);
        MenuItem i = p.getMenu().add(R.string.sat_map);
        i.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMap(satDatabase);
                return true;
            }
        });
        MenuItem ii = p.getMenu().add(R.string.street_map);
        ii.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMap(streetDatabase);
                return true;
            }
        });

        if(NavA&&CurrentMaptype.equals("navionics")){

            MenuItem jc = p.getMenu().add(R.string.navionics_charts);
            jc.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    NMSMapSettings sett = nMap.getSettings();
                    NMSMapSettingsEdit  editor= new NMSMapSettingsEdit(sett);
                    editor.setMapMode(NMSEnum.NMSMapMode.NMSMapModeDefault);
                    nMap.setSettings(editor);
                    return true;
                }
            });
            MenuItem js = p.getMenu().add(R.string.sonar_charts);
            js.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    NMSMapSettings sett = nMap.getSettings();
                    NMSMapSettingsEdit  editor= new NMSMapSettingsEdit(sett);
                    editor.setMapMode(NMSEnum.NMSMapMode.NMSMapModeSonarCharts);
                    nMap.setSettings(editor);
                    return true;
                }
            });
        }else{
            MenuItem j = p.getMenu().add(R.string.navionics_maps);
            j.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(NavA)changeMap("navionics");
                    return true;
                }
            });
        }
        SubMenu jjj = p.getMenu().addSubMenu(R.string.overlays);

        MenuItem jj = jjj.add(R.string.download_osm);
        jj.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                changeMap("OSM");
                return true;
            }
        });
        p.show();
    }
    public void changeMap(String type){
        for(Overlay o:overlays){
            map.getOverlayManager().remove(o);
        }
        if(CurrentMaptype.equals(type) && !type.equals("OSM"))return;//no need to change if we are already here
        if(type.equals("navionics")){
            findViewById(R.id.navionicsContainer).setVisibility(View.VISIBLE);
            findViewById(R.id.map).setVisibility(View.GONE);
            //Log.d("nav","lchangemoving");
            final NMSLocationCoordinate2D g=new NMSLocationCoordinate2D(map.getMapCenter().getLatitude(),map.getMapCenter().getLongitude());
            //Log.d("nav",g.getLongitude()+","+g.getLatitude());
            if((int)g.getLongitude()!=0 && (int)g.getLatitude()!=0) {
                //Log.d("moving" ,nMap.getCamera().getTarget().toString());

                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(NavA)nMap.moveToLocation(g,(float)map.getZoomLevelDouble(),true);
                        if(NavA)nMap.moveToCameraPosition(NMSCameraPosition.cameraWithLatitude(map.getMapCenter().getLatitude(),map.getMapCenter().getLongitude(), (float) map.getZoomLevelDouble()),true);
                    }
                }, 1000);

                if(NavA)nMap.moveToLocation(g,(float)map.getZoomLevelDouble(),true);
                if(NavA)nMap.moveToCameraPosition(NMSCameraPosition.cameraWithLatitude(map.getMapCenter().getLatitude(),map.getMapCenter().getLongitude(), (float) map.getZoomLevelDouble()),true);
                //nMap.moveToZoom((float)map.getZoomLevelDouble(),false);
                //Log.d("moving2" ,nMap.getCamera().getTarget().toString());
            }
            CurrentMaptype = type;
            //loadIcons();
            return;
        }else{
            findViewById(R.id.navionicsContainer).setVisibility(View.GONE);
            findViewById(R.id.map).setVisibility(View.VISIBLE);

            if(NavA&&CurrentMaptype.equals("navionics")&& (int)nMap.getCamera().getTarget().getLatitude()!=0) { //dont move if the other map is not loaded.
                //Log.d("moving mapt to",nMap.getCamera().getTarget().toString()+","+nMap.getCamera().getZoom());
                map.getController().setZoom(nMap.getCamera().getZoom());
                map.getController().setCenter(new GeoPoint(nMap.getCamera().getTarget().getLatitude(), nMap.getCamera().getTarget().getLongitude()));
            }
            //map.getController().animateTo(new GeoPoint(nMap.getCamera().getTarget().getLatitude(),nMap.getCamera().getTarget().getLongitude()));

        }

        overlays.clear();
        if(type.equals("OSM")){
            try {
                File f = new File(overlayDatabase);//Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/
                if (f.exists()) {
                    OfflineTileProvider om = new OfflineTileProvider(new SimpleRegisterReceiver(this), new File[]{f});
                    om.setTileSource(FileBasedTileSource.getSource(om.getArchives()[0].getTileSources().iterator().next()));
                    TilesOverlay tilesOverlay = new TilesOverlay(om, this.getApplicationContext());
                    tilesOverlay.setUseDataConnection(false);
                    tilesOverlay.setLoadingBackgroundColor(Color.TRANSPARENT);
                    map.getOverlays().add(tilesOverlay);
                    map.invalidate();
                    overlays.add(tilesOverlay);
                } else {
                    displayMessage( type + " Base Map not found!");
                }
            }catch (Exception e){
                e.printStackTrace();
                //displayMessage(getResources().getString(R.string.basemap_error));
            }
            fetchedGrid=new BoundingBox(0,0,0,0);
            loadIcons();
            return;
        }
        CurrentMaptype = type;
        //map.getOverlays().clear();
        try {
            File f = new File(type);//Environment.getExternalStorageDirectory().getAbsolutePath() + type);//Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/
            if (f.exists()) {
                OfflineTileProvider om = new OfflineTileProvider(new SimpleRegisterReceiver(this), new File[]{f});
                map.setTileProvider(om);
                this.map.setTileSource(FileBasedTileSource.getSource(om.getArchives()[0].getTileSources().iterator().next()));
                this.map.invalidate();
            } else {
                //displayMessage( type + " Base Map not found!");
            }
        }catch (Exception e){
            e.printStackTrace();
            displayMessage(getResources().getString(R.string.basemap_error));
        }
        fetchedGrid=new BoundingBox(0,0,0,0);
        loadIcons();
    }

    public void closeZoom(View view) {
        findViewById(R.id.close).setVisibility(View.GONE);
        findViewById(R.id.bubble_image_zoom).setVisibility(View.GONE);
        findViewById(R.id.testImage).setVisibility(View.GONE);
        findViewById(R.id.navionics_download).setVisibility(View.GONE);
        InfoWindow.closeAllInfoWindowsOn(map);
        if(!measureMarkers.isEmpty()){
            for(Marker m:measureMarkers){
                m.remove(map);
            }
            for(Marker p:measureMarkertexts ){
                map.getOverlayManager().remove(p);
            }
            map.getOverlayManager().remove(measureLine);
            measureMarkers.clear();
            measureMarkertexts.clear();
        }
        if(!measureNMarkers.isEmpty()){
            for(NMSMarker m : measureNMarkers){
                if(NavA)nMap.removeOverlay(m);
                m.setMap(null);
            }
            for(NMSMarker m : measureNMarkertexts){
                if(NavA)nMap.removeOverlay(m);
                m.setMap(null);
            }
            if(NavA)if(measureNLine!=null)nMap.removeOverlay(measureNLine);
            measureNMarkers.clear();
            measureMarkertexts.clear();
        }
        try{
            NavionicsMobileServices.disableDownloadAreaSelectorAndConfirm(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void deleteMarker(MenuItem item) {
        //displayMessage("To be implemented");
        map.getController().animateTo(((Marker)InfoWindow.getOpenedInfoWindowsOn(map).get(0).getRelatedObject()).getPosition());
        mDbHelper.deleteMarker(((Marker)InfoWindow.getOpenedInfoWindowsOn(map).get(0).getRelatedObject()).getId());
        InfoWindow.closeAllInfoWindowsOn(map);
        fetchedGrid= new BoundingBox(0,0,0,0);
        loadIcons();
        displayMessage(getResources().getString(R.string.marker_deleted));
        Toast.makeText(this,getResources().getString(R.string.marker_deleted),Toast.LENGTH_LONG).show();
    }

    public void downloadNavionics(View view) {
        findViewById(R.id.navionics_download).setVisibility(View.GONE);
        findViewById(R.id.close).setVisibility(View.GONE);
        new testInternet(this, new Callable<String>() { //Next get all location points
            public String call()  {
                NavionicsMobileServices.disableDownloadAreaSelectorAndConfirm(true);
                return null;
            }
        }).execute();


    }


    public void copyLatLong(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", ((TextView)view).getText().toString());
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
        displayMessage(getResources().getString(R.string.position_copied));
    }

    public void showMyMarkers(MenuItem item) {
        try {
            item.getSubMenu().clear();
            if (FirebaseAuth.getInstance().getCurrentUser()==null) {
                displayMessage(getResources().getString(R.string.not_logged_in));
                Toast.makeText(this,getResources().getString(R.string.not_logged_in),Toast.LENGTH_LONG).show();
                return;
            }
            ArrayList<String> markers = mDbHelper.getMyMarkers(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            item.setTitle(getResources().getString(R.string.my_markers) + " (" + markers.size() + ")");
            //Log.d("got markers:", markers.size() + "m");
            for (String m : markers) {
                //Log.d("marker",m);
                try {
                    String[] ms = m.split(":");
                    //if(ms.length<5)continue;
                    MenuItem i = item.getSubMenu().add(ms.length < 5 ? ms[0] : ms[4]);
                    i.setIcon(Symbols.get(ms[1]));
                    final GeoPoint g = new GeoPoint(Double.parseDouble(ms[2]), Double.parseDouble(ms[3]));
                    i.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            map.getController().setZoom(15.0);
                            map.getController().animateTo(g);
                            return false;
                        }
                    });
                }catch (Exception e){
                    //just move on to the next one
                }
            }
        }catch(Exception e){
            e.printStackTrace();
            displayMessage(e.getMessage());
        }
    }

    public void activateCode(View view) {
        try {
            activateCode(((EditText) ((LinearLayout) view.getParent().getParent()).getChildAt(4)).getText().toString());
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            ((PopupWindow) view.getParent().getParent().getParent().getParent().getParent()).dismiss();
        }catch (Exception e){
            Log.d("dasdad", e.getMessage());
            e.printStackTrace();
        }
    }

    public void getCode(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zuluwaterways.com/shop/"));
        Intent defaultBrowser = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_BROWSER);
        defaultBrowser.setData(browserIntent.getData());
        startActivity(defaultBrowser);
        //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zuluwaterways.com/map/")));
    }

    public void removeAllDownloads(MenuItem item) {
        downloadsLeft=-1;
    }

    public void rateApp(View view) {
        Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())));
        }
        if(!getSharedPreferences("MyPrefsFile", MODE_PRIVATE).contains("rated")){
            downloadsLeft+=25;
            getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit().putInt("rated", BuildConfig.VERSION_CODE).apply();
        }
    }

    public void closeHints(View view) {
        view.setVisibility(View.GONE);
    }

    public void closetute(View view) {
        Log.d("close","tute");
        findViewById(R.id.tute_container).setVisibility(View.GONE);
        final MainActivity app = this;
        //requestLocPermission();
        requestWritePermission( true);
        requestReadPermission();
        changeMap(satDatabase);
        loadIconData();
        loadIcons();
        ((WebView) findViewById(R.id.onlineWeb)).setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                changeMap(satDatabase);
                loadIconData();
                fetchedGrid = new BoundingBox(0,0,0,0);
                loadIcons();
            }
        }, 500);
        handler.postDelayed(new Runnable() {
            public void run() {
                changeMap(satDatabase);
                loadIconData();
                fetchedGrid = new BoundingBox(0,0,0,0);
                loadIcons();
            }
        }, 3000);
        //getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit().putInt("version_code", BuildConfig.VERSION_CODE).apply();
    }

    public void nextTute(View view) {
        if(((TextView)view).getText().toString().equals(getResources().getString(R.string.next))){
            tuteVP.setCurrentItem(tuteVP.getCurrentItem()+1);
//            if(tuteVP.getCurrentItem()==1){
//                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_out));
//                view.setClickable(false);
//            }
        }else{
            closetute(view);
        }
    }

    public void getStorePerm(View view) {
        getSharedPreferences("MyPrefsFile", MODE_PRIVATE).edit().putInt("prefs", BuildConfig.VERSION_CODE).apply();
        ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 2);
        //requestWritePermission();
    }

    public void getLocPerm(View view) {
        requestLocPermission();

    }

    public void cancelActivate(View view) {
        findViewById(R.id.codeActivateContainer).setVisibility(View.GONE);
    }

    public void validateCode(View view) {
        validateCode(((TextView)findViewById(R.id.activateCode)).getText().toString());
        findViewById(R.id.codeActivateContainer).setVisibility(View.GONE);
    }

    public void deleteMeasure(View view) {
        if (measureMarkers.size()==1){
            closeZoom(view);
        }else {
            map.getOverlayManager().remove(InfoWindow.getOpenedInfoWindowsOn(map).get(0).getRelatedObject());
            measureMarkers.remove(InfoWindow.getOpenedInfoWindowsOn(map).get(0).getRelatedObject());
            rebuildMeasure();
            InfoWindow.closeAllInfoWindowsOn(map);
        }
    }

    private static class testInternet extends AsyncTask<String, Void, String> {

        @SuppressLint("StaticFieldLeak")
        private final Context app;
        private final Callable<String> afters;

        testInternet(Context c, Callable<String> func) {
            afters = func;
            app=c;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL("https://www.zuluwaterways.com/");
                HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
                urlConnect.getContent();
            } catch (Exception e) {
                e.printStackTrace();
                if(e.getMessage().equals("https://www.zuluwaterways.com/api/users/5eede90f9e3e9e21a29754cf/")){
                    return "good";
                }else {
                    ((MainActivity) app).displayMessage(app.getResources().getString(R.string.no_internet));
                    Toast.makeText(getAppContext(),app.getResources().getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                }
                return "";
            }
            return "good";
        }
        protected void onPostExecute(String result){
            try {
                if(!result.isEmpty())afters.call();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private void showDownloadsDialog(){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.downloads_dialog, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(map, Gravity.CENTER, 0, 0);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.performClick();
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void checkFirstRun() {
        Log.d("checking","First Run");
        final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;
        int currentVersionCode = -1;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            currentVersionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);
        Log.d("first run:",currentVersionCode+":"+savedVersionCode);
        if (currentVersionCode == savedVersionCode) {
            Log.d("found","Not First Run");
            requestWritePermission(true);
            requestReadPermission();
        } else if (savedVersionCode == DOESNT_EXIST) {
            Log.d("found","First Run");
            getNewDB();
            showTute();
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }else {
            Log.d("first run after update",""+currentVersionCode);
            if(downloadsLeft<100)downloadsLeft=100;
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }

    }
    public void showTute(){
        findViewById(R.id.tute_container).setVisibility(View.VISIBLE);
        tuteVP = findViewById(R.id.view_pager);
        Layout_bars = findViewById(R.id.layoutBars);
        screens = new int[]{
                R.layout.intro_screen1,
                R.layout.intro_screen2,
                R.layout.intro_screen3,
                R.layout.intro_screen4,
                R.layout.intro_screen5,
                R.layout.intro_screen6,
                R.layout.intro_screen7
        };
        MyViewPagerAdapter myvpAdapter = new MyViewPagerAdapter();
        tuteVP.setAdapter(myvpAdapter);
        tuteVP.addOnPageChangeListener(viewPagerPageChangeListener);

        ColoredBars(0);
    }
    private boolean checkWriteExternalPermission()    {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    private boolean checkLocationPermission()    {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
    public void changeLang(MenuItem item){
        switch (item.getTitle().toString()){
            case  "Arabic" :{
                setLocale(this,new Locale("ar"));
                break;
            }
            case  "Chinese" :{
                setLocale(this,new Locale("zh"));
                break;
            }
            case  "Dutch" :{
                setLocale(this,new Locale("nl"));
                break;
            }
            case  "English" :{
                setLocale(this,new Locale("en"));
                break;
            }
            case  "Filipino" :{
                setLocale(this,new Locale("b+fil"));
                break;
            }
            case  "French" :{
                setLocale(this,new Locale("fr"));
                break;
            }
            case  "German" :{
                setLocale(this,new Locale("de"));
                break;
            }
            case  "Greek" :{
                setLocale(this,new Locale("el"));
                break;
            }
            case  "Hindi" :{
                setLocale(this,new Locale("hi","IN"));
                break;
            }
            case  "Indonesian" :{
                setLocale(this,new Locale("id","ID"));
                break;
            }
            case  "Italian" :{
                setLocale(this,new Locale("it","IT"));
                break;
            }
            case  "Malay" :{
                setLocale(this,new Locale("ms"));
                break;
            }
            case  "Spanish" :{
                setLocale(this,new Locale("es","ES"));
                break;
            }
            case  "Portuguese" :{
                setLocale(this,new Locale("pt"));
                break;
            }
            case  "Russian" :{
                setLocale(this,new Locale("ru"));
                break;
            }
            case  "Thai" :{
                setLocale(this,new Locale("th"));
                break;
            }
            case  "Vietnamese" :{
                setLocale(this,new Locale("vi","VN"));
                break;
            }
            case  "Zulu" :{
                setLocale(this,new Locale("zu"));
                break;
            }
            default:{
                setLocale(this,new Locale("en"));
            }

        }
    }

    @SuppressLint("ApplySharedPref")
    private void setLocale(Activity context, Locale l) {

        //Log.d("settingLoc",l.getLanguage());
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale.setDefault(l);
        config.setLocale(l);
        if(longp!=null)longp.getMenu().clear();
        if(longp!=null)longp.inflate(R.menu.long_menu);
        context.getBaseContext().getResources().updateConfiguration(config, context.getBaseContext().getResources().getDisplayMetrics());
        if(l.getLanguage().equals(currentLang.getLanguage())) return;
        currentLang = l;
        savePreferences();

        getPreferences(MODE_PRIVATE).edit().putString("language",currentLang.getLanguage()).commit();
        getPreferences(MODE_PRIVATE).edit().putString("languagec",currentLang.getCountry()).commit();

        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        if (mgr != null) {
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        }
        this.finish();
        System.exit(0);

    }

    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0) return;
        Log.d("perms","testing");
        if(requestTimeout--<0)return;
        requestWritePermission(false);
        requestReadPermission();
        //requestLocPermission();
    }

    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        loadPreferences();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                handleIntent(intent);
            }
        }, 500);
    }
    private void handleIntent(Intent intent) {
        String appLinkAction = intent.getAction();
        Uri appLinkData = intent.getData();
        if(appLinkData==null)return;
        Log.d("INNNNNNNNNNNNNNNNTT!!!", "hemmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmre");
        //Log.d("INNNNNNNNNNNNNNN!!!!!!!", ""+appLinkAction);
        Log.d("INNNNNNNNNNNNNNN!!!!!!!", ""+appLinkData.toString());
        //Log.d("INNNNNNNNNNNNNNN!!!!!!!", ""+appLinkData.getPathSegments().get(1));
        //Log.d("INNNNNNNNNNNNNNN!!!!!!!", ""+appLinkData.getEncodedQuery().substring(0,11));

        loadPreferences();
        try {
            if (Intent.ACTION_VIEW.equals(appLinkAction) && appLinkData.getPathSegments().size() > 2) {
                String iconID = appLinkData.getPathSegments().get(2);
                if(appLinkData.getPathSegments().get(1).equals("code")){
                    activateCode(iconID);
                    intent.setData(null);
                    return;
                }
                if (iconID.matches("\\d+(?:\\.\\d+)?")) {//may be a location rather than a point
                    getPreferences(MODE_PRIVATE).edit().putString("lat", appLinkData.getPathSegments().get(1)).apply();
                    getPreferences(MODE_PRIVATE).edit().putString("lon", appLinkData.getPathSegments().get(2)).apply();
                    getPreferences(MODE_PRIVATE).edit().putString("zed", "18.0").apply();
                } else {
                    ArrayList<String> details = mDbHelper.getLocationDataOld(iconID);
                    if (details != null && !details.isEmpty()) {
                        getPreferences(MODE_PRIVATE).edit().putString("lat", details.get(5)).apply();
                        getPreferences(MODE_PRIVATE).edit().putString("lon", details.get(6)).apply();
                        getPreferences(MODE_PRIVATE).edit().putString("zed", "18.0").apply();
                        pleaseOpen = details.get(0);
                        intent.setData(null);
                    }
                }
                //showRecipe(appData);
            }
            if(appLinkData.getPathSegments().get(1).equals("complete")){
                Log.d("INNNNNNNNNNNNNNN!!!!!!!", "running?");
                activateCode(appLinkData.getEncodedQuery().substring(0,11));
                intent.setData(null);
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void activatePack(String code) {
        displayMessage("Pack Downloading");
        res.delete(0,res.length());
        newDatabase = code.charAt(8)=='m'?streetDatabase:satDatabase;
        //Log.d("Activate pack",code);
        Uri uri=Uri.parse("https://www.zuluwaterways.com/offline/validatePack/?code="+code);
        //final String base = code.charAt(9)=='m'?streetDatabase:satDatabase;
        File f = new File(getExternalFilesDir("Downloads").getAbsolutePath() +"/temp.sqlite");
        if(f.exists())f.delete();
        //lastDownload=
        mgr.enqueue(new DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI |
                        DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(getString(R.string.zulu_map_pack))
                .setDescription(getString(R.string.downloading_map_pack))
                .setDestinationInExternalFilesDir(this,"Downloads","temp.sqlite")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        );

        multiDownloads = 1;
        findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
        ((ProgressBar)findViewById(R.id.mapDownloadProgress)).setProgress(0);
        checkDownload();
    }
    private void checkDownload(){
        if(multiDownloads==0)return;
        try {
            findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
            DownloadManager.Query q = new DownloadManager.Query();
            Cursor cursor = mgr.query(q);
            cursor.moveToFirst();
            int cl1 = cursor.getColumnIndex(COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int cl2 = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int bytes_downloaded = cl1!=-1?cursor.getInt(cl1):0;
            int total_bytes = cl2!=-2?cursor.getInt(cl2):0;
            cursor.close();
            int progress = (bytes_downloaded / 10) / ((total_bytes / 1000) + 1);
            if(progress>0)findViewById(R.id.mapDownload).setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(R.id.mapDownloadProgress)).setProgress(progress > ((ProgressBar) findViewById(R.id.mapDownloadProgress)).getProgress() ? progress : ((ProgressBar) findViewById(R.id.mapDownloadProgress)).getProgress());
            ((TextView) findViewById(R.id.mapDownloadInfo)).setText("Downloaded:" + (bytes_downloaded / 1000) / 1000.0 + "/" + ((total_bytes + 1) / 1000) / 1000.0 + "mb");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkDownload();
                }
            }, 300);
        }catch (Exception e){
            displayMessage(e.getMessage());
        }
    }
    private final BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            displayMessage("download Complete!") ;
            multiDownloads = 0;
            ((ProgressBar)findViewById(R.id.mapDownloadProgress)).setProgress(0);
            ((TextView)findViewById(R.id.mapDownloadInfo)).setText("");
            findViewById(R.id.mapDownload).setVisibility(View.GONE);
            String dlfn = getExternalFilesDir("Downloads")==null?getFilesDir().getAbsolutePath():getExternalFilesDir("Downloads").getAbsolutePath();
            File ndb = new File(dlfn + "/ndb.sqlite");
            if (ndb.exists() || ndb.length()>10000) {
                Log.d("#######NewDB!#####",ndb.getAbsolutePath());
                try {
                    SQLiteDatabase sqlitetemp = SQLiteDatabase.openOrCreateDatabase(dlfn + "/ndb.sqlite", null);
                    sqlitetemp.close();
                    pdb.importCurrent();
                    fetchedGrid = new BoundingBox(0,0,0,0);
                    loadIconData();
                    loadIcons();
                    Log.d("#######NewDB!#####","Import Complete!!!");
                }catch (Exception e){
                    displayMessage("Bad Sync. Please contact");
                    e.printStackTrace();
                }finally {
                    ndb.delete();
                }
                return;
            }
            //Log.d("path",getExternalFilesDir("Downloads").getAbsolutePath() + "/temp.sqlite");
            File tp = new File(dlfn + "/temp.sqlite");
            if (!tp.exists() || tp.length()<1000000) {
                displayMessage(getString(R.string.no_pack));
                Log.d("############",tp.getAbsolutePath());
                return;
            }
            try {
                SQLiteDatabase sqlitetemp = SQLiteDatabase.openOrCreateDatabase(dlfn + "/temp.sqlite", null);
                sqlitetemp.close();
                SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(newDatabase, null);
                sqliteDB.execSQL("ATTACH DATABASE '" + dlfn + "/temp.sqlite" + "' AS New_DB");
                sqliteDB.execSQL("INSERT OR IGNORE INTO tiles SELECT * FROM New_DB.tiles ;");
                sqliteDB.close();
                displayMessage(getString(R.string.pack_added));
            }catch (Exception e){
                displayMessage("Corrupt Pack! Please contact us for help");
            }finally {
                tp.delete();
            }
        }
    };

    private final BroadcastReceiver onNotificationClick=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Downloading Pack.", Toast.LENGTH_LONG).show();
        }
    };

    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            ColoredBars(position);
            try {
                //((CheckBox) findViewById(R.id.permission1)).setChecked(checkWriteExternalPermission());
                //((CheckBox) findViewById(R.id.permission2)).setChecked(checkLocationPermission());
            }catch (Exception e){}
            if(position>1 && findViewById(R.id.permission1)!=null &&!((CheckBox)findViewById(R.id.permission1)).isChecked())tuteVP.setCurrentItem(1);
            if (position == screens.length - 1) {
                ((TextView)findViewById(R.id.tutenext)).setText(R.string.lets_go);
                //findViewById(R.id.tuteskip).setVisibility(View.GONE);
            } else {
                ((TextView) findViewById(R.id.tutenext)).setText(R.string.next);
                findViewById(R.id.tutenext).setVisibility(View.VISIBLE);
                findViewById(R.id.tuteskip).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private void ColoredBars(int thisScreen) {
        TextView[] bottomBars = new TextView[screens.length];
        Layout_bars.removeAllViews();
        for (int i = 0; i < bottomBars.length; i++) {
            bottomBars[i] = new TextView(this);
            bottomBars[i].setTextSize(100);
            bottomBars[i].setText(Html.fromHtml("¯"));
            Layout_bars.addView(bottomBars[i]);
            bottomBars[i].setTextColor(Color.BLUE);
        }
        if (bottomBars.length > 0)
            bottomBars[thisScreen].setTextColor(Color.RED);
    }

    class MyViewPagerAdapter extends PagerAdapter {

        MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            View view = inflater.inflate(screens[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return screens.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View v = (View) object;
            container.removeView(v);
        }

        @Override
        public boolean isViewFromObject(View v, Object object) {
            return v == object;
        }
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), 7);
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
}
