package com.didispace.web;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
//import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
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
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.didispace.domain.EventProcess;
import com.didispace.domain.EventProcessMapper;
import com.didispace.domain.Hredo;
import com.didispace.domain.User;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.util.concurrent.ListenableFuture;
import com.didispace.domain.ReturnResult;

//import acync.ConcreateWapper.Concreate;
@RestController
@Transactional
public class ComputeControllersBEH {
	@Autowired
	private EventProcessMapper eventMapper;
	private final Logger logger = Logger.getLogger(getClass());
	// @Autowired
	private AsyncRestTemplate asyrestTemplate = new AsyncRestTemplate();// ?
	// @Autowired
	private RestTemplate restTemplate = new RestTemplate();// ?
	@Autowired
	private DiscoveryClient client;

	@RequestMapping(value = "/add2_EventH/{a}/{bid}", method = RequestMethod.POST)
	public String add2(@PathVariable String a, @PathVariable String bid, @RequestBody User bl,
			HttpServletRequest request) {
		String uri = request.getRequestURL().toString() + "?" + request.getQueryString();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		return "win";
	}

	public Boolean fcontain(List<EventProcess> slist, EventProcess evlog) {
		Boolean rf = true;
		for (EventProcess eventProcess : slist) {
			if (evlog.getSid().equals(eventProcess.getSid())) {
				rf = false;
			}
		}
		// not equal
		return rf;
	}

	@RequestMapping(value = "/add_EventH/{a}/{bid}", method = RequestMethod.GET)
	public String add(@PathVariable String a, @PathVariable String bid, HttpServletRequest request) {
		logger.info("add_EventH/");

		Integer rflag = 0;
		// try {
		// AsyncRestTemplate asyrestTemplate =new AsyncRestTemplate();
		AsyncOperations asnEvnt = new AsyncOperations(asyrestTemplate);
		CountDownLatch latch = null;
		SimpleDateFormat hm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String edate = "";
		// 找到bapp的所有SERVICE
		List<EventProcess> slist = eventMapper.findAll(Integer.parseInt(bid));
		Integer sn = slist.get(0).getServiceNumber();
		if (slist != null && slist.size() < sn) {
			// selcet sevice form log
			List<EventProcess> evlogl = eventMapper.findEvntLog(Integer.parseInt(bid));
			for (EventProcess evlog : evlogl) {
				if (fcontain(slist, evlog)) {
					eventMapper.insert(evlog.getServiceNumber(), evlog.getEtypec(), evlog.getPathParemater(),
							evlog.getEresponse(), evlog.getUri(), evlog.getMethod(), evlog.getBapp(), evlog.getBid(),
							evlog.getSid(), evlog.getService(), evlog.getEtype(), evlog.getParemater(),
							evlog.getEdate());
				}
			}
			slist = eventMapper.findAll(Integer.parseInt(bid));
		}

		Integer sa = 0, sc = 0, sae = 0, sce = 0;// judge service A serviceC
		EventProcess servicec = null;
		EventProcess servicea = null;
		EventProcess serviceBapp = eventMapper.findEp0ById(Integer.parseInt(bid));
		for (EventProcess eventProcess : slist) {
			if (eventProcess.getService() != null && eventProcess.getService().equals("service-C")) {
				servicec = eventProcess;
				// 判断sercice是否成功
				sc = eventProcess.getEtype();
				sce = eventProcess.getEtypec();
				if (sc == 4)
					sc = 1;
				else
					sc = 0;
			} else if (eventProcess.getService() != null && eventProcess.getService().equals("service-A")) {
				servicea = eventProcess;
				sa = eventProcess.getEtype();
				sae = eventProcess.getEtypec();

				if (sa == 4)
					sa = 1;
				else
					sa = 0;

			}
		}
		logger.info("!add 3  evnt1");
		edate = hm.format(new Date());
		// judge service-C and service-A ,then to retry or compansation
		if (sa == 1) {
			if (sae == 1 || sae == 2) {
				if (sc == 1) {
					if (sce == 1 || sce == 2) {

					} else {// sce 0

					}
				} else {// sc 0
                     
				}
			} else {// sae 0

				if (sc == 1) {
					if (sce == 1 || sce == 2) {

					} else {// sce 0

					}
				} else {// sc 0

					String resq = "";
					latch = new CountDownLatch(2);
					Map<String, Object> resultsa = new HashMap<String, Object>();
					Map<String, Object> resultsc = new HashMap<String, Object>();

					// compansation 1 or retry 2 sa
					asnEvnt.asncdo(resultsa, servicea.getUri(), latch, servicea.getMethod(), servicea.getParemater(),
							"java.lang.String", servicea.getPathParemater());
					// retry sc
					asnEvnt.asncdo(resultsc, servicec.getUri(), latch, servicec.getMethod(), servicec.getParemater(),
							servicec.getEresponse(), servicec.getPathParemater());

					try {
						latch.await();
						logger.info("!所有模块都完成，任务完成");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (resultsc.get("resbody") != null) {// !
						// 成功，更新serviceC的状态OK，,业务成功
						logger.info("!更新serviceC的状态OK任务完成");
						eventMapper.updateComp(2, edate, servicec.getSid());
						eventMapper.update(4, edate, servicec.getSid());
						// 更新bapp的状态OK
						// if no record
						EventProcess ec = eventMapper.findEpById2(servicec.getBid());
						if (ec != null) {
							eventMapper.updatebapp(8, edate, servicec.getBid());
						} else {
							logger.info("!eventMapper.insert(s");

							eventMapper.insert(serviceBapp.getServiceNumber(), 0, serviceBapp.getPathParemater(),
									serviceBapp.getEresponse(), serviceBapp.getUri(), serviceBapp.getMethod(),
									serviceBapp.getBapp(), serviceBapp.getBid(), 1, null, 2, serviceBapp.getParemater(), edate);
						}
						// 判断是否人工，update hredo
						Hredo hredo = eventMapper.findhredoId(servicec.getBid());
						if (hredo != null) {
							eventMapper.updatehrdo(servicec.getBid());
						}
						rflag = 1;

					} else {
						// 不成功，插入表hredo,人工处理
						// update record in eventProcess
						EventProcess ec = eventMapper.findEpById2(servicec.getBid());
						if (ec == null) {
							logger.info("// 不成功，插入表hredo,人工处理");
							 // 不成功，插入表eventprocess
							eventMapper.insert(serviceBapp.getServiceNumber(), 0, serviceBapp.getPathParemater(),
									serviceBapp.getEresponse(), serviceBapp.getUri(), serviceBapp.getMethod(),
									serviceBapp.getBapp(), serviceBapp.getBid(), 1, null, 7, serviceBapp.getParemater(), edate);
						}
						logger.info(" @@@/" + ec.toString());
						eventMapper.updatebapp(7, edate, servicec.getBid());
					
						// 判断是否已经插入
						Hredo hredo = eventMapper.findhredoId(servicec.getBid());
						if (hredo == null) {
							// 不成功，插入表hredo,人工处理"
							eventMapper.insertRdo(servicec.getBid(), 2, edate);
						}
					}

				
				}

			}
		} else {// sa 0

			if (sc == 1) {
				if (sce == 1 || sce == 2) {

				} else {// sce 0

					// retry sa
					// compansation sc
					latch = new CountDownLatch(1);
					Map<String, Object> resultsc = new HashMap<String, Object>();
					// User
					// 看compansation 是否完成
					if (servicec.getEtypec() != 1) {
						asnEvnt.asncdo(resultsc, asnEvnt.getcomUri(servicec.getUri()), latch, servicec.getMethod(),
								servicec.getParemater(), "java.lang.String", servicec.getPathParemater());
						edate = hm.format(new Date());
						try {
							latch.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// ReturnResult rec=(ReturnResult)resultsc.get("resbody") ;
						if (resultsc.get("resbody") != null) {// !
							//  更新serviceC的compansation状态 ,业务成功
							eventMapper.updateComp(1, edate, servicec.getSid());
							// 更新bapp的状态OK
							// if no record
							EventProcess ec = eventMapper.findEpById2(servicec.getBid());
							logger.info("   comp@@@/");

							if (ec != null) {
								logger.info(" @@@/" + ec.toString());
								eventMapper.updatebapp(8, edate, servicec.getBid());
								 
								 
								 
							} else {
								eventMapper.insert(serviceBapp.getServiceNumber(), 0, serviceBapp.getPathParemater(),
										serviceBapp.getEresponse(), serviceBapp.getUri(), serviceBapp.getMethod(),
										serviceBapp.getBapp(), serviceBapp.getBid(), 1, null, 8, serviceBapp.getParemater(),
										edate);
							}
							// 判断是否人工，update hredo
							Hredo hredo = eventMapper.findhredoId(servicec.getBid());
							if (hredo != null) {
								eventMapper.updatehrdo(servicec.getBid());
							}
							rflag = 1;
						} else {
							logger.info("add 3  evnt2");
							// insert record in eventProcess
							EventProcess ec = eventMapper.findEpById2(servicec.getBid());
							if (ec == null) {
								eventMapper.insert(serviceBapp.getServiceNumber(), 0, serviceBapp.getPathParemater(),
										serviceBapp.getEresponse(), serviceBapp.getUri(), serviceBapp.getMethod(),
										serviceBapp.getBapp(), serviceBapp.getBid(), 1, null, 7, serviceBapp.getParemater(),
										edate);
							}
							logger.info(" @@@/" + ec.toString());
							eventMapper.updatebapp(7, edate, servicec.getBid());
							// 不成功，插入表hredo,人工处理
							// 判断是否已经插入hredo
							Hredo hredo = eventMapper.findhredoId(servicec.getBid());
							if (hredo == null) {
								eventMapper.insertRdo(servicec.getBid(), 2, edate.toString());
							}
							// 更新serviceC状态fail
							//eventMapper.updateComp(2, edate, servicec.getSid());
						}
					}
				
				}
			} else {// sc 0

			}

		}
//		 if (sa == 1 && sc == 1) {
//			edate = hm.format(new Date());
//			logger.info("****sa == 1 && sc == 1" + edate);
//			rflag = 1;
//			EventProcess ec = eventMapper.findEpById2(servicec.getBid());
//			if (ec == null) {
//				String service = null;
//				eventMapper.insert(serviceBapp.getServiceNumber(), 0, serviceBapp.getPathParemater(),
//						serviceBapp.getEresponse(), serviceBapp.getUri(), serviceBapp.getMethod(),
//						serviceBapp.getBapp(), serviceBapp.getBid(), 1, service, 2, serviceBapp.getParemater(), edate);
//				logger.info("sa == 1 && sc == 1" + serviceBapp.getServiceNumber());
//
//			}
//		}

		// } catch (Exception e) {
		//
		// }
		if (rflag == 1)
			return "ok";
		else
			return "fail";
	}

}