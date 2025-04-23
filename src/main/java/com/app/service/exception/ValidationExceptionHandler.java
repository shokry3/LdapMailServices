package com.app.service.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.app.service.pojo.RequestExceptionErrors;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestControllerAdvice
public class ValidationExceptionHandler {

	
	public Map<String, Object> returnBadReqErrorTemp(RequestExceptionErrors requestExceptionErrors){
		Map<String, Object> tempJsonData = new HashMap<String, Object>();
		tempJsonData.put("StatusDetails", requestExceptionErrors);
		return tempJsonData;
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> handleExceptions(MethodArgumentTypeMismatchException exception) {
		//exception.printStackTrace();
		System.out.println("Exception : "+exception.getStackTrace());
		System.out.println("NumberFormatException");
		List<String> errors = new ArrayList<>();
		//((Errors) exception).getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		//errors.add(exception.getMessage());
		errors.add("Invalid Number  --> Must Insert Number Format");
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.out.println("NumberFormatException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleExceptions(DataIntegrityViolationException exception) {
		//exception.printStackTrace();
		System.out.println("Exception : "+exception.getStackTrace());
		System.out.println("DataIntegrityViolationException");
		List<String> errors = new ArrayList<>();
		//((Errors) exception).getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		//errors.add(exception.getMessage());
		errors.add("invalid SQL statement");
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.out.println("DataIntegrityViolationException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BadSqlGrammarException.class)
	public ResponseEntity<Map<String, Object>> handleExceptions(BadSqlGrammarException exception) {
		//exception.printStackTrace();
		System.out.println("Exception : "+exception.getStackTrace());
		System.out.println("BadSqlGrammarException");
		List<String> errors = new ArrayList<>();
		//((Errors) exception).getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		//errors.add(exception.getMessage());
		errors.add("invalid SQL statement : "+exception.getMessage());
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.out.println("BadSqlGrammarException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Map<String, Object>> handleExceptions(HttpMessageNotReadableException exception , HttpServletRequest request) throws IOException {
		//exception.printStackTrace();
		//System.out.println("Exception : "+exception.getStackTrace());
		//System.out.println("HttpMessageNotReadableException");
		ContentCachingRequestWrapper nativeRequest = (ContentCachingRequestWrapper) request;
	    String  requestBody = getRequestBoudy(request);
		System.out.println("body :::  " + requestBody);
		List<String> errors = new ArrayList<>();
		//((Errors) exception).getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		errors.add("Required request body is missing --> Must inserted all JSON parameter correctly");
		errors.add(requestBody);
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.out.println("HttpMessageNotReadableException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException exception) {
		//exception.printStackTrace();
		//System.out.println("Exception : "+exception.getStackTrace());
		//System.out.println("MethodArgumentNotValidException");
		List<String> errors = new ArrayList<>();
		((Errors) exception).getAllErrors().forEach(err -> errors.add(err.getDefaultMessage()));
		//errors.add(exception.getMessage());
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.out.println("MethodArgumentNotValidException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}	

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(MissingServletRequestParameterException exception) {
		//exception.printStackTrace();
		//System.out.println("Exception : "+exception.getStackTrace());
		//System.err.println("MissingServletRequestParameterException");
		List<String> errors = new ArrayList<>();
		errors.add(exception.getMessage());
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.err.println("MissingServletRequestParameterException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(RuntimeException exception) {
		exception.printStackTrace();
		//System.out.println("Exception : "+exception.getStackTrace());
		//System.err.println("RuntimeException");
		List<String> errors = new ArrayList<>();
		// ((Errors) ex).getAllErrors().forEach(err ->
		// errors.add(err.getDefaultMessage()));
		exception.printStackTrace();
		errors.add(exception.getMessage());
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.err.println("RuntimeException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleValidationExceptions(FileNotFoundException exception) {
		exception.printStackTrace();
		System.out.println("Exception : "+exception.getStackTrace());
		System.err.println("FileNotFoundException");
		List<String> errors = new ArrayList<>();
		// ((Errors) ex).getAllErrors().forEach(err ->
		// errors.add(err.getDefaultMessage()));
		exception.printStackTrace();
		errors.add(exception.getMessage());
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.err.println("FileNotFoundException");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler
	public ResponseEntity<Map<String, Object>> handleExceptions(Exception exception) {
		//exception.printStackTrace();
		//System.out.println("Exception : "+exception.getStackTrace());
		List<String> errors = new ArrayList<>();
		errors.add(exception.getMessage());
		RequestExceptionErrors error = new RequestExceptionErrors();
		error.setStatusCode(HttpStatus.BAD_REQUEST.value());
		error.setStatusMessage(errors.toString());
		error.setTime(new Date());
		System.out.println("Exception");
		Map<String, Object> errorTemp = returnBadReqErrorTemp(error);
		return new ResponseEntity<>(errorTemp, HttpStatus.BAD_REQUEST);
	}
	
	public String getRequestBoudy(HttpServletRequest request) {
		ContentCachingRequestWrapper nativeRequest = (ContentCachingRequestWrapper) request;
	    String  requestEntityAsString = new String(nativeRequest.getContentAsByteArray());
	    if (requestEntityAsString == null || requestEntityAsString.isEmpty()) {
	    	requestEntityAsString = "RequestBody is empty (null).";
	    }else {
	    	requestEntityAsString = "Request: "+requestEntityAsString;
	    }
		
		return requestEntityAsString;
	}
}
