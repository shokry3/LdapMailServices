package com.app.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.app.service.pojo.Request;
import com.app.service.srinterface.IMailService;
import com.app.service.utils.LdabUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MailService implements IMailService {

	@Override
	public Map<String, Object> confirmDomainUser(Request request) throws Exception {
		
		Map<String, Object> dataSet = new HashMap<String, Object>();
		// Map<String, Object> statusDetails = new HashMap<String, Object>();
		Map<String, Object> outputData = new HashMap<String, Object>();
		Map<String, String> resultSet = new HashMap<String, String>();
		
		if (request == null) {
			throw new RuntimeException("Must inserted all JSON parameter correctly");
		} else {
			resultSet = LdabUtils.confirmDomainUser(request);
			if(resultSet != null && resultSet.get("CreateFlag").equals("1")) {
				LdabUtils.addUserToGroup(resultSet.get("userDomain"), "All Users");
			}
			outputData.put("ResultSet", resultSet);

			if (outputData.get("ResultSet") != null) {
				dataSet = returnJsonDataTemp(1, outputData, null, null);
			} else {
				dataSet = returnJsonDataTemp(2, null, "E0002", "No Requests Data Found.");
			}
		
		}
		return dataSet;
	}
	
	// returnJsonDataTemp --> (main template of return json data success of fail)
	public Map<String, Object> returnJsonDataTemp(int flage, Map<String, Object> outputData, String errorCode,
			String errorMsg) {
		Map<String, Object> tempJsonData = new HashMap<String, Object>();
		Map<String, Object> statusDetails = new HashMap<String, Object>();
		if (flage == 1) { // success Process
			statusDetails.put("StatusCode", "200");
			statusDetails.put("StatusMessage", "Success Process");
			tempJsonData.put("OutputData", outputData);
		} else { // 2 fail
			statusDetails.put("StatusCode", errorCode);
			statusDetails.put("StatusMessage", errorMsg);
		}
		tempJsonData.put("StatusDetails", statusDetails);
		return tempJsonData;
	}

}
