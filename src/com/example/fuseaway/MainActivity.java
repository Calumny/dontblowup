package com.example.fuseaway;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.example.fuseaway.R;

import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
 
public class MainActivity extends Activity  implements View.OnClickListener, View.OnTouchListener{
	
	AlertDialog.Builder alert;
	private Uri uri2;
	private String username;
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) { 
    	int itemId = item.getItemId();
    	return super.onOptionsItemSelected(item);
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);

        drawView = (DrawingView)findViewById(R.id.drawing_canvas);
        drawView.setOnTouchListener(this);
        
		Point size = new Point();

		getWindowManager().getDefaultDisplay().getSize(size);
		

		Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.no_medal);
    	drawView.setBitmap(Bitmap.createScaledBitmap(background, size.x, size.y, true));

//        setupCanvas(getIntent());
        
        // Get the view from new_activity.xml
//        dispatchTakePictureIntent();
        
    }
    
    private PopupWindow palettePopup;
    
    @Override
    public void onClick (View v) {
    	
    }
    
    private float yDelta = 0;
    
    public boolean onTouch(View view, MotionEvent event) {

    	if (view.getId() == R.id.drawing_canvas) {
    		return drawView.onTouchEvent(event);
    	}
    	
        final int Y = (int) event.getRawY();
        
    switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            yDelta = Y - view.getY();
            break;
        case MotionEvent.ACTION_UP:
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            break;
        case MotionEvent.ACTION_POINTER_UP:
            break;
        case MotionEvent.ACTION_MOVE:
            view.setY(Y - yDelta);
            break;
    }
    
    	return true;
    }
    
    
    public void colorSelect(View view) {
    	palettePopup.dismiss();
    	drawView.setColor(view.getTag().toString());
    }
    
    public void setErase(View view) {
    	palettePopup.dismiss();
    	drawView.setErase();
    }
    
    public void clearCanvas(View view) {
    	palettePopup.dismiss();
    	drawView.clearCanvas();
    }
    
    public void noColor(View view) {
    	palettePopup.dismiss();
    	drawView.noColor();
    }
    
    public void undo(View view) {

    	if (palettePopup != null) {
    		palettePopup.dismiss();
    	}
    	drawView.undo();
    }
    
    private DrawingView drawView;
    
   
}