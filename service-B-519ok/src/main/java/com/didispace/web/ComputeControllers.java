package com.didispace.web;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
//import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didispace.domain.ReturnResult;
import com.didispace.domain.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;

//import acync.ConcreateWapper.Concreate;
@RestController
public class ComputeControllers implements RabbitTemplate.ConfirmCallback {

	public static final String EXCHANGE = "spring-boot-exchanger";
	public static final String ROUTINGKEY = "spring-boot-routingKey";// event
	public static final String ROUTINGKEY1 = "ms-send";// call service
	public static final String ROUTINGKEY2 = "ms-get";// each bapp
																	// get
																	// result
	//@Autowired
	private RabbitTemplate rabbitTemplate;
	private final Logger logger = Logger.getLogger(getClass());
	// @Autowired
	private RestTemplate restTemplate = new RestTemplate();
	@Autowired
	private DiscoveryClient client;

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("127.0.0.1:5672");
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");
		connectionFactory.setVirtualHost("test");
		connectionFactory.setPublisherConfirms(true); // 必须要设置
		return connectionFactory;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	// 必须是prototype类型
	public RabbitTemplate rabbitTemplate1() {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		return template;
	}

	private Integer gsida = 0;
	private Integer gsidc = 0;
	private String sresultc = null;
	private String sresulta = null;

	public Integer getGsida() {
		return gsida;
	}

	public void setGsida(Integer gsida) {
		this.gsida = gsida;
	}

	public Integer getGsidc() {
		return gsidc;
	}

	public void setGsidc(Integer gsidc) {
		this.gsidc = gsidc;
	}

	public String getSresultc() {
		return sresultc;
	}

	public void setSresultc(String sresultc) {
		this.sresultc = sresultc;
	}

	public String getSresulta() {
		return sresulta;
	}

	public void setSresulta(String sresulta) {
		this.sresulta = sresulta;
	}

	// get service a c result
	@RabbitListener(queues = "q-get")
	public void receiveResult(String foo) {
		if (null != foo && !foo.isEmpty()) {
			logger.info(getGsidc() + " @=c  --a= " + getGsida() + "   B  --receiveResult  Foo<" + foo);
			JSONObject obj = JSONObject.parseObject(foo);
			String res = obj.getString("res");
			Integer sid = obj.getInteger("sid");

			Integer a = getGsida();
			Integer c = getGsidc();
			logger.info(sid + " @  listebn " + res + " " + a + " a  ---c  " + c);
			if (a.equals(sid)) {
				if(!res.equals("{}"))
				setSresulta(res);
				logger.info(getSresulta() + " @  *** a");

			} else if (c.equals(sid)) {
				if(!res.equals("{}"))
				setSresultc(res);
				logger.info(getSresultc() + " @ ^^^^ c");

			}
		}
	}

	/**
	 * 回调
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		System.out.println(" 回调id:" + correlationData.getId());
		if (ack) {
			// servicea
			// if (correlationData.getId().equals(getsida())) {
			logger.info("消息成功消费");
			// }
		} else {
			logger.info("消息消费失败:" + cause);
		}
	}

	@RequestMapping(value = "/add3/{a}", method = RequestMethod.GET)
	public String add3(@PathVariable String a, HttpServletRequest request) {
		// restTemplate = new RestTemplate();

		List<User> bl = new ArrayList<User>();
		User u = new User();
		u.setName("^^^Johnathan M Smith");
		bl.add(u);
		String bls = JSONObject.toJSONString(bl);
		System.out.println(bls);
		Map<String, String> vars = new HashMap<String, String>();
		vars.put("ids", "INID");

		HttpHeaders headers = new HttpHeaders();
		MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
		headers.setContentType(type);
		headers.add("Accept", MediaType.APPLICATION_JSON.toString());

		// JSONObject jsonObj = JSONObject.fromObject( pj);
		HttpEntity<String> formEntity = new HttpEntity<String>(bls, headers);

		User returns = restTemplate.postForObject("http://localhost:2221/add2/{id}", formEntity, User.class);

		rabbitTemplate.setConfirmCallback(this);
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
		rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY, "", correlationId);

		// User returns =
		// restTemplate.postForObject("http://localhost:2221/add2/{a}", u,
		// User.class, vars);
		String uri = request.getRequestURL().toString() + "?" + request.getQueryString();

		List<ServiceInstance> list = client.getInstances("rabbitmq-hello");
		if (list != null && list.size() > 0) {
			System.out.println((list.get(0)).getUri());
		}
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println(uri + "    b3@#/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId()
				+ ", result:");

		return "win";
	}

	@RequestMapping(value = "/add2/{a}", method = RequestMethod.POST)
	public String add2(@PathVariable String a, @RequestBody User bl, HttpServletRequest request) {
		String uri = request.getRequestURL().toString() + "?" + request.getQueryString();
		// restTemplate = new RestTemplate();
		// restTemplate.getForEntity("http://localhost:2222/send?a=" + a,
		// String.class).getBody();
		List<ServiceInstance> list = client.getInstances("rabbitmq-hello");
		if (list != null && list.size() > 0) {
			System.out.println((list.get(0)).getUri());
		}
		ServiceInstance instance = client.getLocalServiceInstance();
		System.out.println(uri + "    b3@#/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId()
				+ ", result:");

		return "win";
	}

	// bapp sample

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/add/{a}", method = RequestMethod.GET)
	public String add(@PathVariable String a, HttpServletRequest request) {

		try {
			rabbitTemplate = rabbitTemplate1();
			rabbitTemplate.setConfirmCallback(this);
			// only once call
			Integer serviceNumber = 2;// bapp call service number

			ServiceInstance instance = client.getLocalServiceInstance();
			// bapp start
			Map<String, String> varsb = new HashMap<String, String>();
			varsb.put("a", a);
			String pathParematerb = JSON.toJSONString(varsb);
			Integer bid = UUID.randomUUID().hashCode();
			bid = bid > 0 ? bid : -bid;
			String bapp = instance.getServiceId();
			String urib = "http://" + instance.getHost() + ":" + instance.getPort() + "/add/" + a;
			Integer methodb = 1;
			String edateb = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
			Integer etypeb = 1; // bapp etype = 1 BAPP start, 2ok 3:need
								// evh,7fail; service
			TJhelper thelp = new TJhelper(); // // :4ok ,5fail
			String eresponseb = JSONObject.toJSONString(String.class);
			logger.info("bapp start");

			thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematerb, bid, 0, eresponseb, urib, bapp, null, etypeb,
					methodb, null, edateb, 1);

			// service-A

			String edatea = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
			String uria = "http://localhost:2220/add1/{a}?pevh={pevh}";
			String eresponse = JSONObject.toJSONString(ReturnResult.class);
			String servicea = "service-A";
			Integer sida = UUID.randomUUID().hashCode();			
			sida = sida > 0 ? sida : -sida;
			gsida=sida;
			Map<String, String> varsa = new HashMap<String, String>();
			varsa.put("a", a);
			String pathParematera = JSON.toJSONString(varsa);
			String pevha = thelp.createParameter(serviceNumber, 0, pathParematera, bid, sida, eresponse, uria, bapp,
					servicea, 5, 1, null, edatea);

			varsa.put("pevh", pevha);
			// method 1 GET:2 POST
			Integer methoda = 1;
			Integer etypea = 4;
			logger.info("service-a start" + pevha);
//			rabbitTemplate.setConfirmCallback(this);
//			CorrelationData correlationId = new CorrelationData(sida.toString());
//			rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY1, pevha, correlationId);
			  pathParematera = JSON.toJSONString(varsa);

			 thelp.send( rabbitTemplate,restTemplate, serviceNumber, 0, pathParematera, bid, sida, eresponse, uria, bapp, servicea, etypea,			 methoda, null, edatea, 2);

			// restTemplate = new RestTemplate();
			// ReturnResult ry = restTemplate
			// .getForEntity("http://localhost:2221/add1/{a}?pevh={pevh}",
			// ReturnResult.class, vars).getBody();
			logger.info("@@  service-C start");
			// service-C.....
			String servicec = "service-C";
			String pathParematerc;
			Integer sidc = UUID.randomUUID().hashCode();
			sidc = sidc > 0 ? sidc : -sidc;
			gsidc=sidc;
			List<User> bl = new ArrayList<User>();
			Integer methodc = 2;// post 4p
			User b = new User();
			b.setName("3^^^Johnathan M Smith");
			b.setAge(56);
			b.setId(66l);
			Integer etypec = 5;
			String edatec = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
			String uric = "http://localhost:2220/add2/{ids}";
			String parematerc = JSON.toJSONString(b, SerializerFeature.WriteClassName);
			Map<String, String> varsc = new HashMap<String, String>();
			String ids = "3INID]" + bid + "]" + sidc + "]" + serviceNumber + "]" + servicec + "]" + bapp;// +
			varsc.put("ids", ids);
			pathParematerc = JSON.toJSONString(varsc);
			/*String pevhc = thelp.createParameter(serviceNumber, 0, pathParematerc, bid, sidc, eresponse, uric, bapp,
					servicec, 5, 2, null, edatec);*/
			 

//			correlationId = new CorrelationData(sidc.toString());
//			rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY1, pevhc, correlationId);

			 thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematerc, bid,			 sidc, eresponse, uric, bapp, servicec,			 etypec, methodc, parematerc, edatec, 2);

			String mkeyc = bid + sidc.toString() + "Result";
			String mkeya = bid + sida.toString() + "Result";
			logger.info("所有模块都start");

			// wait sa sc result
			boolean loop1 = true;
			int times1 = 0;
			// wait sa sc end
			String xa = getSresulta();
			String xc = getSresultc();
			while ((xa == null || xc == null) && loop1) {				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (times1++ == 100) {
					loop1 = false;// exit while
				}

			}
			
			String resa = getSresulta();
			logger.info(resa + " =resa 所有模块都完成，任务完成" + loop1);
			String resultsa = JSONObject.toJSONString("fail");
			if (null != resa && !resa.isEmpty()&&!resa.equals( "{}")) {
				JSONObject eresponsea = JSONObject.parseObject(resa);
				String resoa = eresponsea.getString("sresult");// .getJSONObject("sresult");
				  resultsa = JSONObject.toJSONString(resoa);
				etypea = eresponsea.getInteger("statusCode");
				logger.info(etypea + " =etypea  ! 所有模块都完成，任务完成" + loop1);

				thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematera, bid, sida, resultsa, uria, bapp, servicea,	etypea, methoda, null, edatea, 1);
			}else{
				//service_a fail
				thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematera, bid, sida, resultsa, uria, bapp, servicea,	5, methoda, null, edatea, 1);
	
			}
			String resc = getSresultc();
			logger.info(resc + " =resc 所有模块都完成，任务完成");
			String resultsc = JSONObject.toJSONString("fail");

			if (null != resc && !resc.isEmpty()&&!resc.equals( "{}")) {
				JSONObject eresponsec = JSONObject.parseObject(resc);
				String resoc = eresponsec.getString("sresult");// .getJSONObject("sresult");
				  resultsc = JSONObject.toJSONString(resoc);
				etypec = eresponsec.getInteger("statusCode");
//				thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematerc, bid, sidc, resultsc, uric, bapp, servicec,
//						etypec, methodc, null, edatec, 1);

				if (resc != null) {

					// if (etypec == 4) {
					// LinkedHashMap us = (LinkedHashMap) resoc.getSresult();
					// Set ukey = us.keySet();
					// for (Object uk : ukey) {
					// // get user
					// System.out.println("@@3333bappp--se c : " + " vars=" +
					// us.get(uk));
					//
					// }
					//
					// }
				}

				// SEND Message to TJE
				edatec = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");

				thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematerc, bid, sidc, resultsc, uric, bapp, servicec,	etypec, methodc, parematerc, edatec, 1);
			}else{
				//service_c fail
				thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematerc, bid, sidc, resultsc, uric, bapp, servicec,	5, methodc, parematerc, edatec, 1);
	
			}
			// bapp end ,judge bapp ,etype=2:ok ,etype=3:fail
			Integer rs = 2;// BAPP fail
			//etypea=4;//del
			etypeb = 6;
			if (etypea == 4 && etypec == 4) {
				etypeb = 2;// BAPP ok
				rs = 1;
			} else if ((etypea == 4 && etypec == 5) || (etypea == 5 && etypec == 4)) {
				etypeb = 3;// BAPP half ok need to do bapp's eventHandle
				rs = 1;
			} else if (etypea == 5 && etypec == 5) {
				etypeb = 6;// BAPP fail
				rs = 2;
			}
			edateb = JSON.toJSONStringWithDateFormat(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
			thelp.send(rabbitTemplate,restTemplate, serviceNumber, 0, pathParematerb, bid, 1, eresponseb, urib, bapp, null, etypeb,	methodb, null, edateb, 1);
			if (rs == 1) {
				return "ok";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}

}