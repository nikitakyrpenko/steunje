package com.negeso.framework.util.http;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class UnsecuredHttpClient {

	public static HttpClient verifiedClient(HttpClient base) {
		try {
			SSLContext ctx = SSLContext.getInstance("SSL");
			X509TrustManager tm = new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				@Override
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
				@Override
				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
			};

			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			ClientConnectionManager mgr = base.getConnectionManager();
			SchemeRegistry registry = mgr.getSchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
			return new DefaultHttpClient(mgr, base.getParams());
		} catch (Exception ex) {
			ex.printStackTrace();
			return base;
		}
	}
}
