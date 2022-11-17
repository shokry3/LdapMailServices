package com.app.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.ldapUtils.LdabUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frimtec.libraries.jpse.PowerShellExecutor;

@RestController
@RequestMapping("/back/serv/")
public class ServiceApi {

	@RequestMapping("/test")
	public String home() {
		return "home.jsp";

	}

	@RequestMapping("/app")
	public String test() {
		return "app.jsp";

	}

	// Test Script using PowerShellExecutor API : String script =
	// "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force ; if
	// ($?) {$UserCredential = New-Object
	// System.Management.Automation.PsCredential('ADF@itamana.net',$pass)} ; if ($?)
	// {$Session = New-PSSession -ConfigurationName Microsoft.Exchange
	// -ConnectionUri http://exg19-01.itamana.net/PowerShell/ -Credential
	// $UserCredential -Authentication kerberos -AllowRedirection } ; if ($?)
	// {Import-PSSession $Session -AllowClobber } ; if ($?) {Get-MailboxDatabase |
	// ft name}";

	// Create New Domain User
	@PostMapping("/mbox")
	public ResponseEntity<String> createMailBocx(@RequestBody Request request) {
		System.out.println("adOpject :: " + request);
		String output = null;
		try {
			String domainUser = request.getDomainUser();
			String mailBox = request.getMailBox();
			System.out.println("domainUser: " + domainUser + " MailBox: " + mailBox);
			if (domainUser != null) {
				String script = "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force ; if ($?) {$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass)}  ; if ($?) {$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://exg19-01.itamana.net/PowerShell/ -Credential $UserCredential -Authentication kerberos -AllowRedirection } ; if ($?) {Import-PSSession $Session -AllowClobber } ; if ($?) {Get-MailboxDatabase | ft name}; if ($?){$mail = \""
						+ domainUser + "\" ;Enable-Mailbox -Identity $mail -Database " + mailBox
						+ " ;Set-CASMailbox -Identity  $mail  -OWAEnabled $true}";
				System.out.println("User domain executed script::   " + script);
				PowerShellExecutor executor = PowerShellExecutor.instance();
				System.out.println("PowerShell runtime version " + executor.version()
						.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));

				System.out.println("Execute command: ");
				output = executor.execute(script).getStandardOutput();
				System.out.println(" output = " + output);
			}
			if (output != null && !output.isEmpty()) {
				System.out.println(" mbox service call done is ok ************** ");
				return ResponseEntity.ok().body(output);
			} else {
				System.out.println(" 1- mbox service call fail to execute -------------------- ");
				return ResponseEntity.internalServerError().body("Error: Couldn't execute script");
			}

		} catch (Exception ex) {
			System.out.println(" 2- mbox service call fail to execute -------------------- ");
			ex.printStackTrace();
			return ResponseEntity.internalServerError().body("Found Exception: " + ex.getMessage());
		}
	}

	// Disable domain user.
	@PostMapping("/dmuser")
	public ResponseEntity<String> disableUser(@RequestBody Request request) {
		String output = null;
		try {
			String domainUser = request.getDomainUser();
			if (domainUser != null) {
				String script = "Disable-ADAccount -Identity \"" + domainUser + "\" ";
				System.out.println("User domain stoped executed script::   " + script);
				PowerShellExecutor executor = PowerShellExecutor.instance();
				System.out.println("PowerShell runtime version " + executor.version()
						.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
				output = executor.execute(script).getStandardOutput();
				System.out.println(" dmuser service call done is ok ************** ");
				return ResponseEntity.ok().body("User " + domainUser + " Desabled");
			} else {
				System.out.println(" 1- dmuser service call fail to execute -------------------- ");
				return ResponseEntity.internalServerError().body("Error: Couldn't disable user " + domainUser);
			}

		} catch (Exception ex) {
			System.out.println(" 2- dmuser service call fail to execute -------------------- ");
			ex.printStackTrace();
			return ResponseEntity.internalServerError().body("Found Exception: " + ex.getMessage());
		}
	}

	//Modify domain user on Active Directory....
	@SuppressWarnings("unused")
	@PostMapping("/mmuser/{user}")
	public ResponseEntity<String> modifyDmUser(@PathVariable String user, @RequestBody Request request) {
		try {
			System.out.println(" User Code ::  " + user);
			System.out.println(" request Object ::  " + request.toString());
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> datafields = mapper.convertValue(request, new TypeReference<Map<String, String>>() {
			});
			while (datafields.values().remove(null));// Remove attributes with null values from datafields map
			String output = LdabUtils.modifyDomainUser(user, datafields, null);
			if (output == null) {
				return ResponseEntity.ok().body("User " + user + " Successfully Modified on AD.");
			} else {
				return ResponseEntity.internalServerError().body("Modified Fail: " + output);
			}

		} catch (Exception ex) {
			System.out.println(" 2- dmuser modify user fail to execute -------------------- ");
			ex.printStackTrace();
			return ResponseEntity.internalServerError().body("Found Exception: " + ex.getMessage());
		}
	}
	
	//Modify list of users on Active Directory....
	@PostMapping("/mdulist")
	public ResponseEntity<String> modifyDmUsers(@RequestBody List<Request> requests) {
		try {
			System.out.println("1-Starting update domain users data with count  " + requests.size());
			
			ObjectMapper mapper = new ObjectMapper();
			int updatedCount = 0;
			for(Request request : requests) {
				Map<String, String> datafields = mapper.convertValue(request, new TypeReference<Map<String, String>>() {});
				while (datafields.values().remove(null));// Remove attributes with null values from datafields map.
				System.out.println("datafields " + datafields.toString());
				String output = LdabUtils.modifyDomainUser(request.getUserCode() , datafields, null);
				if (output == null) {
					System.out.println("- User " + request.getUserCode() + " Successfully Modified on AD.");
					updatedCount++;
				}else {
					System.out.println("- User " + request.getUserCode() + " Modify Fail on AD: " + output);
				}
			}
			
			if (updatedCount > 0) {
				return ResponseEntity.ok().body("- Count " + updatedCount + " Users Successfully Modified on AD.");
			} else {
				return ResponseEntity.internalServerError().body("** AD Users List Modify Fail: ");
			}

		} catch (Exception ex) {
			System.out.println("** dmuser modify user fail to execute -------------------- ");
			ex.printStackTrace();
			return ResponseEntity.internalServerError().body("Found Exception: " + ex.getMessage());
		}
	}

	// Create new domain user
	@PostMapping("/cduser")
	public ResponseEntity<String> confirmDomainUser(@RequestBody Request request) throws Exception {
		System.out.println("Request Body Data:: " + request.getUserCode() + " Employee Name: "+request.getEnglishName());
		Map<String, String> resultList = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultList = LdabUtils.confirmDomainUser(request);
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.ok().body(json);
		} catch (Exception ex) {
			System.out.println(" 1- confirmDomainUser Service execute faile  -------------------- ");
			ex.printStackTrace();
			resultList.put("errorExist", "1");
			resultList.put("errorMsg", "confirmDomainUser Found Exception: " + ex.getMessage());
			resultList.put("outputMsg", "INTERNAL_SERVER_ERROR: catch Exception while confirmDomainUser method");
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json);
		}
	}

	// Create new domain user
	@GetMapping("/gduser/{user}")
	public ResponseEntity<String> findDomainUser(@PathVariable String user) throws Exception {
		System.out.println(user);
		Map<String, String> resultList = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultList = LdabUtils.checkLdabUserCode(user);
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.ok().body(json);
		} catch (Exception ex) {
			resultList.put("outputMsg", "INTERNAL_SERVER_ERROR: catch Exception while find domain user.");
			ex.printStackTrace();
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(json);
		}
	}
}
