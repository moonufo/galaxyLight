package com.didispace.rabbit;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
//import org.springframework.amqp.core.Queue;  
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.AsyncRestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.didispace.web.EventProcessMapper;
import com.didispace.web.ScheduledHRedo;
import com.rabbitmq.client.Channel;

@Configuration
public class AmqpConfig {
	private static final Logger logger = LoggerFactory.getLogger(AmqpConfig.class);

	public static final String EXCHANGE = "spring-boot-exchanger";
	public static final String ROUTINGKEY = "spring-boot-routingKey";
	public static final String ROUTINGKEY1 = "ms-send";
	public static final String ROUTINGKEY2 = "ms-get";

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

	@Autowired
	private EventProcessMapper eventMapper;
	private AsyncRestTemplate asyrestTemplate = new AsyncRestTemplate();// ?

	/**
	 * 针对消费者配置 1. 设置交换机类型 2. 将队列绑定到交换机
	 * 
	 * 
	 * FanoutExchange: 将消息分发到所有的绑定队列，无routingkey的概念 HeadersExchange
	 * ：通过添加属性key-value匹配 DirectExchange:按照routingkey分发到指定队列
	 * TopicExchange:多关键字匹配
	 */
	@Bean
	public DirectExchange defaultExchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public Queue queue0() {// event
		return new Queue("spring-boot-queue", true); // 队列持久spring-boot-queue

	}

	@Bean
	public Queue queue1() {// call service
		return new Queue("q-send", true); // 队列持久spring-boot-queue

	}

	@Bean
	public Queue queue2() {// result
		return new Queue("q-get", true); // 队列持久spring-boot-queue

	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue0()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY);
	}

	@Bean
	public Binding binding1() {
		return BindingBuilder.bind(queue1()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY1);
	}

	@Bean
	public Binding binding2() {
		return BindingBuilder.bind(queue2()).to(defaultExchange()).with(AmqpConfig.ROUTINGKEY2);
	}

	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(5);
	ThreadPoolExecutor executor = new ThreadPoolExecutor(3, Runtime.getRuntime().availableProcessors(), 1,
			TimeUnit.MINUTES, queue, new ThreadPoolExecutor.CallerRunsPolicy());

	@Transactional
	public void process(String hellos) {

		Runnable run = new Runnable() {
			@Override
			public void run() {
				try {
					logger.info("   rt process<" + hellos);
					JSONObject obj = JSONObject.parseObject(   hellos  );
					

					String bapp = obj.getString("bapp");
					Integer etype = obj.getInteger("etype");
					Integer serviceNumber = obj.getInteger("serviceNumber");
					Integer method = obj.getInteger("method");
					String pathParemater = obj.getString("pathParemater");
					String edate = obj.getString("edate");
					JSONObject up2 = obj.getJSONObject("paremater");
					String up3 = null;
					if (up2 != null)
						up3 = up2.toJSONString();
					String uri = obj.getString("uri");
					String service = obj.getString("service");
					Integer bid = obj.getInteger("bid");
					Integer sid = obj.getInteger("sid");
					// 去重msg sid
					String mkey = bid + sid.toString() + "Msg1";
					if (!stringRedisTemplate.hasKey(mkey)) {
						String eresponse = obj.getString("eresponse");
						String pap = obj.getString("pathParemater");
						Map<String, String> vars = JSON.parseObject(pap, new TypeReference<Map<String, String>>() {
						});
						vars.put("bid", bid + "");
						stringRedisTemplate.opsForValue().set(mkey, mkey);
						stringRedisTemplate.expire(mkey, 120, TimeUnit.MINUTES);
						eventMapper.insert(serviceNumber, 0, pathParemater, eresponse, uri, method, bapp, bid, sid,			service, etype, up3, edate);
						if (etype == 3) { 
							logger.info("mq 3");
							// call bappBEH get
							String func = uri.substring(uri.lastIndexOf(":"), uri.length());
							int a = func.indexOf("/");
							int b2 = func.indexOf("/", a + 1);
							if (b2 > 0)
								func = func.substring(a + 1, b2);
							else
								func = func.substring(a + 1);
							StringBuffer urie = new StringBuffer();
							urie.append(uri.substring(0, uri.indexOf(func))).append(func).append("_EventH")
									.append(uri.substring(uri.indexOf(func) + func.length())).append("/{bid}");
							uri = urie.toString();
							if (method == 2) {// post
								HttpHeaders headers = new HttpHeaders();
								MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
								headers.setContentType(type);
								headers.add("Accept", MediaType.APPLICATION_JSON.toString());
								headers.setContentType(type);
								HttpEntity<String> formEntity = new HttpEntity<String>(up3, headers);
								asyrestTemplate.postForEntity(uri, formEntity, String.class, vars);
							} else {// get
								eresponse = obj.getString("eresponse");
								asyrestTemplate.getForEntity(uri, String.class, vars);

							}

						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		executor.execute(run);

	}

	public void process1(String hellos) {

		Runnable run = new Runnable() {
			@Override
			public void run() {
				try {
					logger.info(" sa 2 process1 call  s "+hellos);
					//String xhellos="{" + hellos + "}";
					JSONObject obj = JSONObject.parseObject( hellos);
					// Integer mtype = obj.getInteger("mtype");
					// if (mtype == 2) {// call service
					String bapp = obj.getString("bapp");
					Integer etype = obj.getInteger("etype");
					Integer serviceNumber = obj.getInteger("serviceNumber");
					Integer method = obj.getInteger("method");
					String pathParemater = obj.getString("pathParemater");
					String edate = obj.getString("edate");
					JSONObject up2 = obj.getJSONObject("paremater");
					String up3 = null;
					if (up2 != null)
						up3 = up2.toJSONString();
					String uri = obj.getString("uri");
					Integer bid = obj.getInteger("bid");
					Integer sid = obj.getInteger("sid");
					String pap = obj.getString("pathParemater");
					Map<String, String> vars = JSON.parseObject(pap, new TypeReference<Map<String, String>>() {
					});

					// 去重msg sid
					String mkey = bid + sid.toString() + "Msg";
					if (!stringRedisTemplate.hasKey(mkey)) {

						stringRedisTemplate.opsForValue().set(mkey, mkey);
						stringRedisTemplate.expire(mkey, 120, TimeUnit.MINUTES);
						String eresponse = obj.getString("eresponse");
						String service = obj.getString("service");

						stringRedisTemplate.opsForValue().set(mkey, mkey);
						stringRedisTemplate.expire(mkey, 120, TimeUnit.MINUTES);
//						eventMapper.insert(serviceNumber, 0, pathParemater, eresponse, uri, method, bapp, bid, sid,
//								service, etype, up3, edate);
 						logger.info("mq prcess1 " + sid);
						// call service

						if (method == 2) {// post
							HttpHeaders headers = new HttpHeaders();
							MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
							headers.setContentType(type);
							headers.add("Accept", MediaType.APPLICATION_JSON.toString());
							headers.setContentType(type);
							HttpEntity<String> formEntity = new HttpEntity<String>(up3, headers);
							asyrestTemplate.postForEntity(uri, formEntity, String.class, vars);
						} else {// get
							asyrestTemplate.getForEntity(uri, String.class, vars);

						}

					}
					// }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		executor.execute(run);

	}

	@Bean
	public SimpleMessageListenerContainer messageContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(queue0());
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		container.setMessageListener(new ChannelAwareMessageListener() {

			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				String msgd = new String(body);
				process(msgd);
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息成功消费
			}
		});
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer messageContainer2() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(queue1());

		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 设置确认模式手工确认
		container.setMessageListener(new ChannelAwareMessageListener() {

			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				String msgd = new String(body);
				logger.info("   rt Rlisten<" + msgd);
				process1(msgd);
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false); // 确认消息成功消费
			}
		});
		return container;
	}
	// @RabbitListener(queues = "q-send")
	// public void receiveMessage3(String foo) {
	// logger.info("Received Foo<" + foo );
	// process1(foo);
	// }

}