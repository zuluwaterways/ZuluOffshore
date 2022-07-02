package com.zulu.offshore;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

class HTTPGetRequest extends AsyncTask<String, Void, String> {
    public static final String REQUEST_METHOD = "GET";
    public static final int READ_TIMEOUT = 25000;
    public static final int CONNECTION_TIMEOUT = 25000;


    private final Callable<String> afters;
    private final StringBuilder res;




    public HTTPGetRequest(StringBuilder s, Callable<String> func ){
        afters = func;
        res = s;
    }

    @Override
    protected String doInBackground(String... params) {
        String stringUrl = params[0];
        String result;
        String inputLine;

        if (params.length>1) {
            if (params[1].equals( "testLogin")) {
                try {
                    URL myUrl = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                    connection.setRequestProperty("authorization", params[2]);
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        res.append(line);
                    }
                    serverAnswer.close();
                    Log.d("Sucesss",res.toString());
                    return res.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (params[1].equals( "pic")) {
                try {
                    InputStream in = new URL(stringUrl).openConnection().getInputStream();
                    Bitmap profilePic = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    profilePic.compress(Bitmap.CompressFormat.JPEG, 75, stream);
                    String path =  params[3] + "/.image" + "/";
                    File fileDir = new File(path);
                    if (!fileDir.isDirectory())
                        fileDir.mkdirs();
                    //Log.d("creating file",fileDir + "/"+params[2]+"");
                    File bitmapDir = new File(fileDir + "/" + params[2] + "");
                    bitmapDir.createNewFile();
                    FileOutputStream streams = new FileOutputStream(bitmapDir);
                    streams.write(stream.toByteArray());
                    streams.close();
                    return path;

                } catch (Exception e) {
                    System.out.println("pic: " + e.toString());
                }
            }
            if (params[1].equals("png")) {
                try {
                    InputStream in = new URL(stringUrl).openConnection().getInputStream();
                    Bitmap profilePic = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    profilePic.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    String path = params[3] + "/.image" + "/";
                    File fileDir = new File(path);
                    if (!fileDir.isDirectory())
                        fileDir.mkdirs();
                    File bitmapDir = new File(fileDir + "/" + params[2]);
                    bitmapDir.createNewFile();
                    Log.d("Created Pic",""+fileDir + "/"+params[2]);
                    FileOutputStream streams = new FileOutputStream(bitmapDir);
                    streams.write(stream.toByteArray());
                    streams.close();
                    return path;

                } catch (Exception e) {
                    System.out.println("pic: " + e.toString());
                }
            }
            if (params[1].equals("tile")) {
               // Log.d("sat update", "jpg");
                try {
                   // Log.d("sat update","getting image"+params[2]);
                    URLConnection con = new URL(stringUrl).openConnection();
                    String etag = con.getHeaderField("ETag");
                    if(etag.equals("\"vvvvvvvvvvvvf\""))return "nope";
                    Log.d("etag",etag);
                    InputStream in = con.getInputStream();
                    Bitmap pic = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    pic.compress(params[3].equals("png")? Bitmap.CompressFormat.PNG: Bitmap.CompressFormat.JPEG,90, stream);
                    byte[] imageInByte = stream.toByteArray();
                   // Log.d("sat update","got image:"+imageInByte.length)

                    //MainActivity.insertSatData(Long.parseLong(params[2]), imageInByte,params[4],params[5]);
                   // Log.d("sat update","written");

                    SQLiteDatabase sqliteDB = SQLiteDatabase.openOrCreateDatabase(params[4], null);
                    ContentValues values = new ContentValues();
                    values.put("key", Long.parseLong(params[2]));
                    values.put("provider", params[5]);
                    values.put("tile", imageInByte);
                    //Log.d("inserting",values.toString());
                    long ir  = sqliteDB.insert("tiles",null,values);
                    if(ir<1){ //didnt insert as data already exists
                        Log.d("insert","Failed");
                        long v = sqliteDB.update("tiles",values, "key=?", new String[] {Long.parseLong(params[2])+""});
                        Log.d("insert","try2:"+v);
                    }
                    sqliteDB.close();
                    return null;

                } catch (Exception e) {
                    System.out.println("pic: " + e.toString());
                }
            }
            if (params[1].equals( "json")) {
                try {
                    URL myUrl = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection)
                            myUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                   // Log.d("sending",stringUrl);
                   // Log.d("sending",params[2]);
                    connection.setReadTimeout(READ_TIMEOUT);
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);
                    connection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write(params[2]);  //<--- sending data.
                    wr.flush();
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        res.append(line);
                    }
                    wr.close();
                    serverAnswer.close();
                    return res.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (params[1].equals( "activate")) {
                try {

                    URL myUrl = new URL(stringUrl+params[2]);
                    HttpURLConnection connection = (HttpURLConnection)myUrl.openConnection();
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);
                   // Log.d("sending",stringUrl+params[2]);
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        res.append(line);
                    }
                    serverAnswer.close();
                    return res.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (params[1].trim().equals("addLocation")) {
                try {
                    URL myUrl = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("authorization", params[10]);
                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write("lat="+params[2]+"&lon="+params[3]+"&user="+params[4]+"&type="+params[5]+"&title="+params[6]+"&pics="+params[7]+"&tags="+params[8]+"&desc="+params[9]);  //<--- sending data.
                    wr.flush();
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        Log.d("Server Resonse", line);
                        res.append(line);
                    }
                    wr.close();
                    serverAnswer.close();
                    return res.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (params[1].trim().equals("addComment")) {
                try {
                    URL myUrl = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        res.append(line);
                    }
                    serverAnswer.close();
                    Log.d("Sucesss",res.toString());
                    return res.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (params[1].trim().equals("addPic")) {
                try {
                    URL myUrl = new URL(stringUrl);

                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/octet-stream");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");
                OutputStream outputStream = connection.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(params[2]);  //<--- sending data
                wr.flush();
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        Log.d("pic Server Resonse", line);
                        res.append(line);
                    }
                wr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (params[1].trim().equals("updateHistory")) {
                try {
                    URL myUrl = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write("id="+params[2]+"&type="+params[3]+"&user="+params[4]+"&data="+params[5]);  //<--- sending data.
                    wr.flush();
                    BufferedReader serverAnswer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = serverAnswer.readLine()) != null) {
                        Log.d("Server Resonse", line);
                        res.append(line);
                    }
                    wr.close();
                    serverAnswer.close();
                    return res.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                if(params[1].equals("update")){
                   // Log.d("updating", params[0]);
                    StringBuilder b = new StringBuilder();
                    try {
                        URL myUrl = new URL(params[0]);//Create a connection
                        HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                        connection.setRequestMethod(REQUEST_METHOD);
                        connection.setReadTimeout(READ_TIMEOUT);
                        connection.setConnectTimeout(CONNECTION_TIMEOUT);
                        connection.connect();
                        //Log.d("received",connection.getContentLength()+" bytes");
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        BufferedReader reader = new BufferedReader(streamReader);

                        StringBuffer buffer = new StringBuffer();
                        String line = "";
                        Log.d("received",streamReader.getEncoding()+"");
                        while ((inputLine = reader.readLine()) != null) {
                            b.append(inputLine);
                        }
                        Log.d("received","length:"+b.length());
                        reader.close();
                        streamReader.close();
                        //Log.d("received",buffer.toString());
                        return b.toString();

                    }catch (Exception e){
                        Log.d("received in error","length:"+b.length());
                        e.printStackTrace();
                    }
                }


            } else {
                try {                    //Create a URL object holding our url
                    URL myUrl = new URL(stringUrl);
                    //Create a connection
                    HttpURLConnection connection = (HttpURLConnection)
                            myUrl.openConnection();
                    //Set methods and timeouts
                    connection.setRequestMethod(REQUEST_METHOD);
                    connection.setReadTimeout(READ_TIMEOUT);
                    connection.setConnectTimeout(CONNECTION_TIMEOUT);
                    //Connect to our url
                    connection.connect();
                    result = "";
                    if (res != null) {
                        //Create a new InputStreamReader
                        InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                        //Create a new buffered reader and String Builder
                        BufferedReader reader = new BufferedReader(streamReader);
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((inputLine = reader.readLine()) != null) {
                            stringBuilder.append(inputLine);
                        }
                        //Close our InputStream and Buffered reader
                        reader.close();
                        streamReader.close();
                        //Set our result equal to our stringBuilder
                        result = stringBuilder.toString();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    result = null;
                }
                return result;
            }
        return "";
    }


    protected void onPostExecute(String result){
        try {
            if(res!=null) {
                res.delete(0, res.length());
                res.append(result);
            }
            afters.call();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

   }