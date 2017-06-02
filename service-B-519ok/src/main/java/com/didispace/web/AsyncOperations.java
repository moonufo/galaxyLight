package com.didispace.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import com.didispace.domain.ReturnResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

public class AsyncOperations {
	AsyncRestTemplate asyrestTemplate;

	public AsyncOperations(AsyncRestTemplate asyrestTemplate) {
		this.asyrestTemplate = asyrestTemplate;
	}

	public void asncdo(Map<String, Object> results, String uri, CountDownLatch latch, Integer method, String paremater,
			String eresponse, String vars) {
		// asyrestTemplate = new AsyncRestTemplate();
		try {
			if (method == 2) {// post
				HttpHeaders headers = new HttpHeaders();
				MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
				headers.setContentType(type);
				headers.add("Accept", MediaType.APPLICATION_JSON.toString());
				headers.setContentType(type);
				 
//				String up3 = null;
//				if (paremater != null)
//					up3 = JSON.toJSONString(paremater);
					 
				HttpEntity<String> formEntity = new HttpEntity<String>(paremater, headers);
				// asyrestTemplate = new AsyncRestTemplate();
				Map<String, String> vart = JSON.parseObject(vars, new TypeReference<Map<String, String>>() {
				});
				ListenableFuture<?> statResponse = asyrestTemplate.postForEntity(uri, formEntity,
						String.class, vart);
				addCallBack(statResponse, latch, results);
			} else {// get
				Map<String, String> vart = JSON.parseObject(vars, new TypeReference<Map<String, String>>() {
				});
				ListenableFuture<?> statResponse = asyrestTemplate.getForEntity(uri, String.class, vart);
				addCallBack(statResponse, latch, results);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// if (results.get("resbody") != null)
		// return results.get("resbody").toString();
		// else
		// return null;
	}

	// 增加回调
	private <T> void addCallBack(ListenableFuture<T> statResponse, CountDownLatch latch, Map<String, Object> results) {
		if (statResponse != null) {
			statResponse.addCallback(new CommonListenableCallBack<T>(results, latch));
		}
	}

	// get compansation uri
	public String getcomUri(String uri) {
		String func = uri.substring(uri.lastIndexOf(":"), uri.length());
		int a = func.indexOf("/");
		int b2 = func.indexOf("/", a + 1);
		if (b2 > 0)
			func = func.substring(a + 1, b2);
		else
			func = func.substring(a + 1);
		StringBuffer urie = new StringBuffer();
		urie.append(uri.substring(0, uri.indexOf(func))).append(func).append("_comp")
				.append(uri.substring(uri.indexOf(func) + func.length()));
		uri = urie.toString();
		return uri;
	}

	public String createParameter(Integer serviceNumber, Integer etypec, String pathParemater, Integer bid, Integer sid,
			String eresponse, String uri, String bapp, String service, Integer etype, Integer method, String paremater,
			String edate) {
		String pj = "{\"serviceNumber\":" + serviceNumber + ",\"etypec\":" + etypec + ",";

		if (null != pathParemater) {
			if (null != service && paremater != null) {
				pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":"
						+ eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp + "\",\"service\":\"" + service
						+ "\",\"etype\":" + etype + ",\"method\":" + method + ",\"paremater\":" + paremater
						+ ",\"edate\":" + edate + "";
			} else if (null != service && paremater == null) {
				pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":"
						+ eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp + "\",\"service\":\"" + service
						+ "\",\"etype\":" + etype + ",\"method\":" + method + ",\"edate\":" + edate + "";
			} else if (null == service && paremater == null) {
				pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":"
						+ eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp + "\",\"etype\":" + etype
						+ ",\"method\":" + method + ",\"" + "edate\":" + edate + "";
			} else if (null == service && paremater != null) {
				pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":"
						+ eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp + "\",\"etype\":" + etype
						+ ",\"method\":" + method + ",\"paremater\":" + paremater + ",\"edate\":" + edate + "";
			}
		} else {
			if (null != service && paremater != null) {
				pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
						+ "\",\"bapp\":\"" + bapp + "\",\"service\":\"" + service + "\",\"etype\":" + etype
						+ ",\"method\":" + method + ",\"paremater\":" + paremater + ",\"edate\":" + edate + "";
			} else if (null != service && paremater == null) {
				pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
						+ "\",\"bapp\":\"" + bapp + "\",\"service\":\"" + service + "\",\"etype\":" + etype
						+ ",\"method\":" + method + ",\"edate\":" + edate + "";
			} else if (null == service && paremater == null) {
				pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
						+ "\",\"bapp\":\"" + bapp + "\",\"etype\":" + etype + ",\"method\":" + method + ",\""
						+ "edate\":" + edate + "";
			} else if (null == service && paremater != null) {
				pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
						+ "\",\"bapp\":\"" + bapp + "\",\"etype\":" + etype + ",\"method\":" + method
						+ ",\"paremater\":" + paremater + ",\"edate\":" + edate + "";
			}
		}
		pj += "}";

		return pj;
	}

	public String send(RestTemplate restTemplate, Integer serviceNumber, Integer etypec, String pathParemater,
			Integer bid, Integer sid, String eresponse, String uri, String bapp, String service, Integer etype,
			Integer method, String paremater, String edate) {
		String pj = "\"serviceNumber\":" + serviceNumber + ",\"etypec\":" + etypec + ",";
		try {
			if (null != pathParemater) {
				if (null != service && paremater != null) {
					pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid
							+ ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp
							+ "\",\"service\":\"" + service + "\",\"etype\":" + etype + ",\"method\":" + method
							+ ",\"paremater\":" + paremater + ",\"edate\":" + edate + "";
				} else if (null != service && paremater == null) {
					pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid
							+ ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp
							+ "\",\"service\":\"" + service + "\",\"etype\":" + etype + ",\"method\":" + method
							+ ",\"edate\":" + edate + "";
				} else if (null == service && paremater == null) {
					pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid
							+ ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp
							+ "\",\"etype\":" + etype + ",\"method\":" + method + ",\"" + "edate\":" + edate + "";
				} else if (null == service && paremater != null) {
					pj += "\"pathParemater\":" + pathParemater + ",\"bid\":" + bid + ",\"sid\":" + sid
							+ ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri + "\",\"bapp\":\"" + bapp
							+ "\",\"etype\":" + etype + ",\"method\":" + method + ",\"paremater\":" + paremater
							+ ",\"edate\":" + edate + "";
				}
			} else {
				if (null != service && paremater != null) {
					pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
							+ "\",\"bapp\":\"" + bapp + "\",\"service\":\"" + service + "\",\"etype\":" + etype
							+ ",\"method\":" + method + ",\"paremater\":" + paremater + ",\"edate\":" + edate + "";
				} else if (null != service && paremater == null) {
					pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
							+ "\",\"bapp\":\"" + bapp + "\",\"service\":\"" + service + "\",\"etype\":" + etype
							+ ",\"method\":" + method + ",\"edate\":" + edate + "";
				} else if (null == service && paremater == null) {
					pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
							+ "\",\"bapp\":\"" + bapp + "\",\"etype\":" + etype + ",\"method\":" + method + ",\""
							+ "edate\":" + edate + "";
				} else if (null == service && paremater != null) {
					pj += "\"bid\":" + bid + ",\"sid\":" + sid + ",\"eresponse\":" + eresponse + ",\"uri\":\"" + uri
							+ "\",\"bapp\":\"" + bapp + "\",\"etype\":" + etype + ",\"method\":" + method
							+ ",\"paremater\":" + paremater + ",\"edate\":" + edate + "";
				}
			}
			// restTemplate = new RestTemplate();
			MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			HttpEntity<String> formEntity = new HttpEntity<String>("{" + pj + "}", headers);
			// SEND Message to TJE
			//String result = restTemplate.postForObject("http://localhost:2222/send", formEntity, String.class);
//			if (result == null) {
//				result = restTemplate.postForObject("http://localhost:2222/send", formEntity, String.class);
//
//			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pj;
	}
}
