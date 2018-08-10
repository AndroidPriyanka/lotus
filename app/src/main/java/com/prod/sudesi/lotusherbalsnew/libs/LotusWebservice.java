package com.prod.sudesi.lotusherbalsnew.libs;

import android.content.Context;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class LotusWebservice {

	Context context;

	// -----------------Mahi

	String url = "http://lotusws.lotussmartforce.com/Service1.svc";// production lotus server
	//String url = "http://sandboxws.lotussmartforce.com/Service1.svc"; // UAT Link Lotus server


	public LotusWebservice(Context con) {
		context = con;
	}

	public SoapPrimitive SaveAttendance(String empid, String date,
			String attend, String absent_type, String lat, String lon) {
		SoapPrimitive result = null;
		try {
			Log.v("", "attendace service called");
            SoapObject request = new SoapObject("http://tempuri.org/",
					"SaveAttendance");

			request.addProperty("emp_id", empid);
			request.addProperty("Adate", date);
			request.addProperty("attendance", attend);
			request.addProperty("AbsentType", absent_type);
			request.addProperty("lat", lat);
			request.addProperty("lon", lon);

			Log.e("AttendanceValues", empid + "---" + date + "---" + attend
					+ "---" + absent_type + "---" + lat + "---" + lon);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SaveAttendance", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("SaveAttendance", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

    public SoapObject SaveAttendanceForDubai(String empid, String date,
                                     String attend, String absent_type, String lat, String lon) {
        SoapObject result = null;
        try {
            Log.v("", "attendace service called");
            SoapObject request = new SoapObject("http://tempuri.org/",
                    "SaveAttendance_Dubai");

            request.addProperty("emp_id", empid);
            request.addProperty("Adate", date);
            request.addProperty("attendance", attend);
            request.addProperty("AbsentType", absent_type);
            request.addProperty("lat", lat);
            request.addProperty("lon", lon);

            Log.e("AttendanceValues", empid + "---" + date + "---" + attend
                    + "---" + absent_type + "---" + lat + "---" + lon);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);// soap envelop with version
            envelope.setOutputSoapObject(request); // set request object
            envelope.dotNet = true;

            HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
            // transport
            // call
            androidHttpTransport.call(
                    "http://tempuri.org/IService1/SaveAttendance_Dubai", envelope);

            result = (SoapObject) envelope.getResponse();
            Log.e("SaveAttendance_Dubai", result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

	public SoapPrimitive SaveStock(String id,String Pid, String CatCodeId,
			String EANCode, String empId, String ProductCategory,
			String product_type, String product_name, String shadeno,
			String Opening_Stock, String FreshStock, String Stock_inhand,
			String SoldStock, String S_Return_Saleable,
			String S_Return_NonSaleable, String ClosingBal, String GrossAmount,
			String Discount, String NetAmount, String Size, String Price,
			String AndroidCreatedDate) {

		SoapPrimitive result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SaveStock");

			if (id == null || id.equalsIgnoreCase("null")) {

				CatCodeId = "0";
			}

			if (CatCodeId == null || CatCodeId.equalsIgnoreCase("null")) {

				CatCodeId = "0";
			}

			if (EANCode == null ||  EANCode.equalsIgnoreCase("null") ) {

				EANCode = "0";
			}

			if (ProductCategory == null || ProductCategory.equalsIgnoreCase("null")) {

				ProductCategory = "";
			}
			
			if (product_type == null || product_type.equalsIgnoreCase("null") ) {

				product_type = "";
			}
			
			if (product_name==null || product_name.equalsIgnoreCase("null") ) {
				product_name = "";
			}
			
			if (shadeno == null || shadeno.equalsIgnoreCase("null")) {

				shadeno = "0";
			}
			
			if (Opening_Stock == null || Opening_Stock.equalsIgnoreCase("null")) {

				Opening_Stock = "0";
			}
			
			if (FreshStock == null || FreshStock.equalsIgnoreCase("null")) {

				FreshStock = "0";
			}
			
			if (SoldStock == null || SoldStock.equalsIgnoreCase("null")) {

				SoldStock = "0";
			}
			
			if (Stock_inhand==null || Stock_inhand.equalsIgnoreCase("null")) {
				Stock_inhand = "";
			}
			
			if (S_Return_Saleable == null || S_Return_Saleable.equalsIgnoreCase("null")) {

				S_Return_Saleable = "0";
			}

			if (S_Return_NonSaleable == null || S_Return_NonSaleable.equalsIgnoreCase("null")) {

				S_Return_NonSaleable = "0";
			}
			
			if (ClosingBal == null || ClosingBal.equalsIgnoreCase("null")) {

				ClosingBal = "0";
			}
			
			if (GrossAmount == null || GrossAmount.equalsIgnoreCase("null")) {

				GrossAmount = "0";
			}

			if (Discount == null || Discount.equalsIgnoreCase("null")) {

				Discount = "0";
			}

			if (NetAmount == null || NetAmount.equalsIgnoreCase("null")) {

				NetAmount = "0";
			}

			if (Size == null || Size.equalsIgnoreCase("null")) {

				Size = "0";
			}
			
			if (Price == null || Price.equalsIgnoreCase("null")) {

				Price = "0";
			}
			
			if (AndroidCreatedDate == null || AndroidCreatedDate.equalsIgnoreCase("null") ) {

				AndroidCreatedDate = "";
			}
			
			request.addProperty("id", id);
			request.addProperty("Pid", Pid);
			request.addProperty("CatCodeId", CatCodeId);
			request.addProperty("EANCode", EANCode);
			request.addProperty("empId", empId);
			request.addProperty("ProductCategory", ProductCategory);
			request.addProperty("product_type", product_type);
			request.addProperty("product_name", product_name);
			request.addProperty("shadeno", shadeno);
			request.addProperty("Opening_Stock", Opening_Stock);
			request.addProperty("FreshStock", FreshStock);
			request.addProperty("Stock_inhand", Stock_inhand);
			request.addProperty("SoldStock", SoldStock);
			request.addProperty("S_Return_Saleable", S_Return_Saleable);
			request.addProperty("S_Return_NonSaleable", S_Return_NonSaleable);
			request.addProperty("ClosingBal", ClosingBal);
			request.addProperty("GrossAmount", GrossAmount);
			request.addProperty("Discount", Discount);
			request.addProperty("NetAmount", NetAmount);
			request.addProperty("Size", Size);
			request.addProperty("Price", Price);
			request.addProperty("AndroidCreatedDate", AndroidCreatedDate);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call("http://tempuri.org/IService1/SaveStock",
					envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("SaveStock", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public SoapPrimitive SaveTesterData(String EmpID, String Pid,
			String CatCodeId, String EANCode, String ProductCategory,
			String ProductType, String ProductName, String shadeno,
			String Current_status, String RequestDate, String DeliveredDate,
			String Size) {
		SoapPrimitive result = null;
		try {
			Log.e("", "tester service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SaveTesterData");

			if (shadeno.equalsIgnoreCase("null")) {
				shadeno = "";
			}
			request.addProperty("EmpID", EmpID);
			request.addProperty("Pid", Pid);
			request.addProperty("CatCodeId", CatCodeId);
			request.addProperty("EANCode", EANCode);
			request.addProperty("ProductCategory", ProductCategory);
			request.addProperty("ProductType", ProductType);
			request.addProperty("ProductName", ProductName);
			request.addProperty("shadeno", shadeno);
			request.addProperty("Current_status", Current_status);
			request.addProperty("RequestDate", RequestDate);
			request.addProperty("DeliveredDate", DeliveredDate);
			request.addProperty("Size", Size);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SaveTesterData", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("SaveTesterData", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * public SoapObject SendNotification() { SoapObject result = null; try {
	 * SoapObject request = new SoapObject("http://tempuri.org/",
	 * "SendNotification"); SoapSerializationEnvelope envelope = new
	 * SoapSerializationEnvelope( SoapEnvelope.VER11);// soap envelop with
	 * version envelope.setOutputSoapObject(request); // set request object
	 * envelope.dotNet = true;
	 * 
	 * HttpTransportSE androidHttpTransport = new HttpTransportSE(url);// http
	 * // transport // call
	 * androidHttpTransport.call("http://tempuri.org/IService1/SendNotification"
	 * , envelope);
	 * 
	 * result = (SoapObject) envelope.getResponse(); } catch (Exception e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * Log.e("SendNotification", result.toString()); return result; }
	 */

	public SoapObject GetNotification(String EmpID) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetNotification");

			request.addProperty("EmpID", EmpID);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/GetNotification", envelope);

			result = (SoapObject) envelope.getResponse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("GetNotification", result.toString());
		return result;
	}

	/*
	 * public SoapPrimitive ImageSaveData(String product_id, String emp_id,
	 * String img_count,String img_name) { SoapPrimitive result = null; try {
	 * SoapObject request = new SoapObject("http://tempuri.org/",
	 * "SaveVisibility");
	 * 
	 * request.addProperty("product_type", product_id);
	 * request.addProperty("emp_id",emp_id);
	 * request.addProperty("img_count",img_count);
	 * request.addProperty("img_name",img_name);
	 * 
	 * SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	 * SoapEnvelope.VER11);// soap envelop with version
	 * envelope.setOutputSoapObject(request); // set request object
	 * envelope.dotNet = true;
	 * 
	 * HttpTransportSE androidHttpTransport = new HttpTransportSE(url);// http
	 * // transport // call
	 * androidHttpTransport.call("http://tempuri.org/IService1/SaveVisibility",
	 * envelope);
	 * 
	 * result = (SoapPrimitive) envelope.getResponse(); Log.e("SaveVisibility",
	 * result.toString());
	 * 
	 * 
	 * } catch(Exception e) { e.printStackTrace(); } return result; }
	 */
	public SoapPrimitive SaveVisibility(String product_type, String emp_id,
			String img_count, String Description, String Latitude,
			String Longitude) {
		SoapPrimitive result = null;
		try {
			Log.e("", "save visibility service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SaveVisibility");
			// /// send link
			request.addProperty("product_type", product_type);
			request.addProperty("emp_id", emp_id);
			request.addProperty("img_count", img_count);
			request.addProperty("Description", Description);
			request.addProperty("Latitude", Latitude);
			request.addProperty("Longitude", Longitude);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SaveVisibility", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("SaveVisibility", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapPrimitive UploadImage(String ProductType, String ImageName,
			int VisibilityID, String captureddate) {
		SoapPrimitive result = null;
		try {
			Log.e("", "upload image service called");

			Log.e("", "ProductType=" + ProductType + " ImageName=" + ImageName
					+ " VisibilityID=" + VisibilityID + " captureddate="
					+ captureddate);
			SoapObject request = new SoapObject("http://tempuri.org/",
					"UploadImage");
			// /// send link

			request.addProperty("ProductType", ProductType);
			request.addProperty("ImageName", ImageName);
			request.addProperty("VisibilityID", VisibilityID);
			request.addProperty("DateCaptureAndroid", captureddate);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/UploadImage", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("UploadImage", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapObject GetLogin(String EmpId, String Password,String version) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/","GetLogin");
			// /// send link
			request.addProperty("EmpId", EmpId);
			request.addProperty("Password", Password);
			request.addProperty("version", version);
			
			Log.e("REQUEST", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call("http://tempuri.org/IService1/GetLogin",envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("GetLogin=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapObject GetProducts(String Date, String username) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetProducts");
			// /// send link
			request.addProperty("Date", Date);
			request.addProperty("EmpId", username);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/GetProducts", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("GetProducts=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapObject GetHoliday(String Month, String Year)

	{
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetHoliday");
			// /// send link
			request.addProperty("month", Month);// "month" is parameter of
												// Getholiday method
			request.addProperty("year", Year);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/GetHoliday", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("GetHoliday=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// BA REPORT

	public SoapObject GetBAOutletSales(String EmpID) {

		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetBAOutletSales");

			request.addProperty("EmpId", EmpID);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/GetBAOutletSales", envelope);
		
			androidHttpTransport.getServiceConnection().disconnect();  //23.04.2015
			
			result = (SoapObject) envelope.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("GetBAOutletSales==", result.toString());
		return result;
	}

	// Ba month wise report

	public SoapObject GetBAOutletMothWiseSales(String EmpID, String month,
			String year) {

		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetBAMonthlyReport");

			request.addProperty("EmpId", EmpID);
			request.addProperty("month", month);
			request.addProperty("year", year);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport
					.call("http://tempuri.org/IService1/GetBAMonthlyReport",
							envelope);

			result = (SoapObject) envelope.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("GetBAOutletmonthSales==", result.toString());
		return result;
	}

	public SoapPrimitive GetServerDate() {
		SoapPrimitive result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetServerDate");
			// /// send link

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/GetServerDate", envelope);

			result = (SoapPrimitive) envelope.getResponse();

			Log.e("GetServerDate=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapPrimitive ChangePassword(String usercode, String password) {
		SoapPrimitive result = null;

		try {
			Log.v("", "username=" + usercode);
			Log.v("", "password=" + password);

			SoapObject request = new SoapObject("http://tempuri.org/",
					"SaveNewPassword");// soap object
			request.addProperty("username", usercode);
			request.addProperty("password", password);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SaveNewPassword", envelope);
			// response soap object
			result = (SoapPrimitive) envelope.getResponse();

			if (result != null) {
				// Log.e("syncLoginTable","Count -- " +
				// String.valueOf(result.getPropertyCount()));
				Log.v("", "CHANGE PASSWORD" + result.toString());
			}

			else {
				Log.e("CHANGE PASSWORD", "NULL");
			}

			return result;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	// cmd.Parameters.Add("@username", EmpId);
	// public List<stock_details> SyncStockData(string EmpId)

	public SoapObject SyncStockData(String empid, String lastsync_date) {
		SoapObject result = null;
		try {
			Log.v("", "sync stock service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SyncStockData");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			request.addProperty("Date", lastsync_date);
			//Log.d("Date of servcer sync result",""+lastsync_date+"-"+empid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SyncStockData", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("SyncStockData=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	//--- tester data download
	public SoapObject SyncGetTesterData(String empid, String lastsync_date) {
		SoapObject result = null;
		try {
			Log.v("", "sync tester data download service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SyncGetTesterData");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			request.addProperty("Date", lastsync_date);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SyncGetTesterData", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("SyncGetTesterData=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	// StoreErro Log for Transaction Download
	public SoapPrimitive StoreErrorLogTablettxt(String sMessage, String line,
			String method, String username, String syncMethod, String status, String date) {

		SoapPrimitive result = null;

		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"StoreErrorLogTablettxt");// soap object

			request.addProperty("sMessage", sMessage);
			request.addProperty("line", line);
			request.addProperty("method", method);
			request.addProperty("username", username);
			request.addProperty("syncMethod", syncMethod);
			request.addProperty("result", status);
			request.addProperty("ErrorDate", date);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/StoreErrorLogTablettxt",
					envelope);
			// response soap object
			result = (SoapPrimitive) envelope.getResponse();

			if (result != null) {

				System.out.print("StoreErrorLogTablettxt_download"
						+ result.toString());
			}

			else {
				Log.e("getProductPoSales", "NULL");
			}
			return result;

		} catch (Exception e) {

			return null;
		}

	}

	// assignUID
	public SoapPrimitive setDeviceId(String username, String uid) {
		SoapPrimitive result = null;

		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"assignUID");
			// soap object
			// request.addProperty("id",id);
			Log.v("", "username=" + username);
			Log.v("", "uid=" + uid);

			request.addProperty("usercode", username);
			request.addProperty("uid", uid);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;
			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/assignUID",
					envelope);

			// response soap object
			result = (SoapPrimitive) envelope.getResponse();
			if (result != null) {
				Log.e("assignUID", "assignUID=" + result.toString());
				System.out.print("assignUID" + result.toString());
			}

			else {
				Log.e("assignUID", "NULL");
			}
			return result;

		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}

	}

	public SoapPrimitive UpdateTableData(String Id, String Updatedata,
			String EmpId) {
		SoapPrimitive result = null;
		try {
			Log.v("", "UpdateTableData service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"UpdateTableData");

			request.addProperty("Id", Id);
			request.addProperty("Updatedata", Updatedata);
			request.addProperty("EmpId", EmpId);

			Log.e("update each row Values", Id + "---" + Updatedata + "---"+ EmpId);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/UpdateTableData", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("UpdateTableData", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// GetVisibilityNotReceivedImage(string EmpId)

	public SoapObject GetVisibilityNotReceivedImage(String username) {
		SoapObject result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetVisibilityNotReceivedImage");
			// /// send link

			request.addProperty("EmpId", username);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport
					.call("http://tempuri.org/IService1/GetVisibilityNotReceivedImage",
							envelope);

			result = (SoapObject) envelope.getResponse();
			//Log.e("GetVisibilityNotReceivedImage=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapPrimitive InsertStockCummData(String Pid, String CatCodeId,
			String EANCode, String empId, String ProductCategory,
			String product_type, String product_name, String shadeno,
			String Opening_Stock, String FreshStock, String Stock_inhand,
			String SoldStock, String S_Return_Saleable,
			String S_Return_NonSaleable, String ClosingBal, String GrossAmount,
			String Discount, String NetAmount, String Size, String Price,
			String AndroidCreatedDate, String Month) {
		SoapPrimitive result = null;

		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"InsertStockCummData");

			request.addProperty("Pid", Pid);
			request.addProperty("CatCodeId", CatCodeId);
			request.addProperty("EANCode", EANCode);
			request.addProperty("empId", empId);
			request.addProperty("ProductCategory", ProductCategory);
			request.addProperty("product_type", product_type);
			request.addProperty("product_name", product_name);
			request.addProperty("shadeno", shadeno);
			request.addProperty("Opening_Stock", Opening_Stock);
			request.addProperty("FreshStock", FreshStock);
			request.addProperty("Stock_inhand", Stock_inhand);
			request.addProperty("SoldStock", SoldStock);
			request.addProperty("S_Return_Saleable", S_Return_Saleable);
			request.addProperty("S_Return_NonSaleable", S_Return_NonSaleable);
			request.addProperty("ClosingBal", ClosingBal);
			request.addProperty("GrossAmount", GrossAmount);
			request.addProperty("Discount", Discount);
			request.addProperty("NetAmount", NetAmount);
			request.addProperty("Size", Size);
			request.addProperty("Price", Price);
			request.addProperty("AndroidCreatedDate", AndroidCreatedDate);
			request.addProperty("Month", Month);
			
			Log.e("request", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/InsertStockCummData",
					envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("InsertStockCummData", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public SoapObject GetTesterProducts(String Date, String username) {
		SoapObject result = null;
		
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetTesterProducts");
			// /// send link
			request.addProperty("Date", Date);
			request.addProperty("EmpId", username);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/GetTesterProducts", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("GetTesterProducts=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	public SoapPrimitive SaveStockEveryDayNetvalue(String Pid, String CatCodeId,
			String EANCode, String empId, String ProductCategory,
			String product_type, String product_name, String shadeno,
			String Opening_Stock, String FreshStock, String Stock_inhand,
			String SoldStock, String S_Return_Saleable,
			String S_Return_NonSaleable, String ClosingBal, String GrossAmount,
			String Discount, String NetAmount, String Size, String Price,
			String AndroidCreatedDate,String date,String boc) {

		SoapPrimitive result = null;
		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"InsertStockEveryDayNetvalue");

			if (CatCodeId == null || CatCodeId.equalsIgnoreCase("null")) {

				CatCodeId = "0";
			}

			if (EANCode == null || EANCode.equalsIgnoreCase("null")) {

				EANCode = "";
			}

			if (ProductCategory == null || ProductCategory.equalsIgnoreCase("null")) {

				ProductCategory = "";
			}
			
			if (product_type == null || product_type.equalsIgnoreCase("null") ) {

				product_type = "";
			}

			if (product_name==null || product_name.equalsIgnoreCase("null") ) {
				product_name = "";
			}
			
			
			if (shadeno == null || shadeno.equalsIgnoreCase("null")) {

				shadeno = "0";
			}
			
			if (Opening_Stock == null || Opening_Stock.equalsIgnoreCase("null")) {

				Opening_Stock = "0";
			}
			
			if (FreshStock == null || FreshStock.equalsIgnoreCase("null")) {

				FreshStock = "0";
			}
			
			if (SoldStock == null || SoldStock.equalsIgnoreCase("null")) {

				SoldStock = "0";
			}
			
			if (Stock_inhand==null || Stock_inhand.equalsIgnoreCase("null")) {
				Stock_inhand = "";
			}

			if (S_Return_Saleable == null || S_Return_Saleable.equalsIgnoreCase("null")) {

				S_Return_Saleable = "0";
			}

			if (S_Return_NonSaleable == null || S_Return_NonSaleable.equalsIgnoreCase("null")) {

				S_Return_NonSaleable = "0";
			}

			if (ClosingBal == null || ClosingBal.equalsIgnoreCase("null")) {

				ClosingBal = "0";
			}

			if (GrossAmount == null || GrossAmount.equalsIgnoreCase("null")) {

				GrossAmount = "0";
			}

			if (Discount == null || Discount.equalsIgnoreCase("null")) {

				Discount = "0";
			}
			
			if (NetAmount == null || NetAmount.equalsIgnoreCase("null")) {

				NetAmount = "0";
			}
			
			if (Size == null || Size.equalsIgnoreCase("null")) {

				Size = "0";
			}
			
			if (Price == null || Price.equalsIgnoreCase("null")) {

				Price = "0";
			}
			
			if (AndroidCreatedDate == null || AndroidCreatedDate.equalsIgnoreCase("null") ) {

				AndroidCreatedDate = "";
			}
			

			request.addProperty("Pid", Pid);
			request.addProperty("CatCodeId", CatCodeId);
			request.addProperty("EANCode", EANCode);
			request.addProperty("empId", empId);
			request.addProperty("ProductCategory", ProductCategory);
			request.addProperty("product_type", product_type);
			request.addProperty("product_name", product_name);
			request.addProperty("shadeno", shadeno);
			request.addProperty("Opening_Stock", Opening_Stock);
			request.addProperty("FreshStock", FreshStock);
			request.addProperty("Stock_inhand", Stock_inhand);
			request.addProperty("SoldStock", SoldStock);
			request.addProperty("S_Return_Saleable", S_Return_Saleable);
			request.addProperty("S_Return_NonSaleable", S_Return_NonSaleable);
			request.addProperty("ClosingBal", ClosingBal);
			request.addProperty("GrossAmount", GrossAmount);
			request.addProperty("Discount", Discount);
			request.addProperty("NetAmount", NetAmount);
			request.addProperty("Size", Size);
			request.addProperty("Price", Price);
			request.addProperty("AndroidCreatedDate", AndroidCreatedDate);
			request.addProperty("Date", date);
			request.addProperty("BOC", boc);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
								Log.v("","Log1");											// call
			androidHttpTransport.call("http://tempuri.org/IService1/InsertStockEveryDayNetvalue",
					envelope);
			
			Log.v("","Log2");
			result = (SoapPrimitive) envelope.getResponse();
			Log.v("","Log3");
			//Log.e("InsertStockEveryDayNetvalue", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public SoapObject SyncStockNetvalue(String empid, String lastsync_date) {
		SoapObject result = null;
		try {
			Log.v("", "sync SyncStockNetvalue service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SyncStockNetvalue");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			request.addProperty("Date", lastsync_date);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SyncStockNetvalue", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("SyncStockNetvalue=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public SoapObject SyncStockCummData(String empid, String lastsync_date) {
		SoapObject result = null;
		try {
			Log.v("", "sync SyncStockCummData service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"SyncStockCummData");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			request.addProperty("Date", lastsync_date);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SyncStockCummData", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("SyncStockCummData=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public SoapPrimitive SaveSupervisorAttendance(String BDE_CODE,String BA_id,String Adate,String Actual_date,String lat,String lon)
	{
		SoapPrimitive result = null;
		try
		{
			
			SoapObject request = new SoapObject("http://tempuri.org/", "SaveSupervisorAttendance");
			
			request.addProperty("BDE_CODE", BDE_CODE);
			request.addProperty("BA_id", BA_id);
			request.addProperty("Adate", Adate);
			request.addProperty("Actual_date", Actual_date);
			request.addProperty("lat", lat);
			request.addProperty("lon", lon);
			
			//Log.e("SaveSupervisorAttendance", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SaveSupervisorAttendance", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			//Log.e("SaveSupervisorAttendance", result.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
		
	}
	
	public SoapPrimitive SaveLogoutTime(String BA_id,String Logout_time)
	{
		SoapPrimitive result = null;
		try
		{
			
			SoapObject request = new SoapObject("http://tempuri.org/", "SaveLogoutTime");
			
			request.addProperty("BA_id", BA_id);
			request.addProperty("Logout_time", Logout_time);
			

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/SaveLogoutTime", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("SaveLogoutTime", result.toString());
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
		
	}
	
	public SoapPrimitive Base64ToImage(String base64String, String imgName) {
		SoapPrimitive result = null;

		try {
			SoapObject request = new SoapObject("http://tempuri.org/",
					"Base64ToImage");
			request.addProperty("base64String", base64String);
			request.addProperty("imgName", imgName);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/Base64ToImage", envelope);

			// response soap object

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("Base64ToImage", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	/*public SoapPrimitive UpdateTableData(String Id, String Updatedata,
			String EmpId) {
		SoapPrimitive result = null;
		try {
			Log.v("", "UpdateTableData service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"UpdateTableData");

			request.addProperty("Id", Id);
			request.addProperty("Updatedata", Updatedata);
			request.addProperty("EmpId", EmpId);

			Log.e("update each row Values", Id + "---" + Updatedata + "---"
					+ EmpId);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url);// http
																			// transport
																			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/UpdateTableData", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("UpdateTableData", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}*/
	
	// dashboard --  
	

	public SoapObject GetDashboradData(String id, String fromdate, String todate, String EMpid,String type){

		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetDashboradData");

			request.addProperty("id", id);
			request.addProperty("fromdate", fromdate);
			request.addProperty("todate", todate);
			request.addProperty("EMpid", EMpid);
			request.addProperty("type", type);
			
			Log.e("Request", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/GetDashboradData", envelope);
		
			androidHttpTransport.getServiceConnection().disconnect();  //23.04.2015
			
			result = (SoapObject) envelope.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("GetDashboradData==", result.toString());
		return result;
	}

	public SoapObject GetBOCOpening(String EmpId)
	{
		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetBOCOpening");

			request.addProperty("EmpId", EmpId);
			
			Log.e("Request", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/GetBOCOpening", envelope);
		
			androidHttpTransport.getServiceConnection().disconnect();  //23.04.2015
			
			result = (SoapObject) envelope.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("GetBOCOpening", result.toString());
		return result;
	
	}

	//-----------------------New Sp for data download----------------------

	public SoapObject DataDownload(String empid, String lastsync_date) {
		SoapObject result = null;
		try {
			Log.v("", "sync stock service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"DataDownload");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			request.addProperty("Date", lastsync_date);
			Log.d("Date server sync result",""+lastsync_date+"-"+empid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/DataDownload", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("DataDownload=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapObject DataDownloadForSale(String empid, String lastsync_date) {
		SoapObject result = null;
		try {
			Log.v("", "sync stock service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"DataDownloadForSale");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			request.addProperty("Date", lastsync_date);
			Log.d("Date server sync result",""+lastsync_date+"-"+empid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/DataDownloadForSale", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("DataDownloadForSale=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	public SoapObject GetFLROutlet(String empid) {
		SoapObject result = null;
		try {
			Log.v("", "sync stock service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"GetFLROutlet");
			// /// send link
			Log.v("", "empid==" + empid);
			request.addProperty("EmpId", empid);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/GetFLROutlet", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("GetFLROutlet=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapPrimitive FLROutletAttendance(String aid, String outletcode) {
		SoapPrimitive result = null;
		try {
			Log.v("", "sync stock service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"FLROutletAttendance");
			// /// send link
			request.addProperty("ID", aid);
			request.addProperty("Outletcode", outletcode);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/FLROutletAttendance", envelope);

			result = (SoapPrimitive) envelope.getResponse();
			Log.e("FLROutletAttendance=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public SoapObject DubaiGetDashboardData(String fromdate, String todate, String outletcode,String EMpid){

		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"DubaiGetDashboardData");

			request.addProperty("FromDate", fromdate);
			request.addProperty("ToDate", todate);
			request.addProperty("OutletCode", outletcode);
			request.addProperty("bacode", EMpid);

			Log.e("Request", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/DubaiGetDashboardData", envelope);

			androidHttpTransport.getServiceConnection().disconnect();  //23.04.2015

			result = (SoapObject) envelope.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("DubaiGetDashboardData==", result.toString());
		return result;
	}

	public SoapObject DubaiBAOutletSale(String bacode, String OutletCode) {

		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"DubaiBAOutletSale");

			request.addProperty("bacode", bacode);
			request.addProperty("OutletCode", OutletCode);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/DubaiBAOutletSale", envelope);

			androidHttpTransport.getServiceConnection().disconnect();  //23.04.2015

			result = (SoapObject) envelope.getResponse();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("DubaiBAOutletSale==", result.toString());
		return result;
	}

	public SoapObject DubaiTotalOutletSaleAPK(String bacode, String FromDate, String Todate)
	{
		SoapObject result = null;
		try {

			SoapObject request = new SoapObject("http://tempuri.org/",
					"DubaiTotalOutletSaleAPK");

			request.addProperty("bacode", bacode);
			request.addProperty("FromDate", FromDate);
			request.addProperty("Todate", Todate);

			Log.e("Request", request.toString());

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url);// http
			// transport
			// call
			androidHttpTransport.call("http://tempuri.org/IService1/DubaiTotalOutletSaleAPK", envelope);

			//androidHttpTransport.getServiceConnection().disconnect();  //23.04.2015

			result = (SoapObject) envelope.getResponse();

			Log.e("DubaiTotalOutletSaleAPK", result.toString());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;

	}

	public SoapObject DataDownloadDubai(String bacode, String Fromdate, String Todate, String outletcode) {
		SoapObject result = null;
		try {
			Log.v("", "sync stock service called");
			SoapObject request = new SoapObject("http://tempuri.org/",
					"DataDownloadDubai");
			request.addProperty("bacode", bacode);
			request.addProperty("Fromdate", Fromdate);
			request.addProperty("Todate", Todate);
			request.addProperty("outletcode", outletcode);
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);// soap envelop with version
			envelope.setOutputSoapObject(request); // set request object
			envelope.dotNet = true;

			HttpTransportSE androidHttpTransport = new HttpTransportSE(url,60000);// http
			// transport
			// call
			androidHttpTransport.call(
					"http://tempuri.org/IService1/DataDownloadDubai", envelope);

			result = (SoapObject) envelope.getResponse();
			Log.e("DataDownloadDubai=", result.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	//------------------------------END--------------------------------------
	
}
