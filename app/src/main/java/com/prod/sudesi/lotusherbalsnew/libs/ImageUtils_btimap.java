package com.prod.sudesi.lotusherbalsnew.libs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.ksoap2.serialization.SoapPrimitive;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class ImageUtils_btimap {

    @TargetApi(Build.VERSION_CODES.FROYO)
	@SuppressLint("NewApi")
	public static String getCompressedImagePath(String orgImagePath,Context con) {//String scanid

        String fname = "", successImages = "0";

        // int total = orgImagePath.length, abortFlag = 0;
        int abortFlag = 0;
        
        String fnameServer;
        
        LotusWebservice service;
        
        SoapPrimitive soap_result;

        String base64 = "";
        
     //   Log.e("Image Total Passed", String.valueOf(total));

        try {

            byte[] bmpPicByteArray = null;


           // int MAX_IMAGE_SIZE = 300 * 1024; // max final file size

            ByteArrayOutputStream bao = new ByteArrayOutputStream();

            bao.reset();

            int compressQuality = 20; // quality decreasing by 5 every loop. (start from 99)
        //    int streamLength = MAX_IMAGE_SIZE;

            
            
            File root = new File(Environment.getExternalStorageDirectory() + File.separator + "sudesi" + File.separator);
            root.mkdirs();

            if (orgImagePath == null) {
                return null;
            }
          //  for (int index = 0; index < total; index++) {

                if (orgImagePath != null && !orgImagePath.trim().equalsIgnoreCase("")) {
                	
                	bao = new ByteArrayOutputStream();
                	bao.reset();
                	
//                    Log.e("image Path ", orgImagePath[index]);
//                	 final BitmapFactory.Options options = new BitmapFactory.Options();
//    	        	 options.inSampleSize = 8;
                    Bitmap bitmap = BitmapFactory.decodeFile(orgImagePath);
                    compressQuality = 80;
                    
                   
                   // quality decreasing by 5 every loop. (start from 99)
                    
                    
            //        streamLength = MAX_IMAGE_SIZE;
                    abortFlag = 0;              
                   
                   
//                    	 while (streamLength >= MAX_IMAGE_SIZE){
                    		 try {
                        
						if (bitmap != null) {
						  bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bao);
						  //bitmap.
						  bmpPicByteArray = bao.toByteArray();
						  
						  base64 = Base64.encodeToString(bmpPicByteArray, Base64.DEFAULT);
						  
						  writeString(base64);
						  
					
              } else {
						  abortFlag = 1;
              }
                    	 
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    		 
//                }
                    bitmap.recycle();
                    bitmap=null;
                    if (abortFlag == 0) {

                        try {
                          
                            fname = orgImagePath;
                            
                            
                            fnameServer = orgImagePath.substring(orgImagePath.lastIndexOf("/")+1,orgImagePath.length());

                            Log.e("orgImagePath", orgImagePath);
                            
                            Log.e("orgImageName", fnameServer);
                            
                            service = new LotusWebservice(con);
                            
                            soap_result = service.Base64ToImage(base64, fnameServer);
                            
                            Log.v("","soap_result.toString()====="+soap_result.toString());
                            if(soap_result!=null)
                            {
                            	if(soap_result.toString().equalsIgnoreCase("Success"))
                            	{
                            		
                                      bmpPicByteArray = null;
//                                      bi = null;
//                                      fos = null;
                                      
                                      bao.reset();
                                      bao = null;

                                      successImages += "," + fname;//Server;//orgImagePath[index];

                                      Thread.sleep(1000);
                            	}
                            	
                            }

//        

                        } catch (Exception e) {

                            e.printStackTrace();

                            fname = null;
                            
                            bmpPicByteArray=null;
                            bao.reset();
                            bao=null;


                        }

                    } else {
                        fname = null;
                        bmpPicByteArray=null;
                        bao.reset();
                        bao=null;
                    }


                }

        //    }

            fname = successImages;
           
        } catch (Exception e) {
            e.printStackTrace();
           // fname = null;
            fname = "please delete files";

			//for (int index = 0; index < total; index++) {
				fname += "," + orgImagePath;
			//}

        }
        return fname;
    }

    public static Bitmap decodeSampledBitmapFromResource(String orgImagePath,
                                                         int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inJustDecodeBounds = false;
        // BitmapFactory.decodeFile(orgImagePath, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(orgImagePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 2;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight) * 2;
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth) * 2;
            }
        }
        
        options=null;

        return inSampleSize;
    }

    public static void writeString(String args) {	
		try {
			File file = new File(Environment.getExternalStorageDirectory()+"/"+"test1.txt");
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(args);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
