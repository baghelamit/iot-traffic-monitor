package com.iot.app.springboot.vo;

import java.io.Serializable;
import java.util.List;

import com.iot.app.springboot.model.POITraffic;
import com.iot.app.springboot.model.TotalTraffic;
import com.iot.app.springboot.model.WindowTraffic;

/**
 * Response object containing traffic details that will be sent to dashboard.
 * 
 * @author jshah
 *
 */
public class Response implements Serializable {
	private List<TotalTraffic> totalTraffic;
	private List<WindowTraffic> windowTraffic;
	private List<POITraffic> poiTraffic;
	
	public List<TotalTraffic> getTotalTraffic() {
		return totalTraffic;
	}
	public void setTotalTraffic(List<TotalTraffic> totalTraffic) {
		this.totalTraffic = totalTraffic;
	}
	public List<WindowTraffic> getWindowTraffic() {
		return windowTraffic;
	}
	public void setWindowTraffic(List<WindowTraffic> windowTraffic) {
		this.windowTraffic = windowTraffic;
	}
	public List<POITraffic> getPoiTraffic() {
		return poiTraffic;
	}
	public void setPoiTraffic(List<POITraffic> poiTraffic) {
		this.poiTraffic = poiTraffic;
	}

}
