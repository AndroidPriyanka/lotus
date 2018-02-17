package com.prod.sudesi.lotusherbalsnew.libs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import com.prod.sudesi.lotusherbalsnew.LoginActivity;
import com.prod.sudesi.lotusherbalsnew.dbConfig.Dbcon;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {
	 
	 final public static String ONE_TIME = "onetime";
	 

	Dbcon db;
	
	 
	
	 @Override
	 public void onReceive(Context context, Intent intent) {
	   PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
	         //Acquire the lock
	         wl.acquire();
	 
	         
	 		
	         //You can do the processing here.
	         Bundle extras = intent.getExtras();
	         StringBuilder msgStr = new StringBuilder();
	          
	         if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
	          //Make sure this intent has been sent by the one-time timer button.
	          msgStr.append("One time Timer : ");
	         }
	         Format formatter = new SimpleDateFormat("hh:mm:ss a");
	         msgStr.append(formatter.format(new Date()));
	         Log.v("","aram raised=="+msgStr);
	         
	       //-----------  
	        
	        
	         String lastLogintime=null;
	        try{
	         db = new Dbcon(context);
	         
	         db.open();
	         
	        lastLogintime = db.getLastLogintime();
	         
	         db.close();
	         
	         
	        }catch(Exception e){
	        	e.printStackTrace();
	        	
	        }
	        
	         
	         SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	        // String ttt = sdff.format(new Date()).toString();
	         Date date=null;
	         String time1 = "";
			try {
				
				
				DateFormat inFormat = new SimpleDateFormat( "MM/dd/yyyy hh:mm:ss");
				
				
				if(lastLogintime!=null){
					
					 date = inFormat.parse(lastLogintime);
					
				}else{
				
				 
				 String ttt = sdf1.format(new Date()).toString();
					date = inFormat.parse(ttt);
				}
				 
				 
				time1 = sdf1.format(date);
				
				
			} catch (java.text.ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	         
	       //  String time1 = date.toString();
	         //String time1 = ttt+" 23:30:00";
	         	 
	         	
	         	 
	         	 Date date2 = null;
	 		      Date date1 = null;
	 		      
	 		      
	 		     // SimpleDateFormat sdf = new SimpleDateFormat("mm/dd/yyy hh:mm:ss aa");
	 		      SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	 		      String currentDateandTime = sdf.format(new Date()).toString();
	 		      Log.v("","currentDateandTime=="+currentDateandTime);
	 		      
	 		    
	 		      
	 		    try{
	 				 try {
	 				 if(time1.equalsIgnoreCase("")){
	 						 
	 						 
	 					 
	 					date2 = sdf.parse(currentDateandTime);
	 					//date1 = sdf.parse(currentDateandTime);
	 					 }
	 					else{
	 						
	 						date2 = sdf.parse(currentDateandTime);
	 						try{
	 						date1 = sdf.parse(time1);
	 						}catch(java.text.ParseException e){
	 							
	 							
	 							StringWriter errors = new StringWriter();
	 							e.printStackTrace(new PrintWriter(errors));
	 							
	 							e.printStackTrace();
	 							//we.writeToSD(errors.toString());
	 						}
	 					}
	 				} catch (java.text.ParseException e) {
	 					// TODO Auto-generated catch block
	 					
	 					StringWriter errors = new StringWriter();
	 					e.printStackTrace(new PrintWriter(errors));
	 					
	 					e.printStackTrace();
	 					//we.writeToSD(errors.toString());
	 				}
	 				 
	 				 //Log.v("","date2.getTime()=="+date2.getTime());
	 				// Log.v("","date1.getTime()=="+date1.getTime());
	 				 
	 				 try{
	 				 
	 				 long diff = date2.getTime() - date1.getTime();
	 			       // long diffSeconds = diff / 1000 % 60;
	 			        long diffMinutes = diff / (60 * 1000) % 60;
	 			        long diffHours = diff / (60 * 60 * 1000);
	 			        
	 			        
	 			        Log.v("","i============"+diffMinutes);
	 				    //  if (date1.compareTo(date2)==0)
	 			        if(diffMinutes==120)
	 				      {
	 			        	  
	 		        	 Intent i = new Intent(context, LoginActivity.class);
	 			          i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	 			        
	 			          context.startActivity(i);
	 			          
	 				      }
	 			        
	 				 }catch(Exception e){
	 					 
	 					 e.printStackTrace();
	 				 }
	 			    wl.release(); 
	 			      
	 			} catch (ParseException e) {
	 				// TODO Auto-generated catch block
	 				StringWriter errors = new StringWriter();
	 				e.printStackTrace(new PrintWriter(errors));
	 				
	 				e.printStackTrace();
	 				//we.writeToSD(errors.toString());
	 			}
	         // ------------- 
	         
	         
	        
	         //Release the lock
	       
	 }
	 
	 public void SetAlarm(Context context)
	    {
	        AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
	        intent.putExtra(ONE_TIME, Boolean.FALSE);
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	        //After after 5 seconds
	       // am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 180 , pi);
	        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000 , pi);

	      //  am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 7200000 , pi);
	        
	        
	       /* Calendar calendar = Calendar.getInstance();
	        calendar.setTimeInMillis(System.currentTimeMillis());
	        calendar.set(Calendar.HOUR_OF_DAY, 17);
	        calendar.set(Calendar.MINUTE, 00);
	        calendar.set(Calendar.SECOND, 00);

	        // With setInexactRepeating(), you have to use one of the AlarmManager interval
	        // constants--in this case, AlarmManager.INTERVAL_DAY.
	        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
	                AlarmManager.INTERVAL_DAY, pi);*/
	    }
	 
	    public void CancelAlarm(Context context)
	    {
	        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
	        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
	        alarmManager.cancel(sender);
	    }
	 
	    public void setOnetimeTimer(Context context){
	     AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
	        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
	        intent.putExtra(ONE_TIME, Boolean.TRUE);
	        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
	        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
	    }
	}