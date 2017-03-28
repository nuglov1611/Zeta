package loader.protocols;

import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import loader.Protocol;
import loader.ZetaProperties;
import loader.ZetaUtility;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.cache.CacheConfig;
import org.apache.http.impl.client.cache.CachingHttpClient;
import org.apache.http.impl.client.cache.ehcache.EhcacheHttpCacheStorage;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.log4j.Logger;

import properties.PropertyConstants;
import properties.PropertyManager;


public class HTTP implements Protocol {
	
	
    private static final Logger log      = Logger.getLogger(HTTP.class);

    private String link = "";
    private String              login    = "";

    private String              password = "";

    boolean                     needAuth = false;

    private static Cache cache = null;
    private static CacheManager cm = null;

    static{
        if (PropertyManager.getIntance().getProperty(PropertyConstants.USE_CACHE).toUpperCase().equals("ON")){
            String path = PropertyManager.getIntance().getProperty(PropertyConstants.CACHE_PATH);
            if ("".equals(path)){
                path = null;
            }
            cm = CacheManager.create();
            cache = new Cache(new CacheConfiguration("ZetaCache", 0)
                    .maxEntriesLocalHeap(1)
                    .overflowToDisk(true)
                 //   .eternal(true)
                    .diskPersistent(true)
                    //.diskStorePath(path)
                    .logging(true));
            cache.getCacheConfiguration().setLogging(true);
            
            cm.addCache(cache);
        }
    }

    public HTTP(String l, String s_login, String s_password) throws URISyntaxException, NoSuchAlgorithmException {
        
        link = l.trim();
        
        if(!link.endsWith("/")){
            link = link + "/";
        }
        if (s_login != null) {
            login = s_login;
            needAuth = true;
        }
        if (s_password != null) {
            password = s_password;
        }
    }

    public byte[] getByName_bytes(String file) throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {

    	TrustManager easyTrustManager = new X509TrustManager() {

    	    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    	    }

    	    public void checkServerTrusted(X509Certificate[] chain,String authType) throws CertificateException {
    	    }

    	    public X509Certificate[] getAcceptedIssuers() {
    	        return null;
    	    }

    	};	
    	
    	byte[] res = null;

        final DefaultHttpClient dclient = new DefaultHttpClient();
        HttpClient client;
        if (PropertyManager.getIntance().getProperty(PropertyConstants.USE_CACHE).toUpperCase().equals("ON")){
            client = new CachingHttpClient(dclient, new EhcacheHttpCacheStorage(cache), new CacheConfig());
        	//client = new CachingHttpClient(dclient);
        } else {
            client = dclient;
        }
        final ProxySelectorRoutePlanner routePlanner = new ProxySelectorRoutePlanner(
                client.getConnectionManager().getSchemeRegistry(),
                ProxySelector.getDefault());  
        dclient.setRoutePlanner(routePlanner);

        SSLContext sslcontext = SSLContext.getInstance("TLS");
        sslcontext.init(null, new TrustManager[] { easyTrustManager }, null);
        SSLSocketFactory sf = new SSLSocketFactory(sslcontext); 
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        Scheme https = new Scheme("https", sf, 443);
        client.getConnectionManager().getSchemeRegistry().register(https);
        
        
        final HttpGet get = new HttpGet(link+file);

        boolean retry = true;
        try{
	        while(retry){
	        	retry = false;
		        if (needAuth) {
		            dclient.getCredentialsProvider().setCredentials(new AuthScope(null, -1),
		                    new UsernamePasswordCredentials(login, password));
		        }
		
		        try {
		        	final HttpResponse resp = client.execute(get);
		        	final HttpEntity entity = resp.getEntity();
		        	int errCode =checkErrorCode(resp.getStatusLine()); 
		        	
		        	if(errCode == Constants.HTTP_STOP){
		        		return null;
		        	}else if(errCode == Constants.HTTP_RETRY){
		        		retry = true;
			            if (entity != null) {
			                entity.consumeContent();
			            }
		        		continue;
		        	}
		        	
		        	
		            log.debug("Responce status: "+resp.getStatusLine());
		            if (entity != null) {
		                log.debug("Response content length: " + entity.getContentLength());
		                int content_length = (int) entity.getContentLength();
		                res = new byte[content_length];
		                int cur_read = 0;
		                int total_read = 0;
		                InputStream is = entity.getContent();
		                while(total_read < content_length){
		                	cur_read = is.read(res, total_read, content_length-total_read);
		                	if(cur_read == 0 || cur_read == -1)
		                		break;
		                	else
		                		total_read += cur_read;
		                }
		            }
		            if (entity != null) {
		                entity.consumeContent();
		            }
		        } catch (ClientProtocolException e) {
					log.error("HTTP loader error! ", e);
				} catch (IOException e) {
					log.error("HTTP loader error! ", e);
				}
	        };
        }
        finally {
        	client.getConnectionManager().shutdown();
        }
        return res;
    }

    private boolean getAuthorization(){
    	
    	login = ZetaUtility.input("Введите логин").toString();
    	password = ZetaUtility.input("Введите пароль").toString();
    	if(login != null && login.trim() != "" && password != null && password.trim()!=""){
    		needAuth = true;
    		return true;
    	}
    	
    	return false;
    }
    
    private int checkErrorCode(StatusLine err) {
    	int res = 0;
    	if(err.getStatusCode() == HttpStatus.SC_UNAUTHORIZED){//требуется авторизация на сервере
    		if(ZetaUtility.sure("Ошибка загрузки документа (HTTP: "+err+") /nХотите ввести логин//пароль?", ZetaProperties.MESSAGE_SURE))
    		{
    			if(getAuthorization())
    				res = Constants.HTTP_RETRY;
    		}else{
				res = Constants.HTTP_STOP;
    		}
    	}
    	else if(err.getStatusCode() == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED){//требуется авторизация на прокси
    	   ZetaUtility.message("Ошибка загрузки документа (HTTP: "+err+")", "Необходима аутентификация прокси", ZetaProperties.MESSAGE_ERROR);	
    	   res = Constants.HTTP_STOP;
    	}
    	else if(Math.abs(err.getStatusCode()/100) >= 4){ //4xx and 5xx - Client and server errors
     	   ZetaUtility.message("Ошибка загрузки документа",  "HTTP: "+err, ZetaProperties.MESSAGE_ERROR);	

     	   res = Constants.HTTP_STOP;
    	}
    	return res;
    }
    
    public char[] getByName_chars(String path, boolean enc) throws Exception {
        try {
            byte[] b = getByName_bytes(path);
            char[] text = new char[b.length];

            if (enc) {
                int foo = 0;
                for (; (foo < b.length) && (b[foo] != '\n') && (b[foo] != '\r'); ++foo) {
                }

                String encoding = new String(b, 0, foo);

                if (ZetaProperties.protocol_debug > 1) {
                    log.debug("Document encoding " + encoding);
                }

                text = new String(b, foo + 1, b.length - 1 - foo, encoding)
                        .toCharArray();
                if (ZetaProperties.protocol_debug > 1) {
                    log.debug(new String(text));
                }
            }
            else
                text = new String(b).toCharArray();
            return text;

        }
        catch (Exception e) {
            if (ZetaProperties.loader_exception) {
                log.error("Can't load data from URL", e);
            }
            throw new Exception();
        }
    }
}
