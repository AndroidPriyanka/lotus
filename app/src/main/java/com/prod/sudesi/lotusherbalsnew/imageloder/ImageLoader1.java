package com.prod.sudesi.lotusherbalsnew.imageloder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.prod.sudesi.lotusherbalsnew.R;


public class ImageLoader1 {
    
    MemoryCache1 memoryCache=new MemoryCache1();
    FileCache1 fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    
    public ImageLoader1(Context context){
        fileCache=new FileCache1(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    final int stub_id= R.drawable.stub;
    public void DisplayImage(//String url, ImageView imageView,
    		String name,TextView tv , String id, CheckBox cb,TextView shade,String str_shade)
    {
      //  imageViews.put(imageView, url);
        cb.setText(id);
        tv.setText(name);
        shade.setText(str_shade);
      /*  Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView);
            imageView.setImageResource(stub_id);
        }*/
    }
    
    public void DisplayMenu(String id, TextView Id, String name, TextView Name)
    		
    {
		Log.e("TAG1", "Flag======3:" + id.toString());
    	Id.setText(id);
    	Name.setText(name);
       
    }
    
    public void DisplayProductData(String id, TextView Id, String prodname, TextView prodName,
    		String prodid, TextView prodId, String openingbal, TextView openingBal, String purchaseqty, TextView purchaseQty,
    		 String usedqty, TextView usedQty, String closingbal, TextView closingBal)
    {
		Log.e("TAG1", "Flag======3:" + id.toString());
    	Id.setText(id);
    	prodName.setText(prodname);
    	prodId.setText(prodid);
    	openingBal.setText(openingbal);
    	purchaseQty.setText(purchaseqty);
    	usedQty.setText(usedqty);
    	closingBal.setText(closingbal);
       
    }
        
    public void DisplayMenu1(String id, TextView Id, String name, TextView Name, String sp_id, TextView sp_ID,String mgroup_id, TextView mgroup_ID,TextView shade,String str_shade)
    {
		Log.e("TAG1", "Flag======3:" + id.toString());
    	Id.setText(id);
    	Name.setText(name);
    	sp_ID.setText(sp_id);
    	mgroup_ID.setText(mgroup_id);
    	shade.setText(str_shade);
       
    }
    //pravin 
    public void Displaynotifications(String id, TextView Id,
    								String not, TextView notification,
    								String cdate, TextView createddate)
    {
		
    	Id.setText(id);
    	notification.setText(not);
    	createddate.setText(cdate);
    	
       
    }
    //pravin
    
    public void Displaynotificationsforadmin(String id, TextView Id, 
    										String crs_id, TextView crs_Id,
    										String date, TextView Date,
    										String crsname, TextView CrsName,
    										String not, TextView notification)
    {
		
    	Id.setText(id);
    	crs_Id.setText(crs_id);
    	Date.setText(date);
    	CrsName.setText(crsname);
    	notification.setText(not);
    	
       
    }
    
    //pravin
        
    private void queuePhoto(String url, ImageView imageView)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);
        
        //from SD cache
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils1.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=70;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public PhotoToLoad(String u, ImageView i){
            url=u; 
            imageView=i;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

	public void DisplayCourse(String string, TextView txt_not_id,
			String string2, TextView txt_not_details) {
		// TODO Auto-generated method stub
		
		txt_not_id.setText(string);
		txt_not_details.setText(string2);
		
		
	}

	public void DisplayCourseforRegistration(
			String course, CheckBox checkbox, String id, TextView text) {
		// TODO Auto-generated method stub
		 Log.d("image loader", "check box value="+course + "id="+id);
		checkbox.setText(course);
		text.setText(id);
	      
	}

	public void DisplayTest(String id, TextView txt_not_id, String desc,
			TextView txt_not_details) {
		// TODO Auto-generated method stub
		
		txt_not_id.setText(id);
		txt_not_details.setText(desc);
		
		
	}

	public void DisplayMainResult(String string, TextView txt_attempt,
			String string2, TextView txt_question, String string5,TextView txt_solve, String string3,
			TextView txt_correct, String string4, TextView txt_wrong) {
		// TODO Auto-generated method stub
		
		txt_attempt.setText(string);
		txt_solve.setText(string5);
		txt_question.setText(string2);
		txt_correct.setText(string3);
		txt_wrong.setText(string4);
		
	}
	
	public void Displayproductlist(
			String course, CheckBox checkbox) {
		// TODO Auto-generated method stub
		 Log.d("image loader", "check box value="+course);
		checkbox.setText(course);
	
	      
	}

	public void Displayproductlist(
			//String string, RadioButton ch_products,
			String string2, TextView txt_products, String string3,
			TextView txt_size, String string4, TextView txt_id) {
		// TODO Auto-generated method stub
		
		//ch_products.setText(string);
		txt_products.setText(string2);
		txt_size.setText(string3);
		txt_id.setText(string4);
		
	}

	public void DisplayProductList(TextView tv_gms_list, String size, TextView tv_product_list,String product,
			CheckBox checkbox, String db_id, TextView tv_price_list,
			String mrp, TextView tv_shadeno_stock, String shadeno) {
		// TODO Auto-generated method stub
		
		
		
	}

}
