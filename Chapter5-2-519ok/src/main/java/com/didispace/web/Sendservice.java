package com.didispace.web;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.didispace.rabbit.AmqpConfig;

@RestController

public class Sendservice implements RabbitTemplate.ConfirmCallback {
	@Autowired
	private EventProcessMapper userMapper;
	@Autowired
	private RabbitTemplate rabbitTemplate;
 
	private final Logger logger = Logger.getLogger(getClass());

	/**
	 * 构造方法注入
	 */
	/*
	 * @Autowired public Sendservice(RabbitTemplate rabbitTemplate) {
	 * this.rabbitTemplate = rabbitTemplate;
	 * rabbitTemplate.setConfirmCallback(this); //
	 * rabbitTemplate如果为单例的话，那回调就是最后设置的内容 }
	 */

	public void sendMsg(String content) {
		logger.info(content );
		rabbitTemplate.setConfirmCallback(this);
		CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());

		JSONObject obj = JSONObject.parseObject(content);
		Integer mtype = obj.getInteger("mtype");
		if (mtype ==2) {
			logger.info("2" );
			rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY1, content, correlationId);

		} else if (mtype ==1){ 
			logger.info("1" );
			rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY, content, correlationId);

		} else if (mtype ==3){
			logger.info("3s" );
			rabbitTemplate.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTINGKEY2, content, correlationId);

		} 
	}

	/**
	 * 回调
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		System.out.println(" 回调id:" + correlationData);
		if (ack) {
			logger.info("消息成功消费");
		} else {
			logger.info("消息消费失败:" + cause);
		}
	}

	@RequestMapping(value = "/send1", method = RequestMethod.GET)
	//@Async
	public String add1(@RequestParam Integer etype) {
		int a=1;
		a++;
		return "ok";
	}

	@RequestMapping(value = "/send", method = RequestMethod.POST)
	@Async
	public String add(@RequestBody String a) {
		//sendMsg(a);
		return "ok";
	}

}