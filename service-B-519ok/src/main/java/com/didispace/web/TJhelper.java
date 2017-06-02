package com.didispace.web;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
 

public class TJhelper {
	public static final String EXCHANGE = "spring-boot-exchanger";
	public static final String ROUTINGKEY = "spring-boot-routingKey";// event
	public static final String ROUTINGKEY1 = "ms-send";// call service
	public static final String ROUTINGKEY2 = "ms-get";// each bapp get result

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

	public String send(RabbitTemplate rabbitTemplate, RestTemplate restTemplate, Integer serviceNumber, Integer etypec,
			String pathParemater, Integer bid, Integer sid, String eresponse, String uri, String bapp, String service,
			Integer etype, Integer method, String paremater, String edate, Integer mtype) {
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

			pj += ",\"mtype\":" + mtype + "" ;
			
			 // pj = JSON.toJSONString(pj);
			  pj = "{" + pj+ "}";
			// restTemplate = new RestTemplate();
			// MediaType type =
			// MediaType.parseMediaType("application/json;charset=UTF-8");
			// HttpHeaders headers = new HttpHeaders();
			// headers.setContentType(type);
			// headers.add("Accept", MediaType.APPLICATION_JSON.toString());
			// HttpEntity<String> formEntity = new HttpEntity<String>("{" + pj +
			// "}", headers);
			// //restTemplate.getMessageConverters().add(new
			// MappingJackson2HttpMessageConverter());
			// restTemplate.getMessageConverters().add(new
			// StringHttpMessageConverter());

			// SEND Message to TJE
			// rabbitTemplate = rabbitTemplate1();

			CorrelationData correlationId = new CorrelationData(sid.toString());
			 
			if (mtype ==2) {
				 
				rabbitTemplate.convertAndSend( EXCHANGE,  ROUTINGKEY1, pj, correlationId);

			} else if (mtype ==1){ 
				 
				rabbitTemplate.convertAndSend( EXCHANGE,  ROUTINGKEY, pj, correlationId);

			} else if (mtype ==3){
				 
				rabbitTemplate.convertAndSend(  EXCHANGE,  ROUTINGKEY2, pj, correlationId);

			} 
			// String result =
			// restTemplate.postForObject("http://localhost:2225/send",
			// formEntity, String.class);
			// if (result == null) {
			// result = restTemplate.postForObject("http://localhost:2225/send",
			// formEntity, String.class);
			// }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pj;
	}
}
