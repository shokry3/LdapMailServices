package com.app.service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.pojo.MailInfo;
import com.app.service.utils.MailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/general/serv/")
public class GeniralApi {
	
	@PostMapping("/sendMail")
	public ResponseEntity<String> sendMail(@RequestBody MailInfo mailInfo) throws Exception {
		Map<String, String> resultList = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			String result = MailUtil.sendEmail(mailInfo.getMsgFrom(),mailInfo.getMsgTo(),mailInfo.getMsgCC(),mailInfo.getSubject(),mailInfo.getBody(),0,null,null);
			resultList.put("result", result);
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.ok().body(json);
		} catch (Exception ex) {ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Mail Sent Fail");
		}
	}
	

}
