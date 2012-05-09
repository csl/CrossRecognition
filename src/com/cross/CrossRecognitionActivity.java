package com.cross;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CrossRecognitionActivity extends Activity implements OnTouchListener 
{
	String TAG = "CrossRecognitionActivity";
	   
	private static final int MENU_CLEAR = Menu.FIRST;

	ImageView imageView;
	Bitmap bitmap;
	Canvas canvas;
	Paint paint;
	float downx = 0;
	float downy = 0;
	float upx = 0;
	float upy = 0;

	int line = 0;
	ArrayList<LineCoordinate> LineRec;
	LineCoordinate nowLC = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        imageView = (ImageView) this.findViewById(R.id.imagev);
        
        LineRec = new ArrayList<LineCoordinate>();

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        float dw = currentDisplay.getWidth();
        float dh = currentDisplay.getHeight();
        
        bitmap = Bitmap.createBitmap((int)dw,(int)dh, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(10);
        
        imageView.setImageBitmap(bitmap);
        imageView.setOnTouchListener(this);
    }
    
    public boolean onCreateOptionsMenu(Menu menu)
    {
      super.onCreateOptionsMenu(menu);
      
      menu.add(0 , MENU_CLEAR, 1 ,"清除畫面")
      .setAlphabeticShortcut('C');
      return true;  
    } 
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
      switch (item.getItemId())
       { 
            case MENU_CLEAR:
				line = 0;
				canvas.drawColor(Color.BLACK);
				imageView.setImageBitmap(bitmap);
				LineRec.clear();            	
                break;
        }
      	return true ;

      }
    
    public boolean CalCrossRecognition()
    {
    	LineCoordinate one = LineRec.get(0);
    	LineCoordinate two = LineRec.get(1);
    	
    	
    	
    	return false;
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent event) 
	{
		int action = event.getAction();
		switch (action) 
		{
			case MotionEvent.ACTION_DOWN:
				if (line == 2)
				{
					line = 0;
					downx = -1;
					downy = -1;
					canvas.drawColor(Color.BLACK);
					imageView.setImageBitmap(bitmap);
					
					if (CalCrossRecognition() == true)
					{
						openDialog("正確");
					}
					else
					{
						openDialog("錯誤");
					}
					
					LineRec.clear();
				}
				else
				{
					downx = event.getX();
					downy = event.getY();
					nowLC = new LineCoordinate();
					
					nowLC.startX = downx;
					nowLC.startY = downy;
				}
				
				
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_UP:
				if (downx != -1 && downy != -1)
				{
					upx = event.getX();
					upy = event.getY();
					
					nowLC.endX = upx;
					nowLC.endY = upy;
					LineRec.add(nowLC);
					
					canvas.drawLine(downx, downy, upx, upy, paint);
					Log.i(TAG, downx + "," + downy + "," + upx + "," +upy);
					imageView.invalidate();
					line++;
				}
				
				if (line == 2)
				{
		            Toast.makeText(this, "請在畫面任一點選，以便辨識十字圖案...", Toast.LENGTH_SHORT).show();
				}

				break;
			case MotionEvent.ACTION_CANCEL:
				break;
			default:
				break;
		}
		return true;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{  
			openOptionsDialog();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);  
	}

	//show message, ask exit yes or no
	private void openOptionsDialog() 
	{
	  new AlertDialog.Builder(this)
	    .setTitle("Exit?")
	    .setMessage("Exit?")
	    .setNegativeButton("No",
	        new DialogInterface.OnClickListener() {
	        
	          public void onClick(DialogInterface dialoginterface, int i) 
	          {
	          }
	    }
	    )
	 
	    .setPositiveButton("Yes",
	        new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialoginterface, int c) 
	        {
	          android.os.Process.killProcess(android.os.Process.myPid());           
	          finish();
	        }
	    }
	    )
	    
	    .show();
	}    
	  //show message
	  public void openDialog(String info)
	  {
	    new AlertDialog.Builder(this)
	    .setTitle("辨識十字圖案結果")
	    .setMessage(info)
	    .setPositiveButton("OK",
	        new DialogInterface.OnClickListener()
	        {
	         public void onClick(DialogInterface dialoginterface, int i)
	         {
	        	 
	         }
	         }
	        )
	    .show();
	  }
	
}