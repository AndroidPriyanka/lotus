package com.prod.sudesi.lotusherbalsnew.image;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.prod.sudesi.lotusherbalsnew.R;
import com.prod.sudesi.lotusherbalsnew.VisibilityImageFragment;


public class ListAdapter extends ArrayAdapter<ImageModel> {

  private final List<ImageModel> list;
  private final Activity context;
  
  public ImageModel element;
  
  private ExecutorService service;
  
  public VisibilityImageFragment fff ;
  
  
  
  public ListAdapter(Activity context2, List<ImageModel> list) {
    super(context2, R.layout.rowlayout, list);
    this.context = context2;
    this.list = list;
    
    
    
    if (this.service != null) {
        this.service.shutdownNow();
    }

    this.service = Executors.newSingleThreadExecutor();
    this.notifyDataSetChanged();
  }

  static class ViewHolder {
    protected ImageView imgView;
    protected Button checkbox;
   
    
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View view = null;
    try
    {
    if (convertView == null) {
      LayoutInflater inflator = context.getLayoutInflater();
      view = inflator.inflate(R.layout.rowlayout, null);
      final ViewHolder viewHolder = new ViewHolder();
      
      
      
      viewHolder.imgView = (ImageView) view.findViewById(R.id.imgView);
      viewHolder.checkbox = (Button) view.findViewById(R.id.chkBox);
    
      viewHolder.checkbox
      .setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			element = (ImageModel) viewHolder.checkbox
	                  .getTag();
			
			
			new AlertDialog.Builder(getContext())
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle("Delete")
	        .setMessage("Are you sure to Delete")
	        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {

	                //Stop the activity
	            	SharedPreferences sharedpre=null;
	           	 
	            	SharedPreferences.Editor saveuser=null;
	            	 
	            	sharedpre = context.getSharedPreferences("Sudesi",0);
	    			
	            	//viewHolder.
	            	
	    			int count1 = element.getImg();
	    			
	    			
	    			
	    			 Log.d("sharepre","in count1"+count1);
	  			  if(count1!=0)
	  			  {
	  				  
	  				  Log.d("sharepre","in count");
	  				saveuser = sharedpre.edit();					            
	  	    		saveuser.putString("ImgPath_"+String.valueOf(count1),"");
	  	    		//list.remove(count1);
	  	    		 
	  	         	saveuser.commit();
	  	         	
	  	      
  	        //	((Activity)context).finish();
	  	      	
  		//	getContext().startActivity(new Intent(getContext(),DashboardNewActivity.class));
	  	         	 	
	  	   
  		//	fff.loadlist(getContext());
	  	
            
	  			  }
	            }

	        })
	        .setNegativeButton("No", null)
	        .show();
			
		}
	});
      
     
    
      view.setTag(viewHolder);
      viewHolder.checkbox.setTag(list.get(position));
    } else {
      view = convertView;
      ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
    }
    ViewHolder holder = (ViewHolder) view.getTag();
    
    holder.imgView.setTag(list.get(position).getPath());
    holder.imgView.setTag(list.get(position).getPath());
    
   
    try {
        this.service.submit(new Job(list.get(position).getPath(),holder.imgView, list.get(position).getPath()));
    } catch (Throwable e) {
    	e.printStackTrace();
    }
    
    }
    catch(Exception e)
    {
    	view=null;    	
    }
    return view;
  }
  
  private final class Job
  implements Runnable
{

private final ImageView logo;
private final String logoTag;
private final String URL;

private Job(String url, ImageView logo, String logoTag)
{
  this.URL = url;
  this.logo = logo;
  this.logoTag = logoTag;
 }

@Override
public void run()
{
	try
	{
	
  Thread t = Thread.currentThread();
  
  t.setPriority(Thread.NORM_PRIORITY - 1); //not to load ui thread much
  
  if (logo.getTag().equals(logoTag)) {
      context.runOnUiThread(new Runnable()
      {
          @Override
          public void run()
          {  
        	    File f =new File(URL);
        	    
        	    Bitmap p = null;
          	    
        	    if(URL.equalsIgnoreCase("No Images"))
        	    {
        	    	logo.setContentDescription(URL);
        	    }
        	    else
        	    {
    	        if(f.exists())
    	        {
    	        	p=BitmapFactory.decodeFile(URL); 
    	        	 
    	        	   // Get current dimensions AND the desired bounding box
    	        	    int width = p.getWidth();
    	        	    int height = p.getHeight();
    	        	    
    	        	    //
    	        	    float density = context.getResources().getDisplayMetrics().density;
    	        	    int bounding =   Math.round((float)150 * density);
    	        
    	        	    // Determine how much to scale: the dimension requiring less scaling is
    	        	    // closer to the its side. This way the image always stays inside your
    	        	    // bounding box AND either x/y axis touches it.  
    	        	    float xScale = ((float) bounding) / width;
    	        	    float yScale = ((float) bounding) / height;
    	        	    float scale = (xScale <= yScale) ? xScale : yScale;
    	        
    	        	    // Create a matrix for the scaling and add the scaling data
    	        	    Matrix matrix = new Matrix();
    	        	    matrix.postScale(scale, scale);

    	        	    // Create a new bitmap and convert it to a format understood by the ImageView 
    	        	    Bitmap scaledBitmap = Bitmap.createBitmap(p, 0, 0, width, height, matrix, true);
    	        	    width = scaledBitmap.getWidth(); // re-use
    	        	    height = scaledBitmap.getHeight(); // re-use
    	            
    	        	    logo.setImageBitmap(scaledBitmap);

    	            }
        	    }
          }
      	});
}
}
catch(Exception e)
{
	e.printStackTrace();
}
}
}
} 