package com.app.service.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

public class CustomLdapSslSocketFactory extends SSLSocketFactory
{
    //private static final String CERT_FILE = "/u02/oracle/config/domains/prdadf122/security/CArrmappstg.cer"; //UAT & Stage
    private static final String CERT_FILE = "C:\\u01\\oracle\\config\\certification\\prdws12\\KMSSRV-CA.cer";  //Production
    private SSLSocketFactory sslSocketFactory;
    
    private static volatile CustomLdapSslSocketFactory singletonCustLdapSslSockFact;
    
        
    private CustomLdapSslSocketFactory() throws KeyManagementException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, IOException
    {   
        sslSocketFactory = loadTrustStoreProgrammatically();
    }
    
        
    private static CustomLdapSslSocketFactory getSingletonInstance() throws KeyManagementException, KeyStoreException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, IOException
    {
        if(CustomLdapSslSocketFactory.singletonCustLdapSslSockFact == null)
        {
            synchronized(CustomLdapSslSocketFactory.class)
            {
                if(CustomLdapSslSocketFactory.singletonCustLdapSslSockFact == null)
                {
                    CustomLdapSslSocketFactory.singletonCustLdapSslSockFact = new CustomLdapSslSocketFactory();
                }
            }
        }
        
        return CustomLdapSslSocketFactory.singletonCustLdapSslSockFact;
    }
    
        
    

    public static SocketFactory getDefault() //this method is called by Ldap implementations to create the custom SSL socket factory. See: https://docs.oracle.com/javase/jndi/tutorial/ldap/security/ssl.html 
    {
        /*
        There are times when you need to have more control over the SSL sockets, or sockets in general, used by the LDAP service provider.
        To set the socket factory implementation used by the LDAP service provider, set the "java.naming.ldap.factory.socket" property to the
        fully qualified class name of the socket factory.
        This class must extend the javax.net.SocketFactory abstract class and provide an implementation of the getDefault() method that
        returns an instance of the custom socket factory.
        See:
        https://docs.oracle.com/javase/jndi/tutorial/ldap/security/ssl.html
        */
        CustomLdapSslSocketFactory custLdapSslSockFact = null;
        
        try
        {
            //custLdapSslSockFact = new CustomLdapSslSocketFactory(); //returns a new instance each time
            custLdapSslSockFact = CustomLdapSslSocketFactory.getSingletonInstance(); //returns the same instance each time (singleton pattern)
        }
        catch(Exception e)
        {
            throw new RuntimeException("Failed create CustomSslSocketFactory. Exception: " + e.getClass().getSimpleName() + ". Reason: " + e.getMessage(), e);
        }
        
        return custLdapSslSockFact;
    }
            

    //1- LDAP Load certificate by Path....
    private SSLSocketFactory loadTrustStoreProgrammatically() throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException
    {  
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        File certFile = new File(CERT_FILE);
        System.out.println("Certificate file Path"+ certFile.getPath());
        trustStore.setCertificateEntry("certnew",getCertificateFile(certFile));
        
        // initialize a trust manager factory with the trusted store
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());    
        trustFactory.init(trustStore);

        // get the trust managers from the factory
        TrustManager[] trustManagers = trustFactory.getTrustManagers();

        // initialize an ssl context to use these managers
        SSLContext sslContext = SSLContext.getInstance("SSL"); //.getInstance("SSL"); or TLS, etc.
        sslContext.init(null, trustManagers, null);
        
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        
        return sslSocketFactory;
    }
    
    //2- LDAP Load certificate by call Trust KeyStore....
    /*private SSLSocketFactory loadTrustStoreProgrammatically() throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, KeyManagementException, CertificateException
    {
        SSLSocketFactory sslSocketFactory = null;
        try{
            System.out.println("*** inside loadTrustStoreProgrammatically.................");
            //Now, reference the custom user-defined system properties defined in your ldap query method above.

            // Create a new trust store, use getDefaultType for .jks files or "pkcs12" for .p12 files
            InitialContext ic = new InitialContext();
            MBeanServer server = (MBeanServer) ic.lookup("java:comp/env/jmx/runtime");

            // Get access to server configuration
            ObjectName runtime = new ObjectName("com.bea:Name=RuntimeService,Type=weblogic.management.mbeanservers.runtime.RuntimeServiceMBean");
            ObjectName serverConfig = (ObjectName) server.getAttribute(runtime, "ServerConfiguration");

            // Passphrase as clear text
            Object keyStoreFileName = server.getAttribute(serverConfig, "CustomTrustKeyStoreFileName");
            //Object keyStorePassPhrase = server.getAttribute(serverConfig, "CustomTrustKeystorePassPhrase");
            
            String keyPassword = "P@ssW0rd";//"Welcome1$";
            
            // Load keystore
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            System.out.println("*** start loaaaaaaaaaaaaaaaaaaaaaaaaaaad trustStore.................");
            trustStore.load(new FileInputStream(keyStoreFileName.toString()),
                    keyPassword.toCharArray());
            
            // initialize a trust manager factory with the trusted store
            TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());    
            trustFactory.init(trustStore);

            // get the trust managers from the factory
            TrustManager[] trustManagers = trustFactory.getTrustManagers();

            // initialize an ssl context to use these managers
            SSLContext sslContext = SSLContext.getInstance("SSL"); //.getInstance("SSL"); or TLS, etc.
            sslContext.init(null, trustManagers, null);
            
            sslSocketFactory = sslContext.getSocketFactory();
            System.out.println("*** End loaaaaaaaaaaaaaaaaaaaaaaaaaaad trustStore.................");
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        
        return sslSocketFactory;
    }*/

    
    private static X509Certificate getCertificateFile(File certificateFile) throws IOException, CertificateException {
            try (FileInputStream inputStream = new FileInputStream(certificateFile)) {
                return (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(inputStream);
            }
        }
    
    private static SSLSocketFactory createSslSocketFactory(KeyStore trustStore) throws GeneralSecurityException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        TrustManager[] trustManagers = tmf.getTrustManagers();

        SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustManagers, null);
        return sslContext.getSocketFactory();
    }
    
    
    
    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
    {
        return sslSocketFactory.createSocket(s, host, port, autoClose);
    }

    @Override
    public String[] getDefaultCipherSuites()
    {
        return sslSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites()
    {
        return sslSocketFactory.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException
    {
        return sslSocketFactory.createSocket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException
    {
        return sslSocketFactory.createSocket(host, port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException
    {
        return sslSocketFactory.createSocket(localHost, port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException
    {
        return sslSocketFactory.createSocket(address, port, localAddress, localPort);
    }
}
