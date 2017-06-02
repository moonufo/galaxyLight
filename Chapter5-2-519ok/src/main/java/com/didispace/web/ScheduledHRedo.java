package com.didispace.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didispace.mapper.HredoService;

//@Component
@RestController
public class ScheduledHRedo {

	private static final Logger logger = LoggerFactory.getLogger(ScheduledHRedo.class);
	@Autowired
	private EventProcessMapper eventMapper;
	private RestTemplate restTemplate = new RestTemplate();// ?
	@Autowired
	HredoService hredoService;
	private SimpleDateFormat hm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Scheduled(cron = "0 2/2 0-23 * * ?") // 2
	public void executeUploadBackTask() {
		try {
			logger.info("!定时任务3--:");
			long now = System.currentTimeMillis();
			String edaten = hm.format(new Date(now));
			String edates = hm.format(new Date(now - 120000));// 120000
			// 运行过的业务
			List<EventProcess> bevl = eventMapper.findEvntThree(edates, edaten);
			logger.info("bevs3 ");

			List<Integer> bevt = new ArrayList<Integer>();
			Integer rsf = 0;
			// 间隔2分钟,处理没有结束的bapp
			for (EventProcess eventProcess : bevl) {
				// 没有完成的业务 2,6
				EventProcess bevs = eventMapper.findEvntSecond(eventProcess.getBid());
				if (bevs == null) {
					// judge if have 3
					EventProcess bevs3 = eventMapper.findEvntFour(eventProcess.getBid());
					logger.info("bevs3 ");
					if (bevs3 != null) {
						// judge if in hredo
						Hredo hrdoh = eventMapper.findEvntFive(bevs3.getBid());
						if (hrdoh == null)
							bevt.add(bevs3.getBid());
						logger.info(bevs3.getBid().toString());
					} else {
						bevt.add(eventProcess.getBid());
					}

				}
			}
			for (Integer bid : bevt) {
				doeventH(bid);
				logger.info("to  doeventH(bid)" + bid);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("ScheduledTest.executeUploadBackTask 定时任务3:end");
	}

	// search id
	// search tasknaME

	@SuppressWarnings("rawtypes") // etype=1OK,2未处理
	@RequestMapping(value = "/rhdo/done/{pageNo}/{pageSize}/{etype}", method = RequestMethod.GET)
	public String pgst(@PathVariable Integer pageNo, @PathVariable Integer pageSize, @PathVariable Integer etype,
			@RequestParam String edates, @RequestParam String edaten, @RequestParam Integer taskID,
			@RequestParam String taskName) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> rest = new HashMap<String, Object>();

		try {
			List<EventProcess> els = new ArrayList<EventProcess>();
			// List<String> pgl = new ArrayList<String>();
			PageInfo<EventProcess> pg = new PageInfo<EventProcess>();
			pg.setPageNo(pageNo);
			pg.setPageSize(pageSize);
			Map<String, Object> emap = new HashMap<String, Object>();
			emap.put("pageNo", pageNo);
			emap.put("pageSize", pageSize);
			emap.put("etype", etype);
			emap.put("edates", edates);
			emap.put("edaten", edaten);
			emap.put("taskID", taskID);
			emap.put("taskName", taskName);

			Integer pgs = hredoService.getEsize(emap);
			pg.setPageTotal(pgs);
			List<EventProcess> hrdol = hredoService.getAll(emap);
			pg.setList(hrdol);
			map.put("todoall", pg);
			rest.put("status", "success");
		} catch (Exception e) {
			rest.put("status", "fail");
			e.printStackTrace();
		}
		map.put("rhdo", rest);
		String ejson = JSON.toJSONString(map);
		logger.info(ejson);
		return ejson;
	}

	// 得到未处理的所有事物 /{pageNo}/{pageSize}
	@RequestMapping(value = "/rhdo/todoall/{pageNo}/{pageSize}", method = RequestMethod.GET)
	public List<EventProcess> findDone() {
		List<EventProcess> els = new ArrayList<EventProcess>();
		Integer pageNo = 0, pageSize = 10;
		List<Hredo> hrdol = eventMapper.findhredoAll(pageNo, pageSize);
		for (Hredo hredo : hrdol) {
			EventProcess evp = eventMapper.findEvntSecond(hredo.getBid());
			els.add(evp);
		}

		// map.addAttribute("els", el);
		return els;
	}

	// 处理事务
	@RequestMapping(value = "/rhdo/todoall/", method = RequestMethod.GET)
	public Integer getp(@PathVariable Integer bid) {
		Integer rflag = 10;

		return rflag;

	}

	// 处理事务
	@RequestMapping(value = "/rhdo/{bid}", method = RequestMethod.GET)
	public String hrdo(@PathVariable Integer bid ) {
		Integer rflag = 0;
		logger.info("do 0");
		rflag = doeventH(bid);
		if (rflag == 1) {
			logger.info("do ok");
			 
				// set 1
				eventMapper.updateokhredo(bid);
			 
			return "success";
		}
		return "fail";

	}

	public Integer doeventH(Integer bid) {
		Integer rflag = 0;
		try {
			SimpleDateFormat hm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String edate = "";
			logger.info("!doeventH");

			// 找到bapp的所有SERVICE
			EventProcess ep = eventMapper.findEpById(bid);
			String uri = ep.getUri();
			logger.info(uri);

			// call bappBEH //
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
			Map<String, String> vart = new HashMap<String, String>();
			vart.put("bid", bid + "");
			if (ep.getMethod() == 2) {// post
				RestTemplate restTemplate = new RestTemplate();
				HttpHeaders headers = new HttpHeaders();
				MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
				headers.setContentType(type);
				headers.add("Accept", MediaType.APPLICATION_JSON.toString());
				HttpEntity<String> formEntity = new HttpEntity<String>(ep.getParemater(), headers);
				String result = restTemplate.postForObject(uri, formEntity, String.class, vart);
				if (result == null) {
					result = restTemplate.postForObject(uri, formEntity, String.class, vart);
				}
				if (result != null&&!result.equals("fail"))
					rflag = 1;

			} else {// get
				String ry = restTemplate.getForEntity(uri, String.class, vart).getBody();
				if (ry == null) {
					ry = restTemplate.getForEntity(uri, String.class, vart).getBody();
				}
				if (ry != null&&!ry.equals("fail"))
					rflag = 1;
			}
			logger.info("www  "+ep.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return rflag;
	}
}
