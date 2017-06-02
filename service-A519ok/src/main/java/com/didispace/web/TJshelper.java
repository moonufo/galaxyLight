package com.didispace.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.didispace.domain.ReturnResult;

public class TJshelper {
	public String send(RestTemplate restTemplate, Integer serviceNumber, Integer etypec, String pathParemater,
			Integer bid, Integer sid, String eresponse, String uri, String bapp, String service, Integer etype,
			Integer method, String paremater, String edate, Integer mtype) {
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
			pj += ",\"mtype\":" + mtype + "";
			// restTemplate = new RestTemplate();
			MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(type);
			headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			HttpEntity<String> formEntity;
			 
				formEntity = new HttpEntity<String>("{" + pj + "}", headers);
				// SEND call Message to TJE
				String result = restTemplate.postForObject("http://localhost:2225/send", formEntity, String.class);
				if (result == null) {
					result = restTemplate.postForObject("http://localhost:2225/send", formEntity, String.class);

				 

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pj;
	}

	public String saveLog(EventProcessMapper eventMapper, String result,String pevh, Integer etype) {
		Integer ren = 0;
		String bsn = "";
		try {
			SimpleDateFormat hm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			JSONObject obj = JSONObject.parseObject(pevh);
			String bapp = obj.getString("bapp");
			Integer serviceNumber = obj.getInteger("serviceNumber");
			Integer method = obj.getInteger("method");
			String paremater = obj.getString("paremater");
			String pathParemater = obj.getString("pathParemater");
			String edate = hm.format(new Date());
//			JSONObject up2 = obj.getJSONObject("paremater");
//			String up3 = null;
//			if (up2 != null)
//				up3 = up2.toJSONString();
			System.out.println(  "==paremater888Receiver : " + paremater);
			String uri = obj.getString("uri");
			String service = obj.getString("service");
			Integer bid = obj.getInteger("bid");

			Integer sid = obj.getInteger("sid");
			//String eresponse = obj.getString("eresponse");
			String pj = "";
			String pap = obj.getString("pathParemater");
			Map<String, String> vars = JSON.parseObject(pap, new TypeReference<Map<String, String>>() {
			});
			vars.put("pevh", pevh);
			pathParemater = JSON.toJSONString(vars);
			// do 服務，if 服务成功 4，fail 5
			etype = 4;
			ren = eventMapper.insertLog(serviceNumber, 0, pathParemater, result, uri, method, bapp, bid, sid,
					service, etype, paremater, edate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bsn;
	}
	@Transactional(propagation=Propagation.REQUIRED)
	public String savePostlog(String result ,String paremater, Integer method, String pathParemater, String pevh, Integer etypec,
			HttpServletRequest request, EventProcessMapper eventMapper) {

		Integer ren = 0;
		String bsn = "";
		try {
			SimpleDateFormat hm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String[] sp = pevh.split("]");
			Integer bid = Integer.parseInt(sp[1]);
			Integer sid = Integer.parseInt(sp[2]);
			Integer serviceNumber = Integer.parseInt(sp[3]);
			String service = sp[4];
			String edate = hm.format(new Date());
			String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
			String uri = basePath + sp[6];
			//String eresponse = JSONObject.toJSONString(ReturnResult.class);
			//eresponse = eresponse.substring(1, eresponse.length() - 1);
			String bapp = sp[5];

			// do 服務，if 服务成功 4，fail 5

			ren = eventMapper.insertLog(serviceNumber, 0, pathParemater, result, uri, method, bapp, bid, sid,
					service, etypec, paremater, edate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bsn;

	}

	public Integer getSid(String pevh) {

		Integer sid = 0;
		try {
			JSONObject obj = JSONObject.parseObject(pevh);
			  sid = obj.getInteger("sid");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sid;
	}

	public String getBsn(String pevh) {

		StringBuffer bsn = new StringBuffer();
		try {
			JSONObject obj = JSONObject.parseObject(pevh);
			String service = obj.getString("service");
			Integer sid = obj.getInteger("sid");
			bsn.append(sid).append(service);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bsn.toString();
	}

	public String getpBsn(String pevh) {

		StringBuffer bsn = new StringBuffer();
		try {
			String[] sp = pevh.split("]");
			String service = sp[4];
			String sid = sp[2];
			bsn.append(sid).append(service);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bsn.toString();
	}
}
