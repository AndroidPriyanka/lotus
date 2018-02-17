package com.prod.sudesi.lotusherbalsnew.libs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;

public class ImageUtils {	
	
    public static String getCompressedImagePath(String orgImagePath,String username, String producttype,String image_name) {
    	
    	Log.e("","orgimagepath=="+orgImagePath);
    	
    	  if (orgImagePath == null) {
              return null;          }
   	 
    	  byte[] bmpPicByteArray=null;
    	  
    	  String fname = "";
    	      	  
       
          ByteArrayOutputStream bao = new ByteArrayOutputStream();  
          Log.e("","imageutil1");
          Bitmap b1 = BitmapFactory.decodeFile(orgImagePath);
          int orgWidth = b1.getWidth();
			int orgHeight = b1.getHeight();
			Log.e("","imageutil2 orwidht"+orgWidth +" orheight="+orgHeight );
			
			int imgWidth = 1280, imgHeight =786;

          SimpleFTP ftp=null;
          try {
  
          	int MAX_IMAGE_SIZE = 200 * 1024; // max final file size
                    
          	int compressQuality = 20; // quality decreasing by 5 every loop. (start from 99)
          	
       	 float ratioX = (float)imgWidth / (float)orgWidth;
         float ratioY = (float)imgHeight / (float)orgHeight;
         float ratio = Math.min(ratioX, ratioY);

         // New width and height based on aspect ratio
         int newWidth = (int)(orgWidth * ratio);
         int newHeight = (int)(orgHeight * ratio);

        
          	try {
          		Log.e("","imageutil3");
//          		Bitmap bitmap2 = scaleBitmap(b1, newWidth, newHeight);
          		
          		if (b1 != null) {
          			Log.e("","imageutil4");
          			b1.compress(Bitmap.CompressFormat.JPEG,
							compressQuality, bao);
					bmpPicByteArray = bao.toByteArray();
					
          		}


          		File root = new File(Environment.getExternalStorageDirectory() + File.separator + "sudesi" + File.separator);
        		root.mkdirs();
        		
        		        				
        		//fname = username+"_"+producttype+"_"+System.currentTimeMillis()+".jpeg";
        		fname = image_name;
        		
        		File sdImageMainDirectory= new File(root, fname);
        		
        		 if ( !sdImageMainDirectory.exists() ) 
        	        {
        			 sdImageMainDirectory.createNewFile();
        	        }
        		/* Bitmap bi = BitmapFactory.decodeByteArray(
							bmpPicByteArray, 0, bmpPicByteArray.length);
*/
        		 
        		  FileOutputStream fos = new FileOutputStream(sdImageMainDirectory); 
				  fos.write(bmpPicByteArray); 

			/*	  bi.compress(Bitmap.CompressFormat.JPEG,
							compressQuality, fos);
				bi.recycle();*/
					fos.flush();
					fos.close();

        		 
        		 //fname = sdImageMainDirectory.toString();
        		 
        	/*	 fname = sdImageMainDirectory.toString()+"~"+fname;
        		 	
        		*/
                	 ftp = new SimpleFTP();

					    // Connect to an FTP server on port 21.
                	 
                	
                	 //-----------------------------------
                	 
             // ftp.connect("sandbox.smartforcecrm.com", 21, "sandboxadupload", "Sandbox@123");//uat 01.02.2015
                	 
//                	 ftp.connect("sandbox.smartforcecrm.com", 21, "lotusadupload", "Lotus@123");//prod 01.02.2015 //04.05.2015//09.07.2015
              
              
              ftp.connect("lotussmartforce.com", 21, "lotusadupload", "Lotus@123");//prod //10.06.2015
                	 //------------------------------
                	 
                	 
                	 //ftp.connect("118.139.173.227", 21, "lotusad", "Lotus@123"); // 
                	 // ftp.connect("118.139.173.227", 21, "androiddevp", "Android123@");
                	// ftp.connect("118.139.173.227", 21, "prayagimages", "Pass123@");
					    //182.50.158.147
					    // Set binary mode.
					    ftp.bin();

					    // Change to a new working directory on the FTP server.
					    //ftp.cwd("easset/TTSL");

					    // Upload some files.
					    ftp.stor(sdImageMainDirectory);
				    
					    // Quit from the FTP server.
					    ftp.disconnect();
			
          	} catch (Exception e) {
          	    
          	  e.printStackTrace();
          	  
          	 ftp.disconnect();
          	 
          	ftp=null;
          	 
          	  fname=null;
          	}
          }
          catch (Exception e) {
                  e.printStackTrace();
                  fname=null;
              }
          return  fname;
    }
    
    public static Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
        canvas.drawBitmap(bitmap, m, new Paint());

       // Bitmap result=waterMark(output, 90, 30, true);
        return output;
    }
    
    public static Bitmap decodeSampledBitmapFromResource(String orgImagePath,
            int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(orgImagePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(orgImagePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
   	
        return inSampleSize;
    }

  }
