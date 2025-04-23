package com.app.service.srinterface;

import java.util.Map;

import com.app.service.pojo.Request;

public interface IMailService {

	Map<String, Object> confirmDomainUser(Request request)  throws Exception ;
}
