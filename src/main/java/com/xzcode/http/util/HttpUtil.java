package com.xzcode.http.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 
 * http工具
 * 
 * @author zai
 * 2018-04-08
 */
public class HttpUtil{
	
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	
	private Gson gson = new GsonBuilder().create();
	
	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	
	private OkHttpClient client;
	
	public HttpUtil(int connectTimeout, int writeTimeout, int readTimeout) {
		init(connectTimeout, writeTimeout, readTimeout);
	}
	
	public HttpUtil() {
		init(10,30,30);
	}
	
	public void init(int connectTimeout, int writeTimeout, int readTimeout) {
		this.client = new OkHttpClient
					.Builder()
				    .connectTimeout(connectTimeout, TimeUnit.SECONDS)
				    .writeTimeout(writeTimeout, TimeUnit.SECONDS)
				    .readTimeout(readTimeout, TimeUnit.SECONDS)
				    .build();
	}
	
	/**
	 * 直接get获取数据体字符串
	 * @param url
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-12
	 */
	public String getForBody(String url) throws IOException {
        return getForBody(url, (String)null);
	}
	
	public String getForBody(String url, String charset) throws IOException {
		
		
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		
	    okhttp3.Response response = client.newCall(request).execute();
        String body = null;
        if (charset != null) {
        	body = new String(response.body().bytes(), charset);
		}else {
			body = response.body().string();
		}
        
        /*
        if (logger.isDebugEnabled()) {
        	logger.debug("\ngetForBody:\nurl:{}\nbody:{}", url, body);
		}
        */
        return body;
	}
	
	public String getForBody(String url, Map<String, Object> params) throws IOException {
		
		if (params != null) {
		
			String newUrl = url.trim();
			String paramsUrlPart = params.keySet().stream().map(k -> {
				Object ov = params.get(k);
				String v = String.valueOf(params.get(k) == null ? "" : ov);
				return k + "=" + v;
			}).collect(Collectors.joining("&"));
			
			if (newUrl.endsWith("?")) {
				newUrl += paramsUrlPart;
			}else {
				newUrl += "?" + paramsUrlPart;
			}
			
			return getForBody(newUrl);
			
		}
		return getForBody(url);
			
	}
	
	/**
	 * 获取json数据体并转为对象
	 * @param url
	 * @param params
	 * @param t
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-12
	 */
	public <T> T getJsonForObject(String url, Map<String, Object> params, Class<T> t) throws IOException {
		
		String body = getForBody(url, params);
	            
        return gson.fromJson(body, t);
	}
	
	/**
	 * 获取json数据体并转为对象
	 * @param url
	 * @param params
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-10-26 13:14:59
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getJsonForObject(String url, Map<String, Object> params) throws IOException {
		
		String body = getForBody(url, params);
	            
        return gson.fromJson(body, Map.class);
	}
	
	/**
	 * 获取json数据体并转为Map
	 * @param url
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-10-26 13:15:03
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getJsonForMap(String url) throws IOException {
		
		String body = getForBody(url, (Map<String, Object>)null);
        return gson.fromJson(body, Map.class);
	}
	
	/**
	 * pos表单
	 * @param url
	 * @param formParams
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-13
	 */
	public String postForm(String url, Map<String, Object> formParams) throws IOException {
		Builder formBodyBuilder = new FormBody.Builder();
		if (formParams != null) {
			for (String key : formParams.keySet()) {
				Object val = formParams.get(key);
				if (val != null) {
					formBodyBuilder.add(key, String.valueOf(val));					
				}
			}
		}
		RequestBody formBody = formBodyBuilder.build();
		
	    Request request = new Request.Builder()
	        .url(url)
	        .post(formBody)
	        .build();

	    Response response = client.newCall(request).execute();
	    String respbody = new String(response.body().bytes());
		return respbody;
	}
	
	/**
	 * 提交表单并返回json转换对象
	 * @param url
	 * @param formParams
	 * @param returnObjClass
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-13
	 */
	public <T> T postFormForJsonObject(String url, Map<String, Object> formParams, Class<T> returnObjClass) throws IOException {
		return gson.fromJson(postForm(url, formParams), returnObjClass);
	}
	
	
	/**
	 * post发送json数据体
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-12
	 */
	public String postJsonBody(String url, Map<String, String> params, String json) throws IOException{
		  RequestBody body = RequestBody.create(JSON, json);
		  Request request = new Request.Builder()
		      .url(url)
		      .post(body)
		      .build();
		  Response response = client.newCall(request).execute();
		  String respbody = new String(response.body().bytes());
		  return respbody;
	}
	
	/**
	 * post json数据并返回指定对象
	 * @param url
	 * @param json
	 * @param returnObjectClass
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-12
	 */
	public <T> T postJsonBodyForObject(String url, String json, Class<T> returnObjectClass) throws IOException{
		  RequestBody body = RequestBody.create(JSON, json);
		  Request request = new Request.Builder()
		      .url(url)
		      .post(body)
		      .build();
		  
		  Response response = client.newCall(request).execute();
		  String respbody = new String(response.body().bytes());
		  return gson.fromJson(respbody, returnObjectClass);
	}
	
	/*public static void main(String[] args) throws IOException {
		HttpUtilService utilService = new HttpUtilService();
		
		utilService.postJsonBodyForObject("http://localhost:81/im/webhook?CallbackCommand=$CallbackCommand", "{\r\n" + 
				"    \"CallbackCommand\": \"C2C.CallbackBeforeSendMsg\", \r\n" + 
				"    \"From_Account\": \"jared\",  \r\n" + 
				"    \"To_Account\": \"Jonh\", \r\n" + 
				"    \"MsgBody\": [  \r\n" + 
				"        {\r\n" + 
				"            \"MsgType\": \"TIMTextElem\", \r\n" + 
				"            \"MsgContent\": {\r\n" + 
				"                \"Text\": \"red packet\"\r\n" + 
				"            }\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}", Map.class);
	}*/
	
	/**
	 * post发送json对象并返回指定类型对象
	 * @param url 请求url
	 * @param object 待转为json字符串的对象
	 * @param returnObjectClass 返回指定类型的对象
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-12
	 */
	public <T> T postJsonObjectForObject(String url, Object object, Class<T> returnObjectClass) throws IOException{
		  return postJsonBodyForObject(url, gson.toJson(object), returnObjectClass);
	}
	
	/**
	 * 
	 * @param url
	 * @param filekey
	 * @param filename
	 * @param file
	 * @param fileContentType 文件类型，如：image/png
	 * @return
	 * @throws IOException
	 * 
	 * @author zai
	 * 2018-04-13
	 */
	public String uploadForBody(
			String url, 
			String filekey, 
			String filename, 
			File file, 
			String fileContentType
		) throws IOException {
		return uploadForBody(url, filekey, filename, file, fileContentType, null, null);
	}
	
	/**
	 * 上传文件并返回body字符串
	 * @param url
	 * @param filekey
	 * @param filename
	 * @param file
	 * @param fileContentType
	 * @param params
	 * @param cookies
	 * @return
	 * 
	 * @author zai
	 * 2018-04-12
	 * @throws IOException 
	 */
	public String uploadForBody(
			String url, 
			String filekey, 
			String filename, 
			File file, 
			String fileContentType ,
			Map<String, String> params,
			Map<String, String> cookies
		) throws IOException {
		
			
			RequestBody fileBody = RequestBody.create(MediaType.parse(fileContentType), file);
			
			MultipartBody.Builder mbuilder = new MultipartBody.Builder();
			
			if (params != null) {
				
				for (String key : params.keySet()) {
					mbuilder.addFormDataPart(key, params.get(key));
				}
			}
			
			RequestBody requestBody = mbuilder
					
	                .setType(MultipartBody.FORM) 
	                .addFormDataPart(filekey, filename, fileBody)
	                .build();
			
			Request request = new Request.Builder()
					
				      .url(url)
				      .post(requestBody)
				      .build();
			
			Response response = client.newCall(request).execute();
			String respbody = new String(response.body().bytes());
			return respbody;

	}
	
	
}
