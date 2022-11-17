package com.app.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.github.frimtec.libraries.jpse.PowerShellExecutor;



public class TestCode {

//	public static void main(String[] args) {
//		
//		execShell();
//
//	}
	
	
    public static void execShell() {
    	try {
    		String script =  "$pass=\")@$#893jofdLKJFDL\"|ConvertTo-SecureString -AsPlainText -Force ; if ($?) {$UserCredential = New-Object   System.Management.Automation.PsCredential('ADF@itamana.net',$pass)} ; if ($?) {$Session = New-PSSession -ConfigurationName Microsoft.Exchange -ConnectionUri http://exg19-01.itamana.net/PowerShell/ -Credential $UserCredential -Authentication kerberos -AllowRedirection } ; if ($?) {Import-PSSession $Session -AllowClobber } ; if ($?) {Get-MailboxDatabase | ft name}";
    		PowerShellExecutor executor = PowerShellExecutor.instance();
    		System.out.println("PowerShell runtime version " +
    		   executor.version().orElseThrow(() -> new RuntimeException("No PowerShell runtime available")));

    		System.out.println("Execute command: ");
    		String output = executor.execute(script).getStandardOutput();
    		System.out.println(" output = " + output);
    	}catch(Exception ex) {
    		ex.printStackTrace();
    		
    	}
    }

}
