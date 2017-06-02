package com.didispace.web;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ComputeControllerComaP {

	private final Logger logger = Logger.getLogger(getClass());
	// compansation
	@Autowired
	private DiscoveryClient client;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Transactional
	@RequestMapping(value = "/add2_comp/{ids}", method = RequestMethod.POST)
	public String add2(@PathVariable String ids, @RequestBody User bl) {
		logger.info("add2_comp      ok");

		String res ="j";
		res = ids;
		// if error return null
 		return res;
	}
	@Transactional
	@RequestMapping(value = "/add1_comp/{a}", method = RequestMethod.GET)
	public String add(@PathVariable String a, @RequestParam String pevh) {
		// ServiceInstance instance = client.getLocalServiceInstance();
		String res = null, bsn = "";
		Integer etype = 6;
		// do Service if ok etype=4 else 5 if fatal error 6
		// insert id redis
		TJshelper tjshelper = new TJshelper();
		logger.info("add1_comp{a}   ok");

		if (!pevh.isEmpty())
			bsn = tjshelper.getBsn(pevh) + "add";

		stringRedisTemplate.opsForValue().get(bsn + "Insert");
 
		// delete updtae id
		stringRedisTemplate.opsForValue().get(bsn + "delete" );
 
		// update   data
		stringRedisTemplate.opsForValue().get(bsn + "update" );
 //save db  log
		 	res = "" + etype;
		// if error return null
 		return res;
	}

}