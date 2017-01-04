package cn.gb40;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 * <pre>
 * httpClient 请求后台数据并生成excel 报表。
 * </pre>
 * @author wangwenhui   946374340@qq.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class HttpClientTest {
	
	private static  CookieStore cookieStore = new BasicCookieStore();  
	public  static String root="";
	private static  String SESSIONID="JSESSIONID=A1AF53924DAFE902A8175BD1AF1E16D1;";
	/**
	 * 存放cookie数据的文件.json
	 */
	private    static  String autobg;

	/**
	 * 初始化桌面路径
	 */
    static  {
		File desktopDir = FileSystemView.getFileSystemView()
				.getHomeDirectory();
			String desktopPath = desktopDir.getAbsolutePath();
			root=desktopPath+"\\";
			  File autobgtemp= new File(root+"autobg.json");
			  autobg=root+"autobg.json";
			 FileReader fr=null;
			try {
				fr = new FileReader(autobgtemp);
				String AUTOBG=FileCopyUtils.copyToString(fr);
				SESSIONID =AUTOBG.split(";")[1]+";";
			fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}
	
	private static	RequestConfig requestConfig = RequestConfig.custom()
			.setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
	private static   CloseableHttpClient httpClient = HttpClients.custom().
			setDefaultRequestConfig(requestConfig)
			.setDefaultCookieStore(cookieStore)
			.build();


   //导出目录以及操作目录

	private    static  File resultTxt=new File(root+"result.txt");
//	private static final File codeJPG=new	 File("E:\\mt-ide\\workspace\\Identifying-code\\temp3\\", "code.jpg");
//	private static final String codeSource="E:\\mt-ide\\workspace\\Identifying-code\\temp3\\code.jpg";
	private static final File codeJPG=new	 File(root+"Identifying-code\\temp3\\", "code.jpg");
	private static final String codeSource=root+"Identifying-code\\temp3\\code.jpg";
	

	
	public static int main(final String date) throws Exception {
		System.out.println(autobg);
	
		
		// 创建一个HttpClient
		     int result=0;
		     //没有autoId调用
		      //本来已经拿了autobg的
//		     successLogin(false);
		     
		     //含有autoid的调用这个方法
		     String content=null;
		     boolean validate=false;
         try{
			 content=autoLogin(date);
			 validate=true;
}catch(Exception e){
	  e.printStackTrace();
	  successLogin(true);
}
         //避免请求两次
		if(!validate){
			 content=autoLogin(date);
		}
			System.out.println(content);
			FileCopyUtils.copy(content.getBytes(), resultTxt);
			JSONObject obj = JSONObject.parseObject(content);
			 result=obj.getIntValue("iTotalRecords");
			final JSONArray json = obj.getJSONArray("aaData");
			if(result>0){
				try{
				//导出数据 使用线程池 分开线线程执行
					     ThreadPoolUtil.executorService.execute(new Runnable() {
								@Override
								public void run() {
									 try {
										 //执行文章id任务
										Test_1.main(json,date);
									} catch (FileNotFoundException e) {
				                       e.printStackTrace();
									}
								}
							});
					     ThreadPoolUtil.executorService.execute(new Runnable() {
								@Override
								public void run() {
									 try {
										 //执行标题线程
										Test_2.main(json,date);
									} catch (FileNotFoundException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							});
                
          
                 
//			if (json.size() > 0) {
//				System.out.println("文章总数："+json.size());
//				for (int i = 0; i < json.size(); i++) {
//					JSONObject job = json.getJSONObject(i); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
//					System.out.println("文章标题="+job.get("title")); // 得到 每个对象中的属性值
//				}
//			}
		} catch ( Exception e) {
					FileCopyUtils.copy(content.getBytes(), resultTxt);
			}
			if (json.size() > 0) {
				System.out.println("文章总数："+json.size());
				for (int i1 = 0; i1 < json.size(); i1++) {
					JSONObject job = json.getJSONObject(i1); // 遍历 jsonarray 数组，把每一个对象转成 json 对象
					System.out.println("文章标题="+job.get("title")); // 得到 每个对象中的属性值
				}
			}

			}
			

			
			return result;
	}
	
	public static boolean printResponse(HttpResponse httpResponse)
			throws ParseException, IOException {
		// 获取响应消息实体
		HttpEntity entity = httpResponse.getEntity();
		boolean result = false;
		// 响应状态
		System.out.println("status:" + httpResponse.getStatusLine());
		if (httpResponse.getStatusLine().toString().indexOf("302") > 0) {
			result = true;
		}
		
		
		//根据返回的响应码判断
		if(!result){
//			successLogin(true);
		}
//		System.out.println("headers:");
//		HeaderIterator iterator = httpResponse.headerIterator();
//		while (iterator.hasNext()) {
//			 iterator.next();
//			System.out.println("\t" + iterator.next());
//		}
		// 判断响应实体是否为空
		if (entity != null) {
			String responseString = EntityUtils.toString(entity);
			System.out.println("response length:" + responseString.length());
			System.out.println("response content:" + responseString.replace("\r\n", ""));
			
		}
		return result;
	}
	
	public static Map<String, String> cookieMap = new HashMap<String, String>(64);
	
	// 从响应信息中获取cookie
	public static String setCookie(HttpResponse httpResponse) {
//		System.out.println("----setCookieStore");
		Header headers[] = httpResponse.getHeaders("Set-Cookie");
		if (headers == null || headers.length == 0) {
			System.out.println("----there are no cookies");
			return null;
		}
		String cookie = "";
		for (int i = 0; i < headers.length; i++) {
			cookie += headers[i].getValue();
			if (i != headers.length-1 ) {
				cookie += ";";
			}
		}
//		System.out.println(cookie);
		
		String cookies[] = cookie.split(";");
		
		for (String c : cookies) {
			c = c.trim();
			if (cookieMap.containsKey(c.split("=")[0])) {
				cookieMap.remove(c.split("=")[0]);
			}
			cookieMap.put(c.split("=")[0], c.split("=").length == 1 ? ""
					: (c.split("=").length == 2 ? c.split("=")[1] : c.split("=", 2)[1]));
		}
//		System.out.println("----setCookieStore success");
		String cookiesTmp = "";
		for (String key : cookieMap.keySet()) {
			cookiesTmp += key + "=" + cookieMap.get(key) + ";";
		}
		
		return cookiesTmp.substring(0, cookiesTmp.length() - 1);

	}
	
	public static Map<String,Object> login() throws ClientProtocolException, IOException{
		    HttpGet getCaptcha = new
				 HttpGet("http://xmiles.cn/xmiles-manager/system/vacode.jsp");
		    getCaptcha.setHeader("Cookie", SESSIONID);
				 CloseableHttpResponse imageResponse = httpClient.execute(getCaptcha);
						
				 FileOutputStream out = new
				 FileOutputStream(codeSource);
				 byte[] bytes = new byte[8192];
				 int len;
				 while ((len = imageResponse.getEntity().getContent().read(bytes)) != -1) {
				 out.write(bytes,0,len);
				 }
				 out.close();
				 String validateCode ="";
				 try {
				 validateCode = ImageTest.getValidateCode(codeJPG);
				 } catch (Exception e) {
				 }
				 System.out.println(validateCode);
				 MessageDigest md = null;
				 try {
				 md = MessageDigest.getInstance("MD5");
				 } catch (NoSuchAlgorithmException e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				 }
				 String password="sqz@2015#";
				 md.update(password.getBytes());
				 //通过执行诸如填充之类的最终操作完成哈希计算。
				 byte b[] = md.digest();
				 //生成具体的md5密码到buf数组
				 int i;
				 StringBuffer buf = new StringBuffer("");
				 for (int offset = 0; offset < b.length; offset++) {
				 i = b[offset];
				 if (i < 0)
				 i += 256;
				 if (i < 16)
				 buf.append("0");
				 buf.append(Integer.toHexString(i));
				 }
				 System.out.println("32位: " + buf.toString());// 32位的加密
				 password= buf.toString().toUpperCase();
				 //构造post数据
				 List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
				 valuePairs.add(new BasicNameValuePair("userId", "sheqiaozhi"));
				 valuePairs.add(new BasicNameValuePair("password",password));
				 valuePairs.add(new BasicNameValuePair("valicode", validateCode));
				 valuePairs.add(new BasicNameValuePair("autologin", "on"));
				 UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
				// 创建一个post请求
				HttpPost post = new HttpPost("http://xmiles.cn/xmiles-manager/system/login.action");
				// //注入post数据
				 post.setEntity(entity);
				 post.setHeader("Cookie", SESSIONID);
				HttpResponse httpResponse = httpClient.execute(post);
				Map<String,Object>dataMap=new HashMap<String,Object>();
				String AUTOBG="";
				 Header[] header= httpResponse.getAllHeaders();
					for (Header header2 : header) {
						if(header2.getName().equals("Set-Cookie"))
							if(header2.getValue().contains("AUTOBG")){
								AUTOBG=header2.getValue().split("=")[1].split(";")[0];
							}
						
					}
				FileCopyUtils.copy(AUTOBG.getBytes(), new File(autobg));
				dataMap.put("result", printResponse(httpResponse));
				dataMap.put("response", httpResponse);
				return dataMap;
	}
	
	public static Map<String,Object> login2() throws ClientProtocolException, IOException{
		HttpPost post = new HttpPost("http://xmiles.cn/xmiles-manager/system/login.action");
		// //注入post数据
		 FileReader fr=new FileReader(autobg);
			String AUTOBG=FileCopyUtils.copyToString(fr);
			fr.close();
			String cookie=SESSIONID+AUTOBG.split(";")[0]+";";
		     post.setHeader("Cookie",cookie );
		     BasicClientCookie newCookie = new BasicClientCookie("name", "zhaoke");   
		     newCookie.setVersion(0);    
		     newCookie.setDomain("/pms/");   //设置范围  
		     newCookie.setPath("/");   
		     newCookie.setAttribute("SERVERID", "5313e4d40abd77f16efabaa01e4c2358|1483515173|1483515173");
		     newCookie.setAttribute("AUTOBG", "5B471FC7C5AEEC0A115495380D2FA7DC5326FCA2A36D0D193CD18DB9CD42D33A6BE496BBF4B23BD427D95FD86378F7AED52482737387BC408E04ED4F55F595A7");
	          cookieStore.addCookie(newCookie); 
	          List<Cookie> cookies = cookieStore.getCookies();  
	          for (int i = 0; i < cookies.size(); i++) {  
	              System.out.println("Local cookie: " + cookies.get(i));  
	          }  
			HttpResponse httpResponse = httpClient.execute(post);
			Map<String,Object>dataMap=new HashMap<String,Object>();
			dataMap.put("result", printResponse(httpResponse));
			dataMap.put("response", httpResponse);
		return dataMap;

	}
	
	public static CloseableHttpResponse query(
			String c,String date){
		HttpPost g = new HttpPost(
				"http://xmiles.cn/xmiles-manager/discovery/daily/querydiscoverystat.action");
		g.setHeader("Cookie", c);
		String aoData="[{'name':'sEcho','value':1},{'name':'iColumns','value':8},{'name':'sColumns','value':'title,stylename,categoryname,pushdate,PV,UV,ck_module,discoveryid'},{'name':'iDisplayStart','value':0},{'name':'iDisplayLength','value':10000},{'name':'mDataProp_0','value':'title'},{'name':'sSearch_0','value':''},{'name':'bRegex_0','value':false},{'name':'bSearchable_0','value':true},{'name':'bSortable_0','value':false},{'name':'mDataProp_1','value':'stylename'},{'name':'sSearch_1','value':''},{'name':'bRegex_1','value':false},{'name':'bSearchable_1','value':true},{'name':'bSortable_1','value':false},{'name':'mDataProp_2','value':'categoryname'},{'name':'sSearch_2','value':''},{'name':'bRegex_2','value':false},{'name':'bSearchable_2','value':true},{'name':'bSortable_2','value':false},{'name':'mDataProp_3','value':'pushdate'},{'name':'sSearch_3','value':''},{'name':'bRegex_3','value':false},{'name':'bSearchable_3','value':true},{'name':'bSortable_3','value':false},{'name':'mDataProp_4','value':'PV'},{'name':'sSearch_4','value':''},{'name':'bRegex_4','value':false},{'name':'bSearchable_4','value':true},{'name':'bSortable_4','value':true},{'name':'mDataProp_5','value':'UV'},{'name':'sSearch_5','value':''},{'name':'bRegex_5','value':false},{'name':'bSearchable_5','value':true},{'name':'bSortable_5','value':true},{'name':'mDataProp_6','value':'ck_module'},{'name':'sSearch_6','value':''},{'name':'bRegex_6','value':false},{'name':'bSearchable_6','value':true},{'name':'bSortable_6','value':false},{'name':'mDataProp_7','value':'discoveryid'},{'name':'sSearch_7','value':''},{'name':'bRegex_7','value':false},{'name':'bSearchable_7','value':true},{'name':'bSortable_7','value':false},{'name':'sSearch','value':''},{'name':'bRegex','value':false},{'name':'iSortCol_0','value':4},{'name':'sSortDir_0','value':'desc'},{'name':'iSortingCols','value':1}]";
		List<NameValuePair> valuePairs2 = new LinkedList<NameValuePair>();
		valuePairs2.add(new BasicNameValuePair("fromDate", date));
		valuePairs2.add(new BasicNameValuePair("jquery-table_length", "1000"));
		valuePairs2.add(new BasicNameValuePair("aoData", aoData));
		UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(valuePairs2, Consts.UTF_8);
		g.setEntity(entity1);
		CloseableHttpResponse r = null;
		try {
			r = httpClient.execute(g);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return r;
		
	}
	
	public static String  autoLogin(String date)  throws Exception{
		String content=null;
		CloseableHttpResponse r=null;
		try{
			 Map<String,Object> result2=login2();
				HttpResponse  httpResponse=(HttpResponse) result2.get("response");
					String c = setCookie(httpResponse);
					c = c 	+ ";"+SESSIONID;			// 将cookie注入到get请求头当中
		            FileReader fr=new FileReader(autobg);
					final String AUTOBG=FileCopyUtils.copyToString(fr);
					c = c 	+AUTOBG;
					System.out.println(c);
					fr.close();
					
					final String tempC=c;
					//使用線程池判斷是否需要重新生成新的autobg
					  ThreadPoolUtil.executorService.execute(new Runnable() {
						@Override
						public void run() {
							//判断是否有新的jessionID 存在就保存在本地文件中
									String[] cookie=tempC.split(";");
									String lastJessionId=SESSIONID;
								for(int i=0;i<cookie.length;i++){
									if(cookie[i].indexOf("JSESSIONID=")!=-1){
										if(!StringUtils.equals(cookie[i]+";", SESSIONID)){
											lastJessionId=cookie[i]+";";
										}else
										{
											continue;
										}
									}else{
										continue;
									}
								}
						if(!lastJessionId.equals(SESSIONID)){
							//拼装新的autobg
							String newAutoBg=AUTOBG.split(";")[0]+";"+lastJessionId;
							try {
								FileCopyUtils.copy(newAutoBg.getBytes(), new File(autobg));
							} catch (IOException e) {
								System.out.println("[autoLogin] FileCopyUtils occur exception");
							}
							System.out.println("[autoLogin] copy new autobg success!");
						}else{
							System.out.println("[autoLogin] last new JESSIONID is the same with the old JESSIONID,so not copy to the autobg file");
						}
						
						}
					});
				
					r=query(c,date);
		}catch(Exception e){
			throw new Exception();
		}
			content = EntityUtils.toString(r.getEntity(), "UTF-8");
			return content;
		
	}
	
   public  static void successLogin(boolean check) throws ClientProtocolException, IOException{
	   while(check){
   	    Map<String,Object>data=login();
   	    if((boolean) data.get("result")){
		     	 System.out.println("登陆成功");
					break;
		     }else{
		      	 System.out.println("登陆失败");
		     }
      }
   }
}
