package org.ekstep.search.actor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.ekstep.common.Platform;
import org.ekstep.common.dto.Request;
import org.ekstep.common.dto.Response;
import org.ekstep.common.dto.ResponseParams;
import org.ekstep.common.dto.ResponseParams.StatusType;
import org.ekstep.common.exception.ResponseCode;
import org.ekstep.compositesearch.enums.SearchOperations;
import org.ekstep.searchindex.elasticsearch.ElasticSearchUtil;
import org.ekstep.searchindex.util.CompositeSearchConstants;

import akka.actor.ActorRef;

public class HealthCheckManager extends SearchBaseActor {
	
	@Override
	protected void invokeMethod(Request request, ActorRef parent) {
		ElasticSearchUtil.initialiseESClient(CompositeSearchConstants.COMPOSITE_SEARCH_INDEX,
				Platform.config.getString("search.es_conn_info"));
		String operation = request.getOperation();
		if (StringUtils.equalsIgnoreCase(SearchOperations.HEALTH.name(), operation)) {
			Response response = checkIndexExists();
			OK("", response, parent);
		}
	}

	@Override
	public void OK(String responseIdentifier, Object vo, ActorRef parent) {
		Response response = new Response();
		if (vo instanceof Response) {
			response = (Response) vo;
		}
		parent.tell(response, getSelf());
	}

	public Response checkIndexExists() {
		List<Map<String, Object>> checks = new ArrayList<Map<String, Object>>();
		Response response = new Response();
		boolean index = false;
		try {
			index = ElasticSearchUtil.isIndexExists(CompositeSearchConstants.COMPOSITE_SEARCH_INDEX);
			if (index == true) {
				checks.add(getResponseData(response, true, "", ""));
			} else {
				checks.add(getResponseData(response, false, "404", "Elastic Search index is not avaialable"));
			}
		} catch (Exception e) {
			checks.add(getResponseData(response, false, "503", e.getMessage()));
		}
		response.put("checks", checks);
		return response;
	}

	public Map<String, Object> getResponseData(Response response, boolean res, String error, String errorMsg) {
		ResponseParams params = new ResponseParams();
		String err = error;
		Map<String, Object> esCheck = new HashMap<String, Object>();
		esCheck.put("name", "ElasticSearch");
		if (res == true && err.isEmpty()) {
			params.setErr("0");
			params.setStatus(StatusType.successful.name());
			params.setErrmsg("Operation successful");
			response.setParams(params);
			response.put("healthy", true);
			esCheck.put("healthy", true);
		} else {
			params.setStatus(StatusType.failed.name());
			params.setErrmsg(errorMsg);
			response.setResponseCode(ResponseCode.SERVER_ERROR);
			response.setParams(params);
			response.put("healthy", false);
			esCheck.put("healthy", false);
			esCheck.put("err", err);
			esCheck.put("errmsg", errorMsg);
		}
		return esCheck;
	}

}
