package com.iot.app.springboot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.iot.app.springboot.dao.POITrafficDataDAO;
import com.iot.app.springboot.dao.TotalTrafficDataDAO;
import com.iot.app.springboot.dao.WindowTrafficDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.iot.app.springboot.model.POITraffic;
import com.iot.app.springboot.model.TotalTraffic;
import com.iot.app.springboot.model.WindowTraffic;
import com.iot.app.springboot.vo.Response;

/**
 * Service class to send traffic data messages to dashboard ui at fixed interval using web-socket.
 * 
 * @author jshah
 *
 */
@Service
@ComponentScan(basePackages = {"com.iot.app.springboot", "com.iot.app.springboot.dao", "com.iot.app.springboot.model"})
public class TrafficDataService {

	private static Logger logger = LoggerFactory.getLogger(TrafficDataService.class);

	@Autowired
	private SimpMessagingTemplate template;
	
	@Autowired
	private TotalTrafficDataDAO totalTrafficDataDAO;
	
	@Autowired
	private WindowTrafficDAO windowTrafficDAO;
	
	@Autowired
	private POITrafficDataDAO poiTrafficDataDAO;
	
	private static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	//Method sends traffic data message in every 5 seconds.
	@Scheduled(fixedRate = 5000)
	public void trigger() {
		List<TotalTraffic> totalTrafficList = new ArrayList<TotalTraffic>();
		List<WindowTraffic> windowTrafficList = new ArrayList<WindowTraffic>();
		List<POITraffic> poiTrafficList = new ArrayList<POITraffic>();

		//Call dao methods
		totalTrafficDataDAO.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> totalTrafficList.add(e));
		windowTrafficDAO.findTrafficDataByDate(sdf.format(new Date())).forEach(e -> windowTrafficList.add(e));
		poiTrafficDataDAO.findAll().forEach(e -> poiTrafficList.add(e));

		//prepare response
		Response response = new Response();
		response.setTotalTraffic(totalTrafficList);
		response.setWindowTraffic(windowTrafficList);
		response.setPoiTraffic(poiTrafficList);
		//logger.info("Sending to UI "+response);
		//send to ui
		this.template.convertAndSend("/topic/trafficData", response);
	}
	
}
