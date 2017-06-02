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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

public class AsyncOperation {
	private AsyncRestTemplate asyrestTemplate = new AsyncRestTemplate();// ?

	public String asncdo(String uri, CountDownLatch latch, Integer method, String paremater, String eresponse,
			String vars) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			if (method == 2) {// post
				HttpHeaders headers = new HttpHeaders();
				MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
				headers.setContentType(type);
				headers.add("Accept", MediaType.APPLICATION_JSON.toString());
				headers.setContentType(type);
				HttpEntity<String> formEntity = new HttpEntity<String>(paremater, headers);

				Map<String, String> vart = JSON.parseObject(vars, new TypeReference<Map<String, String>>() {
				});

				ListenableFuture<?> statResponse = asyrestTemplate.postForEntity(uri, formEntity,
						Class.forName(eresponse), vart);
				addCallBack(statResponse, latch, result);

				System.out.println("  benh post =Receiver : " + result);

			} else {// get
				Map<String, String> vart = JSON.parseObject(vars, new TypeReference<Map<String, String>>() {
				});

				ListenableFuture<?> statResponse = asyrestTemplate.getForEntity(uri, Class.forName(eresponse), vart);
				System.out.println("  benh get =Receiver : " + uri);
				addCallBack(statResponse, latch, result);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result.get("resbody") != null)
			return result.get("resbody").toString();
		return null;
	}

	// 增加回调
	private <T> void addCallBack(ListenableFuture<T> statResponse, CountDownLatch latch, Map<String, Object> result) {
		if (statResponse != null) {
			statResponse.addCallback(new CommonListenableCallBack<T>(result, latch));
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
		System.out.println("  benh uri =Receiver : " + uri);
		return uri;
	}
}
