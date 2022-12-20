package com.app.service.ldapUtils;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.json.JSONObject;

import com.app.service.Request;
import com.app.service.security.CustomLdapSslSocketFactory;
import com.github.frimtec.libraries.jpse.PowerShellExecutor;

public class LdabUtils {

	private static SecureRandom random = new SecureRandom();
	
	public static Hashtable getLdabEnv() {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "Simple");
		env.put(Context.SECURITY_PROTOCOL, "ssl"); // Enable SSL Connection for user and email creation.
		env.put("java.naming.ldap.factory.socket", CustomLdapSslSocketFactory.class.getName());
		env.put(Context.SECURITY_PRINCIPAL, "ADF@itamana.net");
		env.put(Context.SECURITY_CREDENTIALS, ")@$#893jofdLKJFDL");
		env.put(Context.PROVIDER_URL, "ldap://amndc03.itamana.net:636"); // 10.120.4.1:9389
		return env;
	}
	
	// CHECK User exist on Active Directory by domain name
    public static boolean checkLdabUser(String domainUser) {
        // String userCode = null;
        LdapContext ctx = null;
        try {
            //First - connect
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, "ADF@itamana.net");
            env.put(Context.SECURITY_CREDENTIALS, ")@$#893jofdLKJFDL");
            env.put(Context.PROVIDER_URL, "ldap://amndc03.itamana.net:389");
            ctx = new InitialLdapContext(env, null);
            //Second - get user detail
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration answer = ctx.search("DC=ITamana,DC=net", "SamAccountName=" + domainUser, constraints);
            if (answer.hasMore()) {
                System.out.println("LDAP USER EXIST....");
                return true;
            } else {
                //System.out.println("LDAP " + userName + " USER NOTTTTTTTTTTTTTTTTTTTTTT EXIST false ....");
                return false;
            }
        } catch (Exception ex) {
            //System.out.println("LDAP " + userName + " USER NOTTTTTTTTTTTTTTTTTTTTTT EXIST false ....");
            //ex.printStackTrace();
            return false;
        }

    }

	// CHECK User exist on Active Directory by UserCode
    public static Map<String, String> checkLdabUserCode(String userCode) {
        Map<String, String> resultList = new HashMap<String, String>();
        String entry = null;
        LdapContext ctx = null;
        try {
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            env.put(Context.SECURITY_PRINCIPAL, "ADF@itamana.net");
             env.put(Context.SECURITY_CREDENTIALS, ")@$#893jofdLKJFDL");
            env.put(Context.PROVIDER_URL, "ldap://amndc03.itamana.net:389");
            ctx = new InitialLdapContext(env, null);
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {
                "distinguishedName", "sn", "givenname", "mail", "userPrincipalName", "msExchVersion","department","company","pwdLastSet",
                "userAccountControl", "accountExpires", "physicalDeliveryOfficeName", "SamAccountName","mobile","title","manager"
            };
            constraints.setReturningAttributes(attrIDs);
            NamingEnumeration answer =
                ctx.search("DC=ITamana,DC=net", "physicalDeliveryOfficeName=" + userCode, constraints);

            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                if (attrs != null && attrs.get("SamAccountName") != null) {
                    entry = (String) attrs.get("distinguishedName").get();
                    String userDomain = (String) attrs.get("SamAccountName").get();
                    String mailBox = attrs.get("msExchVersion") == null ? "0" : "1";
                    resultList.put("SamAccountName", userDomain);
                    resultList.put("mailExist", mailBox);
                    resultList.put("mailBox", (String)attrs.get("userPrincipalName").get());
                    resultList.put("entry", entry);
                    resultList.put("userDomain", userDomain);
                    resultList.put("mobile", (String) attrs.get("mobile").get());
                    String title = attrs.get("title") == null ? "title not exist" : (String) attrs.get("title").get();
                    resultList.put("title", title);
                    String department = attrs.get("department") == null ? "department not exist" : (String) attrs.get("department").get();
                    resultList.put("department", department);
                    String company = attrs.get("company") == null ? "company not exist" : (String) attrs.get("company").get();
                    resultList.put("company", company);
                    String manager = attrs.get("manager") == null ? "manager not exist" : (String) attrs.get("manager").get();
                    resultList.put("manager", manager);
                    String pwdLastSet = attrs.get("pwdLastSet") == null ? null : (String) attrs.get("pwdLastSet").get();
                    if(pwdLastSet != null && !pwdLastSet.isEmpty()) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        long fileTime = (Long.parseLong(pwdLastSet) / 10000L) - + 11644473600000L;  
                        Date date=new Date(fileTime); 
                        LocalDate resetDate = LocalDate.parse(df.format(date));
                        LocalDate maxDate = resetDate.plusDays(90);
                        resultList.put("pwdLastSet", resetDate.toString());
                        resultList.put("maxPwdAge", maxDate.toString());
                    }
                }
                return resultList;
            } else {
            	resultList.put("outputMsg", "Domain user not exist.");
            	System.out.println("Domain user " + userCode + " not exist.");
                return null;
            }
        } catch (Exception ex) {
            resultList.put("outputMsg", "Server Error while find domain user.");
            System.out.println("Exception while find domain user " + userCode + " : "+ex.getMessage());
            //ex.printStackTrace();
            return null;
        }
    }
    
    //Authenticat Windows User and get UserCode..
    public static String authWindowsUser(String userName, String password) {
        // String userCode = null;
        LdapContext ctx = null;
        try {
            //First - connect
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.SECURITY_AUTHENTICATION, "Simple");
            //            env.put(Context.SECURITY_PRINCIPAL, "ADF@itamana.net");
            //            env.put(Context.SECURITY_CREDENTIALS, ")@$#893jofdLKJFDL");
            env.put(Context.SECURITY_PRINCIPAL, userName + "@itamana.net");
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.PROVIDER_URL, "ldap://amndc03.itamana.net:389");
            ctx = new InitialLdapContext(env, null);
            //Second - get user detail
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {
                "distinguishedName", "sn", "givenname", "mail", "userPrincipalName", "msExchVersion",
                "userAccountControl", "accountExpires", "physicalDeliveryOfficeName", "SamAccountName"
            };
            constraints.setReturningAttributes(attrIDs);
            NamingEnumeration answer = ctx.search("DC=ITamana,DC=net", "SamAccountName=" + userName, constraints);
            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                if (attrs != null && attrs.get("physicalDeliveryOfficeName") != null) {
                    System.out.println("Windows USER EXIST & Authenticated....");
                    String userCode = (String) attrs.get("physicalDeliveryOfficeName").get();
                    return userCode;
                }else{
                    System.out.println("Windows " + userName + " Authentication Fail ....");
                    return null;
                }
            } else {
                System.out.println("Windows " + userName + " Authentication Fail ....");
                return null;
            }
        } catch (Exception ex) {
            System.out.println("Windows " + userName + " Authentication Fail ....");
            return null;
        }
    }
	
    //-This method responsible for user domain creation.....
    public static Map<String, String> createLdabUser(Map<String, String> userDataList) {
        Map<String, String> resultList = new HashMap<String, String>();
        String userDomainName = null;
        String path = "OU=Users_Container,DC=ITamana,DC=net";
        String userName = userDataList.get("UserName");
        String fullEnglishName = userDataList.get("FullEnName");
        String entryDN = "CN=" + userName + "," + path;
        DirContext ctx = null;
        try {
            userDomainName = checkUserDomain(fullEnglishName);

            Attribute cn = new BasicAttribute("cn", userName);
            Attribute oc = new BasicAttribute("objectClass");
            oc.add("top");
            oc.add("person");
            oc.add("organizationalPerson");
            oc.add("user");

            ctx = new InitialDirContext(getLdabEnv());

            Attributes entry = new BasicAttributes();
            entry.put(cn);
            entry.put(oc);

            entry.put("sAMAccountName", userDomainName);
            entry.put("userPrincipalName", userDomainName + "@alriyadh.gov.sa");
            entry.put("DisplayName", userName);
            if(userDataList.get("DirDesc") != null && !userDataList.get("DirDesc").isEmpty())
            	entry.put("department", userDataList.get("DirDesc"));
            if (userDataList.get("UserMobileNo") != null && !userDataList.get("UserMobileNo").isEmpty())
                entry.put("mobile", userDataList.get("UserMobileNo"));
            entry.put("GivenName", userDataList.get("UserName").toString().split(" ")[0]);
            String arrayName[] = userDataList.get("UserName").toString().split(" ");
            entry.put("sn", arrayName[arrayName.length - 1]);
            entry.put("physicalDeliveryOfficeName", userDataList.get("UserCode"));
            if (userDataList.get("EmployeeJob") != null && !userDataList.get("EmployeeJob").isEmpty())
                entry.put("Title", userDataList.get("EmployeeJob"));
            entry.put("Description", fullEnglishName);
            
            //get employee manager data by manager usercode.
            Map<String, String> managerData = checkLdabUserCode(userDataList.get("manager"));
			if(managerData != null && managerData.size() > 0){
				entry.put("manager", managerData.get("entry"));
			}
			if (userDataList.get("company") != null && !userDataList.get("company").isEmpty())
				entry.put("company", userDataList.get("company"));

            // some useful constants from lmaccess.h
            int UF_ACCOUNTDISABLE = 0x0002; //Disable account
            int UF_PASSWD_NOTREQD = 0x0020; //Users cannot change passwords
            int UF_PASSWD_CANT_CHANGE = 0x0040;
            int UF_NORMAL_ACCOUNT = 0x0200; //Normal user
            int UF_DONT_EXPIRE_PASSWD = 0x10000; //password never expires
            int UF_PASSWORD_EXPIRED = 0x800000; //Password has expired

            entry.put("userAccountControl",
                      Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWD_NOTREQD + UF_PASSWORD_EXPIRED +
                                       UF_ACCOUNTDISABLE));
             
            ctx.createSubcontext(entryDN, entry);   //commented just for test environment.......
            System.out.println("Created disabled account for: " + userName + "  with domain : " + userDomainName);
            resultList.put("userDomain", userDomainName);

            ModificationItem[] mods = new ModificationItem[3];

            // Replace the "unicdodePwd" attribute with a new value
            // Password must be both Unicode and a quoted string
            String newRandomPwd = userDataList.get("DomainPwd");//generateRandomPassword();
            String newQuotedPassword = "\"" + newRandomPwd + "\"";
            //System.out.println("newQuotedPassword : " + newRandomPwd);
            byte[] newUnicodePassword = newQuotedPassword.getBytes("UTF-16LE");

            mods[0] =
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                     new BasicAttribute("unicodePwd", newUnicodePassword));
            mods[1] =
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                     new BasicAttribute("userAccountControl",
                                                        Integer.toString(UF_NORMAL_ACCOUNT + UF_PASSWORD_EXPIRED)));

            mods[2] =
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                                     new BasicAttribute("pwdLastSet", Integer.toString(0)));

            // Perform the update
            ctx.modifyAttributes(entryDN, mods);   //commented just for test environment.......
            System.out.println("Set password & updated userccountControl");
            resultList.put("domainPwd", newRandomPwd);
            // now create user mail box.......
            String createMailBocxResult = createMailBocx(userDomainName, userDataList.get("EmailDb"));
            if(createMailBocxResult != null) {
            	resultList.put("createMailBocxOut", createMailBocxResult);
            }

        } catch (Exception e) {
            System.out.println("Problem adding or modifing user on AD: " + e.getMessage());
            resultList.put("errorExist", "1");
            resultList.put("errorMsg", e.getMessage());
            resultList.put("operation", "Create New User");
            e.printStackTrace();
            return resultList;
        }

        System.out.println("Successfully created User: " + userName);
        return resultList;
    }
    
    //Create user mail box account.
    public static String createMailBocx(String domainUser, String mailBox) {
		String output = null;
		try {
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
				return null;
			} else {
				System.out.println(" 1- mbox service call fail to execute -------------------- ");
				return "Error: Couldn't execute script";
			}

		} catch (Exception ex) {
			System.out.println(" 2- mbox service call fail to execute -------------------- ");
			ex.printStackTrace();
			return "Found Exception: " + ex.getMessage();
		}
	}

	// -This method for user domain modify........................
	public static String modifyDomainUser(String userCode, Map<String, String> datafields,String entryPath) {
		Map<String, String> attrList = new HashMap<String, String>();
        if(entryPath == null || entryPath.isEmpty()) {
    		Map<String, String> domainDataList = checkLdabUserCode(userCode);
            if(domainDataList != null && domainDataList.size() > 0){
                entryPath = domainDataList.get("entry");
            }
        }
		
		if (entryPath == null || entryPath.isEmpty()) {
			return "This user code dose not exist on Active Directory";
		} else {
			DirContext ctx = null;
			try {
				if (datafields.get("dirDesc") != null && !datafields.get("dirDesc").isEmpty())
					attrList.put("department", datafields.get("dirDesc"));
				if (datafields.get("mobileNo") != null && !datafields.get("mobileNo").isEmpty())
					attrList.put("mobile", datafields.get("mobileNo"));
				if (datafields.get("arabicName") != null && !datafields.get("arabicName").isEmpty())
					attrList.put("givenName", datafields.get("arabicName").toString().split(" ")[0]);
				if (datafields.get("userCode") != null && !datafields.get("userCode").isEmpty())
					attrList.put("physicalDeliveryOfficeName", datafields.get("userCode"));
				if (datafields.get("jobTitle") != null && !datafields.get("jobTitle").isEmpty())
					attrList.put("title", datafields.get("jobTitle"));
				if (datafields.get("englishName") != null && !datafields.get("englishName").isEmpty())
					attrList.put("description", datafields.get("englishName"));
				if (datafields.get("company") != null && !datafields.get("company").isEmpty())
					attrList.put("company", datafields.get("company"));
				if (datafields.get("manager") != null && !datafields.get("manager").isEmpty()) {
					Map<String, String> managerData = checkLdabUserCode(datafields.get("manager"));
					if(managerData != null && managerData.size() > 0){
						attrList.put("manager", managerData.get("entry"));
					}
				}
				ModificationItem[] mods = new ModificationItem[attrList.size()];
				int modCount = 0;
				for (Map.Entry<String, String> entry : attrList.entrySet()) {
					if (entry.getValue() != null) {
						mods[modCount] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
								new BasicAttribute(entry.getKey(), entry.getValue()));
					}
					modCount++;
				}

				// Perform the update
				if(mods != null && mods.length > 0) {
					ctx = new InitialDirContext(getLdabEnv());
					ctx.modifyAttributes(entryPath, mods);
				}
				return null;
			} catch (Exception ex) {
				System.out.println("Problem modifing exist user on AD: " + ex.getMessage());
				ex.printStackTrace();
				return "Problem modifing exist user on AD: " + ex.getMessage();
			}
		}
	}
	
	public static Map<String, String> confirmDomainUser(Request userRow) throws Exception {
        String userCreated = null;
        String domainPwd = null;
        Map<String, String> userDataList = null;
        Map<String, String> resultList = new HashMap<String, String>();;
        int userModified = 0;
        try {
            //Get user data from current cmn users row.
            userDataList = getUserDataList(userRow);
            //1-First check user not exist by code ##2-create user and email in active directory (ldab API)
            List<String> userAccounts = getUserAccounts(userRow.getUserAccounts());
            //User is has one CMN Account and already exist on Active Directory
            Map<String, String> domainData = LdabUtils.checkLdabUserCode(userRow.getUserCode());
            if ( domainData != null) {
            	if(domainData.get("mailExist").equals("1")){
            		System.out.println("0- It is not possible to create an existing user on Active directory");
                    Map<String, String> errEmpData = new LinkedHashMap<String, String>();
                    errEmpData.put("Employee Code", "" + userRow.getUserCode());
                    errEmpData.put("Employee Name", "" + userRow.getEnglishName());
                    errEmpData.put("Page Code", "" + userRow.getPageCode());
                    errEmpData.put("Error Message", "It is not possible to create an existing user on Active directory");
                    errEmpData.put("Performed By", "Autocreation Service");
                    MailUtil.sendAdEmailError(errEmpData);
                    resultList.put("CreateFlag", "0");
                    resultList.put("operation", "Check User Existion");
                    resultList.put("outputMsg", "It is not possible to create an existing user on Active directory");
                    return resultList;
            	}else {
                    System.out.println("3- Mail Service:: Recreate mailbox for user " + domainData.get("SamAccountName") + " :: " + userDataList.get("UserCode") + " on mailbox database: " + userDataList.get("EmailDb"));
                    String createMailBocxResult = createMailBocx(domainData.get("userDomain") , userDataList.get("EmailDb"));
                    if(createMailBocxResult != null) {
                    	resultList.put("createMailBocxOut", createMailBocxResult);
                    }
                    resultList.put("CreateFlag", "3");
                    resultList.put("operation", "Create only MailBox");
                    resultList.put("outputMsg", "User domain exist and we create mailbox for user " +  userDataList.get("UserCode") + " on mailbox database: " + userDataList.get("EmailDb") );
                    sendConfirmEmail(userDataList, resultList.get("userDomain"), "3");
            	}
            } else if (domainData == null && (userAccounts != null && userAccounts.size() > 0 )) {//Modify domain with new UserCode if user has more cmn accounts
            	System.out.println("2- Check UserCode if has an exist user domain");
            	for (int i = 0; i < userAccounts.size(); i++) {
                    //return user domain data as list if exist on Active Directory
                    Map<String, String> domainDataList = LdabUtils.checkLdabUserCode(userAccounts.get(i));
                    if(domainDataList != null && domainDataList.get("userDomain") != null){
                        String entryPath = domainDataList.get("entry");
                        String slectedDomain = domainDataList.get("userDomain");
                        System.out.println("SlectedDomain: " + slectedDomain);
                        if (slectedDomain != null) {
                        	System.out.println("2- Modify an existing user to new UserCode");
                        	Map<String, String> attrList = new HashMap<String, String>();
                        	attrList.put("userCode", userRow.getUserCode());
                            String modifyError = modifyDomainUser(userRow.getUserCode(), attrList, entryPath);
                            resultList = new HashMap<String, String>();
                            if( modifyError != null) {
                            	resultList.put("errorExist", "1");
                                resultList.put("errorMsg", modifyError);
                                resultList.put("operation", "Modify User Code");
                            }else {
                            	resultList.put("userDomain", slectedDomain);
                                resultList.put("CreateFlag", "2");
                                resultList.put("operation", "Modify User Code");
                                resultList.put("outputMsg", "modify exist domain: " + slectedDomain + " From UserCode: " + userAccounts.get(i) + " to new UserCode: " + userRow.getUserCode());
                                if(domainDataList != null && domainDataList.get("mailExist").equals("0")) {
                                	String createMailBocxResult = createMailBocx(slectedDomain , userDataList.get("EmailDb"));
                                    if(createMailBocxResult != null) {
                                    	resultList.put("createMailBocxOut", createMailBocxResult);
                                    }
                                }
                                userModified = 1;
                            }
                            
                        }
                        break;
                    }
                   
                }
                if(userModified == 0 && (resultList == null || resultList.get("errorExist") == null  || !resultList.get("errorExist").equals("1"))) {
                	System.out.println("1- create new domain user: ");
                    resultList = LdabUtils.createLdabUser(userDataList);
                    resultList.put("CreateFlag", "1");
                    resultList.put("operation", "Create New Domain User");
                    resultList.put("outputMsg", "User and his accounts not exist, We Create new domain for user: " +  userRow.getUserCode());
                }
            } else {//Create New User if User is new and not exist on Active Directory
                System.out.println("1- create new domain user: ");
                resultList = LdabUtils.createLdabUser(userDataList);
                resultList.put("CreateFlag", "1");
                resultList.put("operation", "Create New Domain User");
                resultList.put("outputMsg", "Create new domain for user: " +  userRow.getUserCode());
            }

            if (resultList != null && resultList.size() > 0 && resultList.get("userDomain") != null &&
                (resultList.get("errorExist") == null || !resultList.get("errorExist").equals("1"))) {
                userCreated = resultList.get("userDomain");
                domainPwd = resultList.get("domainPwd");

                //Send Confirmation Email to AD Admin with new user details.
                sendConfirmEmail(userDataList, userCreated, resultList.get("CreateFlag"));
                return resultList;
            } else {
                String errorTxt = resultList.get("errorMsg") != null ? resultList.get("errorMsg") : "User Data CONFIRM ERROR and couldn't create domain";
                System.out.println("USER_DATA_CONFIRM FAIL: " + errorTxt);
                resultList.put("errorExist", "1");
                resultList.put("errorMsg", errorTxt);
                Map<String, String> errEmpData = new LinkedHashMap<String, String>();
                errEmpData.put("Employee Code", "" + userRow.getUserCode());
                errEmpData.put("Employee Name", "" + userDataList.get("FullEnName"));
                errEmpData.put("Error Message", errorTxt);
                errEmpData.put("Page Code", "" + userRow.getPageCode());
                errEmpData.put("Performed By", "Autocreation Service");
                MailUtil.sendAdEmailError(errEmpData);
                return resultList;
            }
        } catch (Exception ex) {
            System.out.println("USER_DATA_CONFIRM Exception: " + ex.getMessage());
            Map<String, String> errEmpData = new LinkedHashMap<String, String>();
            errEmpData.put("Employee Code", "" + userRow.getUserCode());
            errEmpData.put("Employee Name", "" + userDataList.get("FullEnName"));
            errEmpData.put("Error Message", ex.getMessage());
            errEmpData.put("Page Code", "" + userRow.getPageCode());
            errEmpData.put("Performed By", "Autocreation Service");
            MailUtil.sendAdEmailError(errEmpData);
            ex.printStackTrace();
            resultList = new HashMap<String, String>();
            resultList.put("errorExist", "1");
            resultList.put("errorMsg", ex.getMessage());
            resultList.put("operation", "Throw Exception");
            return resultList;
        }

    }
	
	public static List<String> getUserAccounts(String userAccounts) {
		List<String> accounts = new ArrayList<String>();
		if(userAccounts != null && !userAccounts.isEmpty()) {	
			accounts = Arrays.asList(userAccounts.split(","));
		}
		return accounts;
	}

	public static Map<String, String> getUserDataList(Request userRow) {
        Map<String, String> userDataList = new HashMap<String, String>();
        userDataList.put("UserCode", (String) userRow.getUserCode());
        userDataList.put("UserName", (String) userRow.getArabicName());
        userDataList.put("FullEnName", (String) userRow.getEnglishName());
        userDataList.put("UserMobileNo", (String) userRow.getMobileNo());
        userDataList.put("DirDesc", (String) userRow.getDirDesc());
        userDataList.put("EmployeeJob", (String) userRow.getJobTitle());
        userDataList.put("EmailDb", (String) userRow.getMailBox());
        userDataList.put("DomainPwd", (String) userRow.getDomainPwd());
        userDataList.put("company", (String) userRow.getCompany());
        userDataList.put("manager", (String) userRow.getManager());
        userDataList.put("PageCode", (String) userRow.getPageCode());
        return userDataList;
    }
	
    public static void sendConfirmEmail(Map<String, String> userDataList, String domainUser, String createFlag){
        //Perpare Confirm Email Data.
        Map<String, String> empData = new LinkedHashMap<String, String>();
        if(createFlag.equals("1")){
            empData.put("Employee Note", "New user created on Active Directory environment with the following details");
        }
        if(createFlag.equals("2")){
            empData.put("Employee Note", "Modified Exist User Code From: " + userDataList.get("oldUserCode") + " To: "+ userDataList.get("UserCode"));
        }
        if(createFlag.equals("3")){
            empData.put("Employee Note", "Create the MailBox only For Exist User " + userDataList.get("UserCode"));
        }
        empData.put("Employee Ar Name", "" + userDataList.get("UserName"));
        empData.put("Employee En Name", userDataList.get("FullEnName"));
        empData.put("Employee Code", userDataList.get("UserCode"));
        empData.put("Domain Name", domainUser);
        empData.put("Dir Name", userDataList.get("DirDesc"));
        empData.put("Mobile No", "" + userDataList.get("UserMobileNo"));
        empData.put("Employee Job", userDataList.get("EmployeeJob"));
        empData.put("Email Address", domainUser + "@alriyadh.gov.sa");
        empData.put("AD Database", userDataList.get("EmailDb"));
        empData.put("Performed By", "Autocreation Service");
        MailUtil.sendAdAutocreationEmail(empData);
    }
	
    public static String checkUserDomain(String fullEnglishName) throws Exception {
        String userDomainName = null;
        List<String> checkNameList = new ArrayList<String>();
        String[] fullnameArray = null;
        try {
            //1-delete specialcase chars & spaces & masks.
            if (fullEnglishName.contains(" al-")) {
                fullEnglishName = fullEnglishName.replace(" al-", "al");
            }
            if (fullEnglishName.contains(" al ")) {
                fullEnglishName = fullEnglishName.replace(" al ", " al");
            }
            if (fullEnglishName.contains(" bin ")) {
                fullEnglishName = fullEnglishName.replace(" bin ", " ");
            }
            if (fullEnglishName.contains("abdul ")) {
                fullEnglishName = fullEnglishName.replace("abdul ", "abdul");
            }
            if (fullEnglishName.contains("abdel ")) {
                fullEnglishName = fullEnglishName.replace("abdel ", "abdel");
            }
            if (fullEnglishName.contains("  ")) {
                fullEnglishName = fullEnglishName.replace("  ", " ");
            }
            if (fullEnglishName.contains("'")) {
                fullEnglishName = fullEnglishName.replace("'", "");
            }
            if (fullEnglishName.contains(" -")) {
                fullEnglishName = fullEnglishName.replace(" -", "");
            }
            System.out.println("fullEnglishName :: " + fullEnglishName);
            List<String> nameList = new ArrayList<String>(Arrays.asList(fullEnglishName.split(" ")));
            if (nameList != null && nameList.size() == 2) {
                nameList.add(nameList.get(1));
            }
            if (nameList != null && nameList.size() == 3) {
                nameList.add(nameList.get(2));
            }
            if (nameList != null && nameList.size() > 1) {
                fullnameArray = new String[nameList.size()];
                nameList.toArray(fullnameArray);
            } else {
                fullnameArray = fullEnglishName.split(" ");
            }
            String famillyName = fullnameArray[fullnameArray.length - 1];

            //UpperCase first char of famillyName.
            famillyName = famillyName.substring(0, 1).toUpperCase() + famillyName.substring(1);
            //System.out.println("famillyName :: "+famillyName);
            //UpperCase first char of all fullEnglishName.
            for (int i = 0; i < fullnameArray.length; i++) {
                fullnameArray[i] = fullnameArray[i].toUpperCase();
            }
            //Mohamed ALi Shokry Shehata
            //#Case1 : MShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) + famillyName);

            //#Case2 : MAShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) + fullnameArray[1].charAt(0) + famillyName);

            //#Case3 : MASShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) + fullnameArray[1].charAt(0) +
                              fullnameArray[2].charAt(0) + famillyName);

            //#Case4 : MOShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) +
                              (fullnameArray[0].length() > 1 ? fullnameArray[0].charAt(1) : "") + famillyName);

            //#Case5 : MOHShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) +
                              (fullnameArray[0].length() > 1 ? fullnameArray[0].charAt(1) : "") +
                              (fullnameArray[0].length() > 2 ? fullnameArray[0].charAt(2) : "") + famillyName);

            //#Case6 : MOHAShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) +
                              (fullnameArray[0].length() > 1 ? fullnameArray[0].charAt(1) : "") +
                              (fullnameArray[0].length() > 2 ? fullnameArray[0].charAt(2) : "") +
                              (fullnameArray[0].length() > 3 ? fullnameArray[0].charAt(3) : "") + famillyName);

            //#Case7 : MoASShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) +
                              (fullnameArray[0].length() > 1 ? fullnameArray[0].charAt(1) :
                               fullnameArray[0].charAt(0)) + fullnameArray[1].charAt(0) + fullnameArray[2].charAt(0) +
                              famillyName);

            //#Case8 : MoAlSShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) +
                              (fullnameArray[0].length() > 1 ? fullnameArray[0].charAt(1) :
                               fullnameArray[0].charAt(0)) + fullnameArray[1].charAt(0) + fullnameArray[1].charAt(1) +
                              fullnameArray[2].charAt(0) + famillyName);

            //#Case9 : MoAlShShehata
            checkNameList.add("" + fullnameArray[0].charAt(0) +
                              (fullnameArray[0].length() > 1 ? fullnameArray[0].charAt(1) :
                               fullnameArray[0].charAt(0)) + fullnameArray[1].charAt(0) +
                              (fullnameArray[1].length() > 1 ? fullnameArray[1].charAt(1) :
                               fullnameArray[1].charAt(0)) + fullnameArray[2].charAt(0) +
                              (fullnameArray[2].length() > 1 ? fullnameArray[2].charAt(1) :
                               fullnameArray[2].charAt(0)) + famillyName);

            //2-Create Email domain name.
            for (int i = 0; i < checkNameList.size(); i++) {
                //System.out.println(checkNameList.get(i));
                if (!checkLdabUser(checkNameList.get(i))) {
                    userDomainName = checkNameList.get(i);
                    System.out.println("user userDomainName : " + userDomainName);
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return userDomainName;
    }
	
	 public static String generateRandomPassword() {
	        String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	        String DIGIT = "0123456789";
	        String OTHER_SYMBOL = "!@";
	        String strLowerCase = generateRandomString(CHAR_LOWERCASE, 4);
	        String strDigit = generateRandomString(DIGIT, 4);
	        String pwdString = strLowerCase + OTHER_SYMBOL + strDigit;
	        return (pwdString.substring(0, 1).toUpperCase() + pwdString.substring(1));
	    }
	    
	 // generate a random char[], based on `input`
	    private static String generateRandomString(String input, int size) {

	        if (input == null || input.length() <= 0)
	            throw new IllegalArgumentException("Invalid input.");
	        if (size < 1)
	            throw new IllegalArgumentException("Invalid size.");

	        StringBuilder result = new StringBuilder(size);
	        for (int i = 0; i < size; i++) {
	            // produce a random order
	            int index = random.nextInt(input.length());
	            result.append(input.charAt(index));
	        }
	        return result.toString();
	    }

}
