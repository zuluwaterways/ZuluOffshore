package com.app.zuluwaterways;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.util.BoundingBox;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Callable;

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "zo.db";
    private static final String LOCATIONS_TABLE_NAME = "Locations";
    private static final String LOCATIONS_DATA_TABLE_NAME = "LocationsData";
    private static final String LOCATIONS_TYPE_TABLE_NAME = "LocationsType";
    private static final String PICTURES_TABLE_NAME = "Pictures";
    private static final String COMMENTS_TABLE_NAME = "Comments";
    private static final String SYNC_TABLE_NAME = "Sync";
    private final Map LocationTypeID = new HashMap<String,String>();
    //private final Map LocationIcon = new HashMap<String,String>();
    public Map Locations = new HashMap<String,String>();
    private final int dlLeft = 0;
    private final StringBuilder res = new StringBuilder();

    private final Context cc;
    private int toSyncCount = 0;

    private final SQLiteDatabase db;

    DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
        cc = context;
        db = this.getWritableDatabase();
        db.execSQL("create table if not exists markers (id character(25) UNIQUE NOT NULL,lat numeric NOT NULL,lon numeric NOT NULL,type character(25) NOT NULL,title text,description text,date date,usr text,tags text DEFAULT '',likes integer DEFAULT 0, pictures text DEFAULT '')");
        db.execSQL("create table if not exists comments (id character(25) UNIQUE NOT NULL, marker character(25),date date,comment text,user character(100))");
        db.execSQL("create table if not exists types (ids character(25) UNIQUE NOT NULL, name text,data blob)");
        db.execSQL("create table if not exists sync (id character(25) UNIQUE NOT NULL, type character(1))");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+LOCATIONS_TABLE_NAME);
        onCreate(db);
    }

    public void importCurrent(){

        db.execSQL("drop table if exists markers");
        db.execSQL("drop table if exists comments");
        db.execSQL("drop table if exists types");
        db.execSQL("drop table if exists sync");

        db.execSQL("create table if not exists markers (id character(25) UNIQUE NOT NULL,lat numeric NOT NULL,lon numeric NOT NULL,type character(25) NOT NULL,title text,description text,date date,usr text,tags text DEFAULT '',likes integer DEFAULT 0, pictures text DEFAULT '')");
        db.execSQL("create table if not exists comments (id character(25) UNIQUE NOT NULL, marker character(25),date date,comment text,user character(100))");
        db.execSQL("create table if not exists types (ids character(25) UNIQUE NOT NULL, name text,data blob)");
        db.execSQL("create table if not exists sync (id character(25) UNIQUE NOT NULL, type character(1))");
        //db.execSQL("DETACH DATABASE New_DB;");
        db.execSQL("ATTACH DATABASE '" + cc.getExternalFilesDir("Downloads").getAbsolutePath() + "/ndb.sqlite" + "' AS New_DB");
        db.execSQL("INSERT OR IGNORE INTO markers SELECT * FROM New_DB.markers ;");
        db.execSQL("INSERT OR IGNORE INTO comments SELECT * FROM New_DB.comments ;");
        db.execSQL("INSERT OR IGNORE INTO types SELECT * FROM New_DB.types ;");
        db.execSQL("DETACH DATABASE New_DB;");
        db.execSQL("CREATE UNIQUE INDEX markersID ON markers (id);");
        db.execSQL("CREATE INDEX markerslatlon ON markers (lat,lon);");
    }
    public void importex(String file){

        db.execSQL("drop table if exists markers");
        db.execSQL("drop table if exists comments");
        db.execSQL("drop table if exists types");
        db.execSQL("drop table if exists sync");

        db.execSQL("create table if not exists markers (id character(25) UNIQUE NOT NULL,lat numeric NOT NULL,lon numeric NOT NULL,type character(25) NOT NULL,title text,description text,date date,usr text,tags text DEFAULT '',likes integer DEFAULT 0, pictures text DEFAULT '')");
        db.execSQL("create table if not exists comments (id character(25) UNIQUE NOT NULL, marker character(25),date date,comment text,user character(100))");
        db.execSQL("create table if not exists types (ids character(25) UNIQUE NOT NULL, name text,data blob)");
        db.execSQL("create table if not exists sync (id character(25) UNIQUE NOT NULL, type character(1))");
        //db.execSQL("DETACH DATABASE New_DB;");
        db.execSQL("ATTACH DATABASE '" + file + "' AS New_DB");
        db.execSQL("INSERT OR IGNORE INTO markers SELECT * FROM New_DB.markers ;");
        db.execSQL("INSERT OR IGNORE INTO comments SELECT * FROM New_DB.comments ;");
        db.execSQL("INSERT OR IGNORE INTO types SELECT * FROM New_DB.types ;");
        db.execSQL("DETACH DATABASE New_DB;");
        db.execSQL("CREATE UNIQUE INDEX markersID ON markers (id);");
        db.execSQL("CREATE INDEX markerslatlon ON markers (lat,lon);");
    }

    @SuppressLint("Range")
    public ArrayList<String> getLocations(BoundingBox b){//double x1, double y1, double x2, double y2) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<>();
        //Long tic = System.currentTimeMillis();
        Cursor res = db.rawQuery( "select * from markers WHERE lat>"+b.getLatSouth()+" AND lat<"+b.getLatNorth() +" AND lon>"+b.getLonWest()+" AND lon<"+b.getLonEast()+" order by date, id asc limit 500", null );
        Log.d("bounding",""+ b);
        //Log.d("toc",""+tic);
        res.moveToFirst();
        while(!res.isAfterLast()) {
            //if(res.getColumnIndex("lat")>=0)
            array_list.add(res.getFloat(res.getColumnIndex("lat"))+":"+res.getFloat(res.getColumnIndex("lon"))+":"+res.getString(res.getColumnIndex("type")).trim()+":"+res.getString(res.getColumnIndex("id")));
            res.moveToNext();
        }
        //Log.d("found markerd",array_list.toString());
        res.close();
        return array_list;
    } //fetch locations within a boundign box. called every map move
    public Map<String,String> getTypesNameMap(){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String,String> out = new HashMap<>();
        Cursor res = db.rawQuery( "select * from types" , null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            out.put(""+res.getString(res.getColumnIndex("ids")).trim(),res.getString(res.getColumnIndex("name")).trim());
            res.moveToNext();
        }

        res.close();
        return out;
    }
    public Map<String,byte[]> getTypesIconMap(){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String,byte[]> out = new HashMap<>();
        Cursor res = db.rawQuery( "select * from types" , null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            out.put(""+res.getString(res.getColumnIndex("ids")).trim(),res.getBlob(res.getColumnIndex("data")));
            res.moveToNext();
        }
        res.close();
        return out;
    }




    public void doUpdate(JSONArray nv){
        try {
            //db.beginTransaction();
            JSONArray markers = nv.getJSONArray(0);
            JSONArray coms = nv.getJSONArray(1);
            for(int i=0;i<markers.length();i++){
                JSONObject o = markers.getJSONObject(i);
                SQLiteStatement stmt = db.compileStatement("INSERT OR REPLACE into markers VALUES (?,?,?,?,?,?,?,?,?,?,?)");
                stmt.bindString(1, o.has("id")?""+o.getString("id").trim():"");
                stmt.bindString(2, o.has("lat")?""+o.getString("lat").trim():"");
                stmt.bindString(3, o.has("lon")?""+o.getString("lon").trim():"");
                stmt.bindString(4, o.has("type")?""+o.getString("type").trim():"");
                stmt.bindString(5, o.has("title")?""+o.getString("title").trim():"");
                stmt.bindString(6, o.has("description")?""+o.getString("description").trim():"");
                stmt.bindString(7, o.has("date")?""+o.getString("date").trim():"");
                stmt.bindString(8, o.has("usr")?""+o.getString("usr").trim():"");
                stmt.bindString(9, o.has("tags")?""+o.getString("tags").trim():"");
                stmt.bindString(10, o.has("likes")?""+o.getString("likes").trim():"");
                stmt.bindString(11, o.has("pictures")?""+o.getString("pictures").trim():"");
                stmt.executeInsert();
                Log.d("inseting marker:", o +"");
            }
            for(int i=0;i<coms.length();i++){
                JSONObject o = coms.getJSONObject(i);
                SQLiteStatement stmt = db.compileStatement("INSERT OR REPLACE into comments VALUES (?,?,?,?,?)");
                //String sql = "INSERT OR REPLACE into comments VALUES ('"+o.getString("id")+"','"+o.getString("marker")+"','"+o.getString("date")+"','"+o.getString("comment")+"','"+o.getString("user")+"')";
                //SQLiteStatement stmt = db.compileStatement(sql);
                stmt.bindString(1, o.has("id")?""+o.getString("id").trim():"");
                stmt.bindString(2, o.has("marker")?""+o.getString("marker").trim():"");
                stmt.bindString(3, o.has("date")?""+o.getString("date").trim():"");
                stmt.bindString(4, o.has("comment")?""+o.getString("comment").trim():"");
                stmt.bindString(5, o.has("user")?""+o.getString("user").trim():"");
                stmt.executeInsert();
                //Log.d("inseting comment:",sql);
            }
            //db.endTransaction();

        } catch (Exception e) {
            e.printStackTrace();
            db.endTransaction();
            //Log.d("got upto",offset+"");
        }
        try {
            ((MainActivity)cc).displayMessage(cc.getResources().getString(R.string.sync_complete));
            ((ProgressBar)((MainActivity)cc).findViewById(R.id.syncProgressBar)).incrementProgressBy(10);
            ((MainActivity)cc).loadPreferences();
            ((MainActivity)cc).loadIconData();
            ((MainActivity)cc).loadIcons();
            ((MainActivity)cc).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            ((MainActivity)cc).findViewById(R.id.syndOverlay).setVisibility(View.GONE);
            ((MainActivity)cc).findViewById(R.id.fab).setVisibility(View.VISIBLE);
            ((MainActivity)cc).findViewById(R.id.locNow).setVisibility(View.VISIBLE);
            ((MainActivity)cc).findViewById(R.id.layerSelect).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public ArrayList<String> getComments(String id){
        //Log.d("getting comments","'"+id+"',");
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> out = new ArrayList<>();
        Cursor c = db.rawQuery( "select * from comments WHERE trim(marker) ='" + id.trim() +"'", null );
        c.moveToFirst();
        while(!c.isAfterLast()) {
            out.add(c.getString(c.getColumnIndex("date")) +";"+c.getString(c.getColumnIndex("user")) +";" + c.getString(c.getColumnIndex("comment")));
            c.moveToNext();
        }
        //Log.d("got comments",out.toString());
        c.close();
        return out;
    }


    private Map<String,String> getLocationIDMap(){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String,String> out = new HashMap<>();
        Cursor res = db.rawQuery( "select * from "+LOCATIONS_DATA_TABLE_NAME , null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            out.put(""+res.getInt(res.getColumnIndex("id")),res.getString(res.getColumnIndex("oldID")));
            res.moveToNext();
        }
        res.close();
        return out;
    }



    private Map<String,String> getLocationTypesIDeMap(){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String,String> out = new HashMap<>();
        Cursor res = db.rawQuery( "select * from "+LOCATIONS_TYPE_TABLE_NAME , null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            out.put(""+res.getInt(res.getColumnIndex("id")),res.getString(res.getColumnIndex("oldID")));
            res.moveToNext();
        }
        res.close();
        return out;
    }
    private Map<String,String> getSyncMap(){
        SQLiteDatabase db = this.getReadableDatabase();
        Map<String,String> out = new HashMap<>();
        Cursor res = db.rawQuery( "select * from sync" , null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            out.put(res.getString(res.getColumnIndex("id")),res.getString(res.getColumnIndex("type")));
            res.moveToNext();
        }
        res.close();
        return out;
    }
    public String generateID(){
        return Base64.encodeToString((UUID.randomUUID()+"").getBytes(), Base64.DEFAULT).substring(4,29);
    }

    public ArrayList<String> getLocationData(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from markers WHERE id='"+id+"'", null );
        res.moveToFirst();
        if(res.getCount()==0)return array_list;
        try {
            array_list.add(res.getString(res.getColumnIndex("title")));
            array_list.add(res.getString(res.getColumnIndex("date")));
            array_list.add(res.getString(res.getColumnIndex("description")));
            array_list.add(res.getString(res.getColumnIndex("id")));
            array_list.add(res.getString(res.getColumnIndex("pictures")));
            array_list.add(res.getString(res.getColumnIndex("usr")));
            array_list.add(res.getString(res.getColumnIndex("lat")));
            array_list.add(res.getString(res.getColumnIndex("lon")));
            array_list.add(res.getString(res.getColumnIndex("type")));
            array_list.add(res.getString(res.getColumnIndex("tags")));
        }catch (Exception e){
            e.printStackTrace();
        }
        res.close();
        return array_list;
    }
    public ArrayList<String> getLocationDataOld(String oldid){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from markers WHERE id='"+oldid+"'", null );
        if(res==null)return null;
        res.moveToFirst();
        array_list.add(res.getString(res.getColumnIndex("id")));
        array_list.add(res.getString(res.getColumnIndex("id")));
        array_list.add(res.getString(res.getColumnIndex("title")));
        array_list.add(res.getString(res.getColumnIndex("date")));
        array_list.add(res.getString(res.getColumnIndex("description")));
        array_list.add(res.getString(res.getColumnIndex("lat")));
        array_list.add(res.getString(res.getColumnIndex("lon")));
        array_list.add(res.getString(res.getColumnIndex("type")));
        res.close();
        return array_list;
    }
    private ArrayList<String> getLocation(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from markers WHERE id='"+id+"'", null );
        res.moveToFirst();
        array_list.add(res.getString(res.getColumnIndex("lat")));
        array_list.add(res.getString(res.getColumnIndex("lon")));
        array_list.add(res.getString(res.getColumnIndex("type")));
        res.close();
        return array_list;
    }
    private ArrayList<String> getComment(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from comments WHERE id='"+id+"'", null );
        res.moveToFirst();
        array_list.add(res.getString(res.getColumnIndex("marker")));
        array_list.add(res.getString(res.getColumnIndex("date")));
        array_list.add(res.getString(res.getColumnIndex("comment")));
        array_list.add(res.getString(res.getColumnIndex("user")));
        res.close();
        return array_list;
    }


    public boolean addMarker(String lat, String lon, String title, String desc, String type, String comment, Bitmap pic1, Bitmap pic2, Bitmap pic3,String user) {


        try {
            String newID = generateID();
            Cursor s  =db.rawQuery("SELECT * from types WHERE trim(name)='"+type.trim()+"'",null);
            s.moveToFirst();
            String typeID = s.getString(0);
            s.close();
//            Log.d("date format",res.getString(res.getColumnIndex("DateModified")));
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());
            String pics = "";
            ContentValues contentValues;
            if(pic1!=null) {
                String p1 = generateID() + ".jpg";
                FileOutputStream out = new FileOutputStream(cc.getFilesDir() + "/.image" + "/" + p1);
                pic1.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();
                pics = p1;
            }
            if(pic2!=null) {
                String p2 = generateID() + ".jpg";
                FileOutputStream out = new FileOutputStream(cc.getFilesDir() + "/.image" + "/" + p2);
                pic2.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();
                pics= pics.equals("") ?p2:(pics+":"+p2);
            }
            if(pic3!=null) {
                String p3 = generateID() + ".jpg";
                FileOutputStream out = new FileOutputStream(cc.getFilesDir() + "/.image" + "/" + p3);
                assert pic1 != null;
                pic1.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.close();
                pics= pics.equals("") ?p3:(pics+":"+p3);
            }
            contentValues = new ContentValues();
            contentValues.put("id", newID);
            contentValues.put("lat", lat);
            contentValues.put("lon", lon);
            contentValues.put("type", typeID);
            contentValues.put("title",title);
            contentValues.put("description",desc);
            contentValues.put("date",nowAsISO);
            contentValues.put("usr",user);
            contentValues.put("tags","");
            contentValues.put("likes",0);
            contentValues.put("pictures",pics);
            db.insert("markers", null, contentValues);

            contentValues = new ContentValues();
            contentValues.put("id",newID);
            contentValues.put("type","m");
            db.insert("sync", null, contentValues);

            if(!comment.trim().isEmpty()) {
                contentValues = new ContentValues();
                String cid=generateID();
                contentValues.put("id", cid);
                contentValues.put("marker", newID);
                contentValues.put("date", nowAsISO);
                contentValues.put("comment", comment);
                contentValues.put("user", user);
                db.insert("comments", null, contentValues);
                contentValues = new ContentValues();
                contentValues.put("id",cid);
                contentValues.put("type","c");
                db.insert("sync", null, contentValues);
            }
        }catch (Exception e){
            e.printStackTrace();

            return false;
        }
        return true;
    }

    public boolean addComment(String id, String data,String user) {
        try{
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setTimeZone(tz);
            String nowAsISO = df.format(new Date());
            ContentValues contentValues = new ContentValues();
            String cid=generateID();
            contentValues.put("id", cid);
            contentValues.put("marker", id);
            contentValues.put("date", nowAsISO);
            contentValues.put("comment", data);
            contentValues.put("user", user);
            db.insert("comments", null, contentValues);
            contentValues = new ContentValues();
            contentValues.put("id",cid);
            contentValues.put("type","c");
            db.insert("sync", null, contentValues);

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean SyncData(final String token, final String userID) {
        res.delete(0,res.length());
        if(userID=="forced"){
            ((MainActivity) cc).getNewDB();
            if(db.inTransaction())db.endTransaction();
            return true;
        }
        Map<String,String> toSync = getSyncMap();
        if (toSync.size()==0){//nothing to sync
            ((ProgressBar)((MainActivity)cc).findViewById(R.id.syncProgressBar)).setProgress(20);
            db.execSQL("delete from sync");
            Log.d("update","Skipping upload as nothing found");
            ((MainActivity)cc).updateDB();
            ((MainActivity) cc).loadIconData();
            ((MainActivity) cc).loadIcons();
            return false;
        }
        Log.d("Sync","token:"+token);
        if(userID.trim().isEmpty()){
            ((MainActivity) cc).showLogin();
            ((MainActivity) cc).displayMessage(cc.getResources().getString(R.string.login_greeting));
            return true; //dont sync the database if the user has added things and is not logged in
        }
        Log.d("To Sync","almost:"+ Arrays.toString(toSync.keySet().toArray()));
        toSyncCount = 0;

        try{
            for(int i = 0;i<toSync.size();i++){
                //Log.d("Sync",""+toSync.entrySet().toArray()[i].toString());
                if(toSync.values().toArray()[i].toString().equals("m")){
                    uploadMarker(toSync.keySet().toArray()[i].toString());
                    toSyncCount++;
                }
                if(toSync.values().toArray()[i].toString().equals("c")){
                    uploadComment(toSync.keySet().toArray()[i].toString());
                    toSyncCount++;
                }
            }
            db.execSQL("delete from sync");
            ((MainActivity) cc).updateDB();
            if(db.inTransaction())db.endTransaction();
            ((MainActivity) cc).loadIconData();
            ((MainActivity) cc).loadIcons();
            return false;

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this.cc,"Error Syncing Changes",Toast.LENGTH_LONG).show();
            if(userID.equals("forced")){
                Log.d("forced sync!!!","@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                db.execSQL("delete from " + SYNC_TABLE_NAME);
                Toast.makeText(this.cc,"Upload Complete, Starting Download",Toast.LENGTH_LONG).show();
                ((ProgressBar) ((MainActivity) cc).findViewById(R.id.syncProgressBar)).setProgress(20);
                ((MainActivity) cc).getNewDB();
                ((MainActivity) cc).loadIconData();
                ((MainActivity) cc).loadIcons();
                ((MainActivity)cc).displayMessage(cc.getResources().getString(R.string.sync_complete));
                Toast.makeText(this.cc,"Sync Complete!",Toast.LENGTH_LONG).show();
                //db.close();
                if(db.inTransaction())db.endTransaction();
            }
        }

//        }
        return true;
    }
    public String getLastdate(){
        Cursor res = db.rawQuery( "select date from markers order by date desc limit 1", null );
        if(((res == null) || (res.getCount() <= 0)))return "";
        res.moveToFirst();
        int col = res.getColumnIndex("date");
        if(col==-1)col = 0;
        String result = res.getString(col);
        res.close();
        return result;
    }
    private void uploadMarker(String id){
        Log.d("To Sync marker",id);
        final ArrayList markerData = getLocationData(id);
        Log.d("To Sync marker",markerData.toString());
        if(!markerData.get(4).toString().isEmpty()&& PreferenceManager.getDefaultSharedPreferences(cc.getApplicationContext()).getBoolean("sync_pictures",true)) for (String n:markerData.get(4).toString().split(":")) uploadPicture(n);
        new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
            public String call() throws Exception {
                new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
                    public String call() throws Exception {
                        return null;
                    }
                }).execute("https://api.zuluwaterways.com/updateHistory.php",
                        "updateHistory",res.toString(),"n",markerData.get(5).toString(),"androidApp"
                );
                return null;
            }
        }).execute("https://api.zuluwaterways.com/addMarker.php",
                "addLocation",
                markerData.get(6).toString().trim(),
                markerData.get(7).toString().trim(),
                markerData.get(5).toString().trim(),
                markerData.get(8).toString().trim(),
                markerData.get(0).toString().trim(),
                markerData.get(4).toString().trim(),
                markerData.get(9).toString().trim(),
                markerData.get(2).toString().trim(),
                ((MainActivity) cc).getResources().getString(R.string.token)
        );

    }
    private void uploadComment(String id){
        ArrayList commentData = getComment(id);
        Log.d("uploading comment","'"+commentData.get(0).toString()+", "+commentData.get(3).toString()+", "+
                commentData.get(2).toString());
        new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
            public String call() throws Exception {
                return null;
            }
        }).execute("https://api.zuluwaterways.com/comment.php?marker="+commentData.get(0).toString()+"&user="+commentData.get(3).toString()+"&com="+commentData.get(2).toString(),
                "addComment"
        );
        new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
            public String call() throws Exception {
                return null;
            }
        }).execute("https://api.zuluwaterways.com/updateHistory.php",
                "updateHistory",commentData.get(0).toString(),"c",commentData.get(3).toString(),commentData.get(2).toString()
        );
    }
    private void uploadPicture(String name){
        Log.d("do to","upload picture"+name);
        Bitmap bm = BitmapFactory.decodeFile(cc.getFilesDir() + "/.image" + "/" + name);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
        byte[] b = baos.toByteArray();
        String b64img = "data:image/jpeg;base64,"+Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("do to","upload picture"+b64img.length());
        new HTTPGetRequest(res, new Callable<String>() { //Next get all location points
            public String call() throws Exception {
                return null;
            }
        }).execute("https://api.zuluwaterways.com/uploadImg.php?tk=8oacyn5si4l6ej6yx4o9vy698s3bzn5ylmc2&fn="+name,"addPic",b64img);
    }

    public void deleteMarker(String id) {
        try{
            db.delete("markers","trim(id)="+id.trim(),null);
            db.delete("sync","trim(newId)="+id.trim(),null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<String> getMyMarkers(String userID) {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from markers where usr ='"+userID+"' order by date desc", null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            array_list.add(res.getInt(res.getColumnIndex("id"))+":"+
                    res.getInt(res.getColumnIndex("type"))+":"+
                    res.getDouble(res.getColumnIndex("lat"))+":"+
                    res.getDouble(res.getColumnIndex("lon"))+":"+
                    res.getString(res.getColumnIndex("title")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public ArrayList<String> searchMarkers(String search) {
        ArrayList<String> array_list = new ArrayList<>();
        Cursor res = db.rawQuery( "select * from markers where Title like '%"+search.replaceAll("[^A-Za-z0-9]", "%")+"%' order by id desc limit 100", null );
        res.moveToFirst();
        while(!res.isAfterLast()) {
            array_list.add(res.getInt(res.getColumnIndex("id"))+":"+
                    res.getInt(res.getColumnIndex("type"))+":"+
                    res.getDouble(res.getColumnIndex("lat"))+":"+
                    res.getDouble(res.getColumnIndex("lon"))+":"+
                    res.getString(res.getColumnIndex("title")));
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}