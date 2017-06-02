package com.didispace.web;

import org.springframework.data.redis.core.StringRedisTemplate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didispace.domain.ReturnResult;

@RestController
public class ComputeControllera implements RabbitTemplate.ConfirmCallback {
	public static final String EXCHANGE = "spring-boot-exchanger";
	public static final String ROUTINGKEY = "spring-boot-routingKey";// event
	public static final String ROUTINGKEY1 = "ms-send";// call service
	public static final String ROUTINGKEY2 = "ms-get";// each bapp get result
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
		connectionFactory.setAddresses("127.0.0.1:5672");
		connectionFactory.setUsername("admin");
		connectionFactory.setPassword("admin");
		connectionFactory.setVirtualHost("test");
		connectionFactory.setConnectionTimeout(5000);
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

	private final Logger logger = Logger.getLogger(getClass());
	@Autowired
	private EventProcessMapper eventMapper;

	private RestTemplate restTemplate = new RestTemplate();

	/**
	 * 回调
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		logger.info(" 回调id:" + correlationData.getId());
		if (ack) {
			logger.info("消息消费ok:");
		} else {
			logger.info("消息消费失败:" + cause);
		}
	}

	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	//@Transactional
	@Transactional(propagation=Propagation.REQUIRED)
	@RequestMapping(value = "/add2/{ids}", method = RequestMethod.POST)
	public ReturnResult add2(HttpServletRequest request, @PathVariable String ids, @RequestBody User bl) {
		rabbitTemplate = rabbitTemplate1();
		rabbitTemplate.setConfirmCallback(this);
		ReturnResult res = new ReturnResult();
		String bsn = "";
		Integer etype = 5;
		TJshelper tjshelper = new TJshelper();
		if (!ids.isEmpty()) {
			bsn = tjshelper.getpBsn(ids) + "add2";
			logger.info(ids + " sa 2 post!!/add2 " + bsn);

		}
		String result = "";
		ids += "]/add2/{ids}";
		if (!stringRedisTemplate.hasKey(bsn)) {
			// ServiceInstance instance = client.getLocalServiceInstance();
			User user = new User();
			String[] sp = ids.split("]");

			user.setName(sp[0]);
			res.setSresult(user);
			res.setStatusCode(etype);
			result = JSONObject.toJSONString(user);

			// if error res.setStatusCode(6);
			// if need eventh res.setStatusCode(5);
			boolean ret = true;
			if (ret) {
				stringRedisTemplate.opsForValue().set(bsn, bsn);
				stringRedisTemplate.expire(bsn, 45, TimeUnit.MINUTES);
				// res.setSresult("res");

			}
			sp = ids.split("]");
			logger.info(ids + " @   sa post!!/add2 " + sp[1]);
			Map<String, String> vars = new HashMap<String, String>();
			vars.put("ids", sp[0]);
			String pathParemater = JSON.toJSONString(vars);
			String paremater = JSON.toJSONString(bl);
			Integer etypec = 5;// operation ok 4,half ok 5,fail 6
			Integer method = 2;// post ,1:get
			bsn = tjshelper.savePostlog(result, paremater, method, pathParemater, ids, etypec, request, eventMapper);
			Integer sid = Integer.parseInt(sp[2]);
			String eresponse = "";// JSONObject.toJSONString(res);

			// bsn = tjshelper.send(restTemplate, null, null, null, null, sid,
			// eresponse, null, null, null, null, null,
			// null, null, 3);
			Map<String, Object> varsc = new HashMap<String, Object>();
			varsc.put("res", res);
			varsc.put("sid", sid);
			eresponse = JSONObject.toJSONString(varsc);

			CorrelationData correlationId = new CorrelationData(sid.toString());
			rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY2, eresponse, correlationId);

		}
		return res;
	}

	@Transactional
	@RequestMapping(value = "/add1/{a}", method = RequestMethod.GET)
	public ReturnResult add(@PathVariable String a, @RequestParam String pevh) {
		rabbitTemplate = rabbitTemplate1();
		rabbitTemplate.setConfirmCallback(this);
		ReturnResult res = new ReturnResult();
		Integer etype = 4;
		Integer sid = 0;
		// if 参数错误 return 6
		logger.info("a  /add1/{a}");

		String bsn = "";
		TJshelper tjshelper = new TJshelper();
		if (!pevh.isEmpty()) {
			bsn = tjshelper.getBsn(pevh) + "add";
			sid = tjshelper.getSid(pevh);
		}
		String result = "";
		// do only once ok 冥等
		if (!stringRedisTemplate.hasKey(bsn)) {
			// insert id redis
			stringRedisTemplate.opsForValue().set(bsn + "Insert", "a");
			stringRedisTemplate.expire(bsn, 8, TimeUnit.HOURS);

			// delete updtae id
			stringRedisTemplate.opsForValue().set(bsn + "delete", "1");
			stringRedisTemplate.expire(bsn, 8, TimeUnit.HOURS);

			// update data json
			stringRedisTemplate.opsForValue().set(bsn + "updateOne", "2");
			stringRedisTemplate.expire(bsn, 8, TimeUnit.HOURS);

			List<String> al = new ArrayList<String>();
			al.add("1w");
			al.add("2dedw");
			al.add("3rftfgw");
			result = JSONObject.toJSONString(al);

			// do Service if ok etype=4 else 5
			boolean ret = true;// 4
			if (ret) {
				stringRedisTemplate.opsForValue().set(bsn, bsn);
				stringRedisTemplate.expire(bsn, 5, TimeUnit.MINUTES);
				res.setSresult(al);

			}
			res.setStatusCode(etype);
		}
		Map<String, Object> varsc = new HashMap<String, Object>();
		varsc.put("res", res);
		varsc.put("sid", sid);
		String eresponse = JSONObject.toJSONString(varsc);
		// if (!pevh.isEmpty())
		bsn = tjshelper.saveLog(eventMapper, result, pevh, etype);

		// bsn = tjshelper.send(restTemplate, null, null, null, null, sid,
		// eresponse, null, null, null, null, null, null, null, 3);

		logger.info("!a  /add1/{a}   ok");
		// send eresponse
		// rabbitTemplate.setConfirmCallback(this);
		// CorrelationData correlationId = new
		// CorrelationData(tjshelper.getSid(pevh).toString());
		// rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY2, eresponse,
		// correlationId);
		// bsn = tjshelper.send(restTemplate, null, null, null, null, sid,
		// eresponse, null, null, null, null, null, null,
		// null, 3);

		CorrelationData correlationId = new CorrelationData(sid.toString());
		rabbitTemplate.convertAndSend(EXCHANGE, ROUTINGKEY2, eresponse, correlationId);

		return res;
	}

}