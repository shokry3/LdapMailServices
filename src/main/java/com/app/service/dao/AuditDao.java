package com.app.service.dao;


import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AuditDao {

	@Autowired
	JdbcTemplate jdbcTemplate;
	 
	
	@Async
	public Object addApiAudit(Map<String, Object> datafields) {
		Object result = null;
		Map<String, Object> outParams = new HashMap<String, Object>();
		try {
			jdbcTemplate.setResultsMapCaseInsensitive(true);
			SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName("CMNV3")
					.withFunctionName("add_SERVICE_aud_ALL_F").declareParameters(
							new SqlParameter("p_service_name", Types.VARCHAR), new SqlParameter("p_APP_CODE", Types.VARCHAR),
							new SqlParameter("p_client_ip", Types.VARCHAR), new SqlParameter("p_pInput", Types.CLOB),
							new SqlParameter("p_poutput", Types.CLOB), new SqlParameter("p_status_code", Types.NUMERIC),
							new SqlParameter("p_status_desc", Types.VARCHAR), new SqlParameter("p_full_status_desc", Types.CLOB),
							new SqlParameter("p_note", Types.VARCHAR));

			MapSqlParameterSource params = new MapSqlParameterSource(datafields);
			
			outParams.putAll(simpleJdbcCall.execute(params));
			if(outParams != null && outParams.size() > 0) {
				result = outParams.get("return");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		//System.out.println("Service Call Audit Serial is: "+result);
		return result;
	}

}

