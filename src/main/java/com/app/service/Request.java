package com.app.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Request {
	
	    private String domainUser;
	    private String domainPwd;
	    private String mailBox;	
	    private String englishName;
	    private String arabicName;
	    private String dirCode;
	    private String dirDesc;
	    private String mobileNo; 
	    private String userCode; // UserCode
	    private String IdentificationNo;
	    private String IdTypeCode;
	    private String birthDateH;
	    private String jobTitle; // Job Description
	    private String company;
	    private String userAccounts; //if user has more UserCode (say 90000055, 9005578, 90005564 >> this is the same user)
	    private String manager;
	    private String pageCode;  //Client page code that sent the request
	    
		public String getDomainUser() {
			return domainUser;
		}
		public void setDomainUser(String domainUser) {
			this.domainUser = domainUser;
		}
		public String getDomainPwd() {
			return domainPwd;
		}
		public void setDomainPwd(String domainPwd) {
			this.domainPwd = domainPwd;
		}
		public String getMailBox() {
			return mailBox;
		}
		public void setMailBox(String mailBox) {
			this.mailBox = mailBox;
		}
		public String getEnglishName() {
			return englishName;
		}
		public void setEnglishName(String englishName) {
			this.englishName = englishName;
		}
		public String getArabicName() {
			return arabicName;
		}
		public void setArabicName(String arabicName) {
			this.arabicName = arabicName;
		}
		public String getDirCode() {
			return dirCode;
		}
		public void setDirCode(String dirCode) {
			this.dirCode = dirCode;
		}
		public String getDirDesc() {
			return dirDesc;
		}
		public void setDirDesc(String dirDesc) {
			this.dirDesc = dirDesc;
		}
		public String getMobileNo() {
			return mobileNo;
		}
		public void setMobileNo(String mobile) {
			this.mobileNo = mobile;
		}
		public String getUserCode() {
			return userCode;
		}
		public void setUserCode(String userCode) {
			this.userCode = userCode;
		}
		public String getIdentificationNo() {
			return IdentificationNo;
		}
		public void setIdentificationNo(String identificationNo) {
			IdentificationNo = identificationNo;
		}
		public String getIdTypeCode() {
			return IdTypeCode;
		}
		public void setIdTypeCode(String idTypeCode) {
			IdTypeCode = idTypeCode;
		}
		public String getBirthDateH() {
			return birthDateH;
		}
		public void setBirthDateH(String birthDateH) {
			this.birthDateH = birthDateH;
		}
		public String getJobTitle() {
			return jobTitle;
		}
		public void setJobTitle(String jobTitle) {
			this.jobTitle = jobTitle;
		}
		
		public String getCompany() {
			return company;
		}
		public void setCompany(String company) {
			this.company = company;
		}
		
/*		public List<String> getUserAccounts() {
			List<String> accounts = new ArrayList<String>();
			if(userAccounts != null && !userAccounts.isEmpty()) {	
				accounts = Arrays.asList(userAccounts.split(","));
			}
			return accounts;
		}*/
		
		public String getUserAccounts() {
			return userAccounts;
		}
		
		public void setUserAccounts(String userAccounts) {
			this.userAccounts = userAccounts;
		}
		
		public String getManager() {
			return manager;
		}
		public void setManager(String manager) {
			this.manager = manager;
		}
		
		public String getPageCode() {
			return pageCode;
		}
		public void setPageCode(String pageCode) {
			this.pageCode = pageCode;
		}
		@Override
		public String toString() {
			return "Request [domainUser=" + domainUser + ", domainPwd=" + domainPwd + ", mailBox=" + mailBox
					+ ", englishName=" + englishName + ", arabicName=" + arabicName + ", dirCode=" + dirCode
					+ ", dirDesc=" + dirDesc + ", mobileNo=" + mobileNo + ", userCode=" + userCode
					+ ", IdentificationNo=" + IdentificationNo + ", IdTypeCode=" + IdTypeCode + ", birthDateH="
					+ birthDateH + ", jobTitle=" + jobTitle + ", company=" + company + ", userAccounts=" + userAccounts
					+ ", manager=" + manager + ", pageCode=" + pageCode + "]";
		}
		
		
}
