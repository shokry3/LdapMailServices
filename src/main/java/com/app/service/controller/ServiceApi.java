package com.app.service.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.service.pojo.Request;
import com.app.service.services.MailService;
import com.app.service.utils.LdabUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.frimtec.libraries.jpse.PowerShellExecutor;

@RestController
@RequestMapping("/back/serv/")
public class ServiceApi {
	
	@Autowired
	MailService mailService;

	//Create New Domain User
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
	
	// Disable domain user (Just disable user on active directory).
	@PostMapping("/dsuser/{user}")
	public ResponseEntity<String> disableUser(@PathVariable String user) throws JsonProcessingException {
		String result  = null;
		String json = null;
		String domainUser = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> domainData  = LdabUtils.checkLdabUserCode(user);
			if (domainData != null && domainData.get("userDomain") != null) {
				domainUser = domainData.get("userDomain");
				result = LdabUtils.disableEnableUser(user, 2);
				String script1 = "Set-Mailbox -Identity \"" + domainUser + "@alriyadh.gov.sa" + "\" -HiddenFromAddressListsEnabled $true";
				String script2 = "Disable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false"; 
				PowerShellExecutor executor = PowerShellExecutor.instance();
				System.out.println("PowerShell runtime version " + executor.version()
						.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
				executor.execute(script1).getStandardOutput();
				//Execute script1 then wait 3 seconds to execute script2
	            new java.util.Timer().schedule( 
	                    new java.util.TimerTask() {
	                        @Override
	                        public void run() {
	                        	executor.execute(script2).getStandardOutput();
	                        }
	                    }, 
	                    3000 
	            );
				
				resultMap.put("result", "1");
				resultMap.put("message", result);
				resultMap.put("script1", script1);
				resultMap.put("script2", script2);
			}else {
				resultMap.put("result", "-1");
				resultMap.put("message", "Domain user not exist!");
			}
			
			json = mapper.writeValueAsString(resultMap);
			return ResponseEntity.ok().body(json);

		} catch (Exception ex) {
			ex.printStackTrace();
			resultMap.put("result", "-1");
			resultMap.put("message", ex.getMessage() + " , " + result);
			resultMap.put("script1", "Set-Mailbox -Identity \"" + domainUser + "@alriyadh.gov.sa" + "\" -HiddenFromAddressListsEnabled $true");
			resultMap.put("script2", "Disable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false");
			json = mapper.writeValueAsString(resultMap);
			return ResponseEntity.internalServerError().body(json);
		}
	}
	
	// Enable domain user (Enable user on active directory).
	@PostMapping("/enuser/{user}")
	public ResponseEntity<String> enableUser(@PathVariable String user) {
		String message = null;
		String result  = null;
		try {
			Map<String, String> domainData  = LdabUtils.checkLdabUserCode(user);
			String domainUser = domainData.get("userDomain");
			result = LdabUtils.disableEnableUser(user, 1);
				String script = "Enable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false";
				System.out.println(script);
				PowerShellExecutor executor = PowerShellExecutor.instance();
				System.out.println("PowerShell runtime version " + executor.version()
						.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
				executor.execute(script).getStandardOutput();
				message = "{\"result\":\"1\",\"message\":\""+ " , " +  result+ "\"}";
			return ResponseEntity.ok().body(message);

		} catch (Exception ex) {
			ex.printStackTrace();
			message = "{\"result\":\"-1\",\"message\":\""+ex.getMessage()+ " , " +  result+ "\"}";
			return ResponseEntity.internalServerError().body(message);
		}
	}

	// Stop domain user (Stop, disable and remove user from outlook).
	@PostMapping("/stopsUser/{user}")
	public ResponseEntity<Map<String,String>> stopsUser(@PathVariable String user) throws JsonProcessingException {
		String result  = null;
		String domainUser = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Map<String, String> domainData  = LdabUtils.checkLdabUserCode(user);
			domainUser = domainData.get("userDomain");
			result = LdabUtils.disableEnableUser(user, 2);
			String script = "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force ; if ($?) {$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass)}  ; "
					+ "if ($?) {$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://exg19-01.itamana.net/PowerShell/ -Credential $UserCredential -Authentication kerberos -AllowRedirection } ; "
					+ "if ($?) {Import-PSSession $Session -AllowClobber } ; "
					+ "if ($?) {Set-Mailbox -Identity \""+domainUser+"@alriyadh.gov.sa\" -HiddenFromAddressListsEnabled $true}; "; 
			PowerShellExecutor executor = PowerShellExecutor.instance();
			System.out.println("PowerShell runtime version " + executor.version()
					.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
			String out = executor.execute(script).getStandardOutput();
			System.out.println("PowerShell  out: " + out);
			resultMap.put("result", "1");
			resultMap.put("message", result);
			resultMap.put("script", script);
			return ResponseEntity.ok().body(resultMap);

		} catch (Exception ex) {
			ex.printStackTrace();
			resultMap.put("result", "-1");
			resultMap.put("message", ex.getMessage() + " , " + result);
			resultMap.put("script", "Set-Mailbox -Identity \"" + domainUser + "@alriyadh.gov.sa" + "\" -HiddenFromAddressListsEnabled $true;"
					+ "Disable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false");
			return ResponseEntity.internalServerError().body(resultMap);
		}
	}
	
	//Show and showFromAddress user email (show in outlook).
	@PostMapping("/showFromAddress/{user}")
	public ResponseEntity<Map<String,String>> showFromAddress(@PathVariable String user) throws JsonProcessingException {
		String result  = null;
		String domainUser = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		String script = "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force ; if ($?) {$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass)}  ; "
				+ "if ($?) {$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://exg19-01.itamana.net/PowerShell/ -Credential $UserCredential -Authentication kerberos -AllowRedirection } ; "
				+ "if ($?) {Import-PSSession $Session -AllowClobber } ; "
				+ "if ($?) {Set-Mailbox -Identity \""+domainUser+"@alriyadh.gov.sa\" -HiddenFromAddressListsEnabled $false}; "; 
		try {
			Map<String, String> domainData  = LdabUtils.checkLdabUserCode(user);
			domainUser = domainData.get("userDomain");
			result = LdabUtils.disableEnableUser(user, 2);
			PowerShellExecutor executor = PowerShellExecutor.instance();
			System.out.println("PowerShell runtime version " + executor.version()
					.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
			String out = executor.execute(script).getStandardOutput();
			System.out.println("PowerShell  out: " + out);
			resultMap.put("result", "1");
			resultMap.put("message", result);
			resultMap.put("script", script);
			return ResponseEntity.ok().body(resultMap);

		} catch (Exception ex) {
			ex.printStackTrace();
			resultMap.put("result", "-1");
			resultMap.put("message", ex.getMessage() + " , " + result);
			resultMap.put("script", script);
			return ResponseEntity.internalServerError().body(resultMap);
		}
	}
	
	// Activate domain user (In outlook and active directory).
	@PostMapping("/activateUser/{user}")
	public ResponseEntity<Map<String,String>> activateUser(@PathVariable String user) throws JsonProcessingException  {
		String result  = null;
		String domainUser = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		try {
			Map<String, String> domainData  = LdabUtils.checkLdabUserCode(user);
			domainUser = domainData.get("userDomain");
			result = LdabUtils.disableEnableUser(user, 1);
				String script = "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force;$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass);"
						+ "Enable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false";
				System.out.println(script);
				PowerShellExecutor executor = PowerShellExecutor.instance();
				System.out.println("PowerShell runtime version " + executor.version()
						.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
				executor.execute(script).getStandardOutput();
				resultMap.put("result", "1");
				resultMap.put("message", result);
				resultMap.put("script", "Enable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false");
			return ResponseEntity.ok().body(resultMap);

		} catch (Exception ex) {
			ex.printStackTrace();
			resultMap.put("result", "-1");
			resultMap.put("message", ex.getMessage() + " , " + result);
			resultMap.put("script", "Enable-Mailbox -Identity \"" + domainUser + "\" -Confirm:$false");
			return ResponseEntity.internalServerError().body(resultMap);
		}
	}
	

	//Modify domain user on Active Directory....
	@SuppressWarnings("unused")
	@PostMapping("/mmuser/{user}")
	public ResponseEntity<String> modifyDmUser(@PathVariable String user, @RequestBody Request request) {
		try {
			System.out.println(" User Code ::  " + user);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> datafields = mapper.convertValue(request, new TypeReference<Map<String, String>>() {
			});
			while (datafields.values().remove(null));// Remove attributes with null values from datafields map
			String output = LdabUtils.modifyDomainUser(user, datafields, null);
			System.out.println(" modify output ::  " + output);
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
			Map<String, String> failUsers = new HashMap<String, String>();
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
					failUsers.put("FailUser", request.getUserCode());
				}
			}
			
			if (updatedCount > 0) {
				String message = "{\"Result Message\":\"" + updatedCount + " Users Successfully Modified on AD from count "+ requests.size() + "\"}";
				String failUsersStr = mapper.writeValueAsString(failUsers);
				String json = message + failUsersStr;
				return ResponseEntity.ok().body(json);
			} else {
				return ResponseEntity.internalServerError().body("** AD Users List Modify Fail: ");
			}

		} catch (Exception ex) {
			System.out.println("** dmuser modify user fail to execute -------------------- ");
			ex.printStackTrace();
			return ResponseEntity.internalServerError().body("Found Exception: " + ex.getMessage());
		}
	}

	// Create new domain user (Create user on active directory and outlook email).
	@PostMapping("/cduser")
	public ResponseEntity<Object> confirmDomainUser(@RequestBody Request request) throws Exception {
		
		System.out.println("Request Body Data:: " + request.getUserCode() + " Employee Name: "+request.getEnglishName());
		Map<String, Object> resultSet = new HashMap<String, Object>();
		resultSet = mailService.confirmDomainUser(request);
		return ResponseEntity.ok().body(resultSet); 
	}
	
	//Move disabled domain user (Move the disabled user to OU=Disabled_Amana_Users).
	@PostMapping("/moveDisabledUser/{user}")
	public ResponseEntity<Map<String,String>> moveUser(@PathVariable String user) throws JsonProcessingException {
		String result  = null;
		String domainUser = null;
		Map<String, String> resultMap = new HashMap<String, String>();
		String script = "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force;$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass);"
				+ "Move-ADObject -Identity:"+domainUser+"@alriyadh.gov.sa\" -TargetPath:\"OU=Disabled_Amana_Users,OU=Amana-Users,DC=ITamana,DC=net\" -Server:amndc03"; 
		try {
			Map<String, String> domainData  = LdabUtils.checkLdabUserCode(user);
			domainUser = domainData.get("userDomain");
			result = LdabUtils.disableEnableUser(user, 2);
			PowerShellExecutor executor = PowerShellExecutor.instance();
			System.out.println("PowerShell runtime version " + executor.version()
					.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
			String out = executor.execute(script).getStandardOutput();
			System.out.println("PowerShell  out: " + out);
			resultMap.put("result", "1");
			resultMap.put("message", result);
			resultMap.put("script", script);
			return ResponseEntity.ok().body(resultMap);

		} catch (Exception ex) {
			ex.printStackTrace();
			resultMap.put("result", "-1");
			resultMap.put("message", ex.getMessage() + " , " + result);
			resultMap.put("script", script);
			return ResponseEntity.internalServerError().body(resultMap);
		}
	}

	// Get domain user data (Query a bout user on active directory).
	@GetMapping("/gduser/{user}")
	public ResponseEntity<Map<String, String>> findDomainUser(@PathVariable String user) throws Exception {
		System.out.println(user);
		Map<String, String> resultList = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			resultList = LdabUtils.checkLdabUserCode(user);
			/*
			 * Get HiddenFromAddress value ..........................
			 *  
			 ***/
			if(resultList.get("userDomain") != null) {
				String script = "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force ;"
						+ " if ($?) {$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass)}  ;"
						+ " if ($?) {$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://exg19-01.itamana.net/PowerShell/ -Credential $UserCredential -Authentication kerberos -AllowRedirection } ;"
						+ " if ($?) {Import-PSSession $Session -AllowClobber } ;" 
						+ " if ($?) {Get-Mailbox -Identity:"+resultList.get("userDomain")+" | Select-Object -Property HiddenFromAddressListsEnabled}";
				PowerShellExecutor executor = PowerShellExecutor.instance();
				System.out.println("PowerShell runtime version " + executor.version()
						.orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));
				String out = executor.execute(script).getStandardOutput();
				String hiddenFromAddress = out.substring(out.lastIndexOf(":") + 1);
				resultList.put("HiddenFromAddress", hiddenFromAddress);
			} 
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.ok().body(resultList);
		} catch (Exception ex) {
			resultList.put("outputMsg", "INTERNAL_SERVER_ERROR: catch Exception while find domain user.");
			ex.printStackTrace();
			String json = mapper.writeValueAsString(resultList);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultList);
		}
	}
	
	//Authenticate Windows User and get UserCode.
	@PostMapping("/authuser")
	public ResponseEntity<Map<String, Object>> authUser(@RequestBody Request user) throws Exception {
		Map<String, Object> resultList = new HashMap<String, Object>();
		try {
			resultList = LdabUtils.authWindowsUser(user.getDomainUser(), user.getDomainPwd());
			return ResponseEntity.ok().body(resultList);
		} catch (Exception ex) {ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultList);
		}
	}
	
	//Add user to active directory Group.
	@PostMapping("/addUserToGroup")
	public ResponseEntity<Map<String,String>> addUserToGroup(@RequestBody List<Object> requests) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> resultMap = new HashMap<String, String>();
		String result = null;
		try {
			for(Object request : requests) {
				Map<String, String> datafields = mapper.convertValue(request, new TypeReference<Map<String, String>>() {
				});
				result = LdabUtils.addUserToGroup(datafields.get("domainUser"), datafields.get("groupId"));
			}
			resultMap.put("output", "1");
			resultMap.put("message", result);
			return ResponseEntity.ok().body(resultMap);
		} catch (Exception ex) {ex.printStackTrace();
			resultMap.put("output", "-1");
			resultMap.put("message", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
		}
	}
	
	//Remove user from active directory Group.
	@PostMapping("/removeUserGroup")
	public ResponseEntity<Map<String,String>> removeUserFromGroup(@RequestBody List<Object> requests) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> resultMap = new HashMap<String, String>();
		String result = null;
		try {
			for(Object request : requests) {
				Map<String, String> datafields = mapper.convertValue(request, new TypeReference<Map<String, String>>() {
				});
				result = LdabUtils.removeUserFromGroup(datafields.get("domainUser"), datafields.get("groupId"));
			}
			resultMap.put("output", "1");
			resultMap.put("message", result);
			return ResponseEntity.ok().body(resultMap);
		} catch (Exception ex) {ex.printStackTrace();
			resultMap.put("output", "-1");
			resultMap.put("message", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultMap);
		}
	}
	
    //Get domain user active directory groups.
	@GetMapping("/userGroups/{user}")
	public ResponseEntity<String> getUserGroups(@PathVariable String user) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		String result = null;
		try {
			result = LdabUtils.getDomainUserGroups(user);
			resultMap.put("Domain User", user);
			resultMap.put("Domain Groups", result);
			return ResponseEntity.ok().body(result);
		} catch (Exception ex) {ex.printStackTrace();
			resultMap.put("output", "-1");
			resultMap.put("message", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}
	
	@GetMapping("/test")
	public ResponseEntity<?>  testData(@RequestParam String x) throws Exception { 
		if(x == null || x.isEmpty()) {
			System.out.println("x is null");
			throw new Exception("x is null or is emptyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
		}
		return ResponseEntity.ok().body(x);
	}
}
