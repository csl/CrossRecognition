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
	
	static int LDegree = 16;

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
    
    public PointCoordinate getIntersectPoint(double a1, double b1, double c1, double a2, double b2, double c2)
    {  
    	PointCoordinate p = null;  
        double m = a1 * b2 - a2 * b1;  
        if (m == 0) {  
            return null;  
        }  
        double x = (c2 * b1 - c1 * b2) / m;  
        double y = (c1 * a2 - c2 * a1) / m;  
        p = new PointCoordinate();
        
        p.PX = x;
        p.PY = y;      
        
        return p;  
    }
    
    public PointCoordinate getCrossPoint(double Onex2, double Oney2, double Onex1, double Oney1, 
    		double Twox2, double Twoy2, double Twox1, double Twoy1)
    {  
        double Onea, Oneb, Onec;  
        Onea = Oney2 - Oney1;  
        Oneb = Onex1 - Onex2;  
        Onec = (Onex2 - Onex1) * Oney1 - (Oney2 - Oney1) * Onex1;  
        if (Oneb < 0) {  
        	Onea *= -1; Oneb *= -1; Onec *= -1;  
        }else if (Oneb == 0 && Onea < 0) {  
        	Onea *= -1; Onec *= -1;  
        }
        
        double Twoa, Twob, Twoc;  
        Twoa = Twoy2 - Twoy1;  
        Twob = Twox1 - Twox2;  
        Twoc = (Twox2 - Twox1) * Twoy1 - (Twoy2 - Twoy1) * Twox1;  
        if (Twob < 0) {  
        	Twoa *= -1; Twob *= -1; Twoc *= -1;  
        }else if (Twob == 0 && Twoa < 0) {  
        	Twoa *= -1; Twoc *= -1;  
        }  
        
        return getIntersectPoint(Onea, Oneb, Onec, Twoa, Twob, Twoc);
        
    }    
    
    public double max(double X1, double X2)
    {
       if(X1>X2) return X1;
       else return X2;
    }

    public double min(double X1, double X2)
    {
       if(X1<X2) return X1;
       else return X2;
    }
    
    public double getLineLength(double x1, double y1, double x2, double y2)
    {
       double k = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
       return Math.sqrt(k);
    }    
    
    public boolean CalCrossRecognition()
    {
    	LineCoordinate one = LineRec.get(0);
    	LineCoordinate two = LineRec.get(1);
    	
    	double one_startX = one.startX;
    	double one_startY = one.startY;
    	double one_endX = one.endX;
    	double one_endY = one.endY;
    	
    	double two_startX = two.startX;
    	double two_startY = two.startY;
    	double two_endX = two.endX;
    	double two_endY = two.endY;
    	
    	if (one_endX - one_startX == 0 || two_endX - two_startX == 0) 
    		return false;
    	
    	double oneXY =  (one_endY - one_startY) / (one_endX - one_endY);
    	double twoXY =  (two_endY - two_startY) / (two_endX - two_startX);
    	double Theta = ( Math.atan(oneXY) - Math.atan( twoXY) ) * 180 / Math.PI;

    	Log.i(TAG, one_endY + "," + one_startY + "," + one_endX + "," + one_startX + ", " + oneXY);
    	Log.i(TAG, two_endY + "," + two_endY + "," + two_endX + "," + two_startX + ", " + twoXY);
    	Log.i(TAG, "oneXY " + Theta);
    	
    	if (max(Math.abs(oneXY),Math.abs(twoXY)) < 30 && max(Math.abs(oneXY),Math.abs(twoXY)) > 1) 
    		return false;   	
    	
    	
    	if (Math.abs(Theta) < 90 + LDegree && Math.abs(Theta) > 90 - LDegree)
    	{
    		   double S1,D1,S2,D2,X;
    		   S1 = oneXY;
    		   D1 = (one_endX * one_startY - one_startX * one_endY)/(one_endX - one_startX);
    		   S2 = twoXY;
    		   D2 = (two_endX * two_startY - two_startX * two_endY)/(two_endX - two_startX);
    		
    		   if(S1==S2)
    		   {
 	    			Log.i(TAG, "no cross " + S1 + " ," + S2);
    			   return false;
    		   }
    		   else 
    				X=(D2-D1)/(S1-S2);
    		
    		   if(X>=min(one_startX, one_endX) && X<=max(one_startX, one_endX) && X>=min(two_startX, two_endX) && X<=max(two_startX, two_endX))
    		   {
    	    		PointCoordinate pc = getCrossPoint(one_endX, one_endY, one_startX, one_startX, two_endX, two_endY, two_startX, two_startX);
    	    		if (pc == null) return false;
    	    		
    	    		//check 1:1.3
    	    		double c1 = getLineLength(one_startX, one_startY, pc.PX , pc.PY);
    	    		double c2 = getLineLength(pc.PX , pc.PY, one_endX, one_endY);
    	    		double c3 = getLineLength(two_startX, two_startY, pc.PX , pc.PY);
    	    		double c4 = getLineLength(pc.PX , pc.PY, two_endX, two_endY);
    	    		
 	    			Log.i(TAG, "cross " + pc.PX + " ," + pc.PY);
 	    			Log.i(TAG, c1 + " ," + c2 + " ," + c3 + " ," + c4);
 	    			
 	    			double c12 = max(c1, c2)/min(c1, c2);
 	    			double c34 = max(c3, c4)/min(c3, c4);

 	    			Log.i(TAG, c12 + " ," + c34);
 	    			
 	    			if (c12 > 1.3 ) 
 	    				return false; 	    			
    		   }
    		   else
    		   {
   	    			Log.i(TAG, "no cross " + X);
    			   return false;
    		   }
    	}
    	else
    	{
    		return false;
    	}
    	
    	
    	return true;
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