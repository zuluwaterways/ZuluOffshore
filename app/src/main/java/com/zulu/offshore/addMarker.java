package com.zulu.offshore;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class addMarker extends AppCompatActivity {
    private DatabaseHelper mDbHelper;
    private Map<String, Drawable>  Symbols;
    private Bitmap toUpload1;
    private Bitmap toUpload2;
    private Bitmap toUpload3;
    //private EditText currentlyEditing;
    private boolean currentlyBold = false;
    private boolean currentlyItalics = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //Log.d("parent?",getCallingActivity().toString());
        //Log.d("parent?",getBaseContext().toString());
        //Log.d("parent?",getApplicationContext().toString());
        //Log.d("parent?",MainActivity.pdb.toString());
        //if(getParent()==null)return;
        mDbHelper= MainActivity.pdb;
        loadIconData();
        Map<String,String> types= mDbHelper.getTypesNameMap();
        Spinner mSpinner = findViewById(R.id.addTypeSpinner);
        Map<String,String> typesr=new HashMap<>();
        for(int i=0;i<types.size();i++){
            typesr.put(types.values().toArray()[i].toString(),types.keySet().toArray()[i].toString());
        }
        SortedSet<String> names = new TreeSet<>(types.values());
        Drawable[] s = new Drawable[Symbols.size()];
        if(names.size()==Symbols.size())
            try {
                for (int i = 0; i < types.size(); i++) {
                    s[i] = Symbols.get(typesr.get(names.toArray()[i].toString()));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        CustomAdapter mCustomAdapter = new CustomAdapter(this,names.toArray(new String[names.size()]), s);
        mSpinner.setAdapter(mCustomAdapter);
        mSpinner.setSelection(1);
        LoadPreferences();
        final EditText ed = findViewById(R.id.addDiscription);
        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                StyleSpan currentStyle = new StyleSpan(currentlyBold?currentlyItalics?Typeface.BOLD_ITALIC:Typeface.BOLD:currentlyItalics?Typeface.ITALIC:Typeface.NORMAL);
                ed.getText().setSpan(currentStyle,start,start+count,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                //Log.d("disc","changed:"+s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            getIntent().putExtra("fineTune",false);
            SavePreferences();
            finish(); // close this activity and return to preview activity (if there is any)

        }

        return super.onOptionsItemSelected(item);
    }

    public void loadIconData(){
        //if(true)return;
        Map<String, byte[]> typesIcons = mDbHelper.getTypesIconMap();
        Symbols = new HashMap<>();
        for(Map.Entry<String, byte[]> entry : typesIcons.entrySet()) {
            try{
                Drawable dd = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray( entry.getValue(),
                        0,entry.getValue().length), 60, 60, false));
                Symbols.put(entry.getKey(), dd);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private void SavePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("addType",((Spinner)findViewById(R.id.addTypeSpinner)).getSelectedItemPosition());
        editor.putString("addTitle",((EditText)findViewById(R.id.addTitle)).getText().toString());
        editor.putString("addDesc",((EditText)findViewById(R.id.addDiscription)).getText().toString());
        editor.putString("addComment",((EditText)findViewById(R.id.addComment)).getText().toString());

        editor.apply();
    }

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        ((Spinner)findViewById(R.id.addTypeSpinner)).setSelection(sharedPreferences.getInt("addType",25));
        ((EditText)findViewById(R.id.addTitle)).setText(sharedPreferences.getString("addTitle",""));
        ((EditText)findViewById(R.id.addDiscription)).setText(sharedPreferences.getString("addDesc",""));
        ((EditText)findViewById(R.id.addComment)).setText(sharedPreferences.getString("addComment",""));
        ((TextView)findViewById(R.id.addLocationLat)).setText(String.valueOf(getIntent().getDoubleExtra("addLat",0)));
        ((TextView)findViewById(R.id.addLocationLon)).setText(String.valueOf(getIntent().getDoubleExtra("addLon",0)));

    }

    @Override
    public void onBackPressed() {
        SavePreferences();
        //Log.d("fine tuning","here");
        super.onBackPressed();
    }

    public void finetuneLoc(View view) {
        Intent intent = new Intent();
        intent.putExtra("lat",((TextView)findViewById(R.id.addLocationLat)).getText().toString());
        intent.putExtra("lon",((TextView)findViewById(R.id.addLocationLon)).getText().toString());
        setResult(RESULT_OK, intent);
        SavePreferences();
        finish();
    }

    public void adtheMarker(View view) {
        //Log.d("Sel",((Spinner)findViewById(R.id.addTypeSpinner)).getSelectedItemPosition()+"");
        //Log.d("Sel",((TextView)findViewById(R.id.tvMarkerType)).getText()+"");
        //Log.d("Sel",((TextView)((Spinner)findViewById(R.id.addTypeSpinner)).getSelectedView().findViewById(R.id.tvMarkerType)).getText()+"");

        Boolean s;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            s = mDbHelper.addMarker(((EditText)findViewById(R.id.addLocationLat)).getText().toString(),
                    ((EditText)findViewById(R.id.addLocationLon)).getText().toString(),
                    ((EditText)findViewById(R.id.addTitle)).getText().toString(),
                    Html.toHtml(((EditText)findViewById(R.id.addDiscription)).getText(),0),
                    ((TextView)findViewById(R.id.tvMarkerType)).getText().toString(),
                    Html.toHtml(((EditText)findViewById(R.id.addComment)).getText(),0),
                    toUpload1,toUpload2,toUpload3, FirebaseAuth.getInstance().getCurrentUser()==null?"":FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
            );
        }else{
            s = mDbHelper.addMarker(((EditText)findViewById(R.id.addLocationLat)).getText().toString(),
                    ((EditText)findViewById(R.id.addLocationLon)).getText().toString(),
                    ((EditText)findViewById(R.id.addTitle)).getText().toString(),
                    ((EditText)findViewById(R.id.addDiscription)).getText().toString(),
                    ((TextView)findViewById(R.id.tvMarkerType)).getText().toString(),
                    ((TextView)findViewById(R.id.addComment)).getText().toString(),
                    toUpload1,toUpload2,toUpload3,FirebaseAuth.getInstance().getCurrentUser()==null?"":FirebaseAuth.getInstance().getCurrentUser().getDisplayName()
            );
        }
        if(s){
            ArrayList<String> markers = mDbHelper.getMyMarkers(getIntent().getStringExtra("userID"));
            Toast.makeText(this,getResources().getString(R.string.marker_added)+markers.size(),Toast.LENGTH_LONG).show();
            ((EditText)findViewById(R.id.addLocationLon)).setText("");
            ((EditText)findViewById(R.id.addTitle)).setText("");
            ((EditText)findViewById(R.id.addDiscription)).setText("");
            ((EditText)findViewById(R.id.addComment)).setText("");
            ((Spinner)findViewById(R.id.addTypeSpinner)).setSelection(0);
            onBackPressed();
        }else{
            Toast.makeText(this,"Error adding marker",Toast.LENGTH_LONG).show();
        }

    }

    public void addPicture(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri photoUri = data.getData();
            if (photoUri != null) {
                try {
                    Bitmap currentImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                    if(currentImage.getHeight()>900 || currentImage.getWidth()>900){ //rescale large imaages
                        double ratio = (currentImage.getHeight()+0.0)/currentImage.getWidth();
                        int newWidth = (int)(currentImage.getWidth()>currentImage.getHeight()?900:(900/ratio));
                        int newHeight = (int)(currentImage.getWidth()>currentImage.getHeight()?(900*ratio):900);
                        //Log.d("image before",currentImage.getWidth()+"x"+currentImage.getHeight());
                        //Log.d("resizing to",newWidth+","+newHeight+","+ratio);
                        currentImage = Bitmap.createScaledBitmap(currentImage, newWidth, newHeight, false);

                    }
                    double Thumbratio = (currentImage.getHeight()+0.0)/currentImage.getWidth();
                    int smallestDim = Math.min(getApplication().getResources().getDisplayMetrics().widthPixels/4,getApplication().getResources().getDisplayMetrics().heightPixels/4);
                    int ThumbWidth = (int)(currentImage.getWidth()>currentImage.getHeight()?smallestDim:(smallestDim/Thumbratio));
                    int ThumbHeight = (int)(currentImage.getWidth()>currentImage.getHeight()?(smallestDim*Thumbratio):smallestDim);
                    //Log.d("resizing to",Thumbratio+","+ThumbWidth+","+ThumbHeight);
                    ((ImageView)findViewById(toUpload1==null?R.id.addPicture1:toUpload2==null?R.id.addPicture2:R.id.addPicture3)).setImageBitmap(Bitmap.createScaledBitmap(currentImage, ThumbWidth,ThumbHeight, false));
                    if(toUpload1==null){
                        toUpload1=currentImage;
                    }else{
                        if (toUpload2==null){
                            toUpload2=currentImage;
                        }else{
                            toUpload3=currentImage;
                        }
                    }
                    //Log.d("image",currentImage.getWidth()+"x"+currentImage.getHeight());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void editImage(final View view) {
        PopupMenu p = new PopupMenu(this,view);
        MenuItem i = p.getMenu().add("Delete");
        i.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(view.getId()==R.id.addPicture1){
                    toUpload1=null;
                    ((ImageView)view).setImageBitmap(null);
                }
                if(view.getId()==R.id.addPicture2){
                    toUpload2=null;
                    ((ImageView)view).setImageBitmap(null);
                }
                if(view.getId()==R.id.addPicture3){
                    toUpload3=null;
                    ((ImageView)view).setImageBitmap(null);
                }
                return true;
            }
        });
        MenuItem i2 = p.getMenu().add("Rotate");
        i2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Bitmap b = view.getId()==R.id.addPicture1?toUpload1:view.getId()==R.id.addPicture2?toUpload2:toUpload3;
                //Log.d("current form",b.getWidth()+","+b.getHeight());
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rotatedBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true);
                //Log.d("trnasformed form",rotatedBitmap.getWidth()+","+rotatedBitmap.getHeight());
                if(view.getId()==R.id.addPicture1) toUpload1=rotatedBitmap;
                if(view.getId()==R.id.addPicture2) toUpload2=rotatedBitmap;
                if(view.getId()==R.id.addPicture3) toUpload3=rotatedBitmap;

                double Thumbratio = (rotatedBitmap.getHeight()+0.0)/rotatedBitmap.getWidth();
                int smallestDim = Math.min(getApplication().getResources().getDisplayMetrics().widthPixels/4,getApplication().getResources().getDisplayMetrics().heightPixels/4);
                int ThumbWidth = (int)(rotatedBitmap.getWidth()>rotatedBitmap.getHeight()?smallestDim:(smallestDim/Thumbratio));
                int ThumbHeight = (int)(rotatedBitmap.getWidth()>rotatedBitmap.getHeight()?(smallestDim*Thumbratio):smallestDim);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(rotatedBitmap, ThumbWidth, ThumbHeight, true);
                //Log.d("thumb form",scaledBitmap.getWidth()+","+scaledBitmap.getHeight());
                ((ImageView)view).setImageBitmap(scaledBitmap);
                return true;
            }
        });
        p.show();

    }

    public void cancelMarker(View view) {
        onBackPressed();
    }

    public void boldme(View view) {
        currentlyBold = !currentlyBold;
        if(currentlyBold){
            view.setBackgroundColor(Color.BLACK);
            ((Button)view).setTextColor(Color.WHITE);
        }else{
            view.setBackgroundColor(Color.WHITE);
            ((Button)view).setTextColor(Color.BLACK);
        }
        //if (currentlyEditing==null)currentlyEditing=findViewById(R.id.addDiscription);
        if(((EditText)findViewById(R.id.addDiscription)).hasSelection()){
            //Log.d("bold",currentlyBold+"");
            StyleSpan currentStyle = new StyleSpan(currentlyBold?currentlyItalics?Typeface.BOLD_ITALIC:Typeface.BOLD:currentlyItalics?Typeface.ITALIC:Typeface.NORMAL);
            ((EditText)findViewById(R.id.addDiscription)).getText().setSpan(currentStyle,((EditText)findViewById(R.id.addDiscription)).getSelectionStart(),((EditText)findViewById(R.id.addDiscription)).getSelectionEnd(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void Italicsme(View view) {
        currentlyItalics = !currentlyItalics;
        if(currentlyItalics){
            view.setBackgroundColor(Color.BLACK);
            ((Button)view).setTextColor(Color.WHITE);
        }else{
            view.setBackgroundColor(Color.WHITE);
            ((Button)view).setTextColor(Color.BLACK);
        }
        //if (currentlyEditing==null)currentlyEditing=findViewById(R.id.addDiscription);
        if(((EditText)findViewById(R.id.addDiscription)).hasSelection()){
            //d("italics",currentlyItalics+"");
            StyleSpan currentStyle = new StyleSpan(currentlyBold?currentlyItalics?Typeface.BOLD_ITALIC:Typeface.BOLD:currentlyItalics?Typeface.ITALIC:Typeface.NORMAL);
            ((EditText)findViewById(R.id.addDiscription)).getText().setSpan(currentStyle,((EditText)findViewById(R.id.addDiscription)).getSelectionStart(),((EditText)findViewById(R.id.addDiscription)).getSelectionEnd(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public void linkMe(View view) {
        final EditText currentlyEditing = findViewById(R.id.addDiscription);
        //if(currentlyEditing.hasSelection())
        //Log.d("k",currentlyEditing.getSelectionStart()+","+currentlyEditing.getSelectionEnd());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Link Details");
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        final EditText link = new EditText(this);
        link.setHint("Link URL");
        link.setInputType(InputType.TYPE_CLASS_TEXT);
        final EditText text = new EditText(this);
        text.setInputType(InputType.TYPE_CLASS_TEXT);
        text.setHint("Link Text");
        if(currentlyEditing.hasSelection())
            text.setText(currentlyEditing.getText().subSequence(currentlyEditing.getSelectionStart(),currentlyEditing.getSelectionEnd()));
        ll.addView(link);
        ll.addView(text);
        builder.setView(ll);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(currentlyEditing.hasSelection()){
                    currentlyEditing.getText().delete(currentlyEditing.getSelectionStart(),currentlyEditing.getSelectionEnd());
                    currentlyEditing.getText().insert(currentlyEditing.getSelectionStart(),Html.fromHtml("<a href=\""+link.getText().toString()+"\">"+text.getText().toString()+"</a>"));
                }else{
                    currentlyEditing.getText().insert(currentlyEditing.getSelectionStart(),Html.fromHtml("<a href=\""+link.getText().toString()+"\">"+text.getText().toString()+"</a>"));
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
}
