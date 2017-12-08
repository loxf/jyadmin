package org.loxf.jyadmin.base.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Consts;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.loxf.jyadmin.base.exception.JyException;

/**
 * http工具类
 */
public final class HttpsUtil {
	// 超时时间
	public static final String defaultTimeOut = "120";

	public static void main(String[] args) {
		String Url = "https://abc.html";
		try {
			String getResult = doHttpsGet(Url, null, null);
			String postResult = doHttpsPost(Url, null, null, null);
			System.out.println("_______________get:" + getResult);
			System.out.println("_______________post:" + postResult);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * post请求
	 *
	 * @param url
	 * @param values
	 * @param cookie
	 * @param refer
	 * @return
	 * @throws IOException
	 */
	public static String doHttpsPost(String url, List<NameValuePair> values, String cookie, String refer) throws IOException {
		String result = null;

		HttpPost post = new HttpPost(url);
		CloseableHttpClient httpClient = buildHttpClient();
		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Integer.valueOf(defaultTimeOut) * 1000).setConnectTimeout(Integer.valueOf(defaultTimeOut) * 1000).build();
		post.setConfig(requestConfig);

		if (cookie != null) {
			post.setHeader("Cookie", cookie);
		}
		if (refer != null) {
			post.setHeader("Referer", refer);
		}
		if (values != null) {
			UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(values, Consts.UTF_8);
			post.setEntity(formEntity);
		}

		CloseableHttpResponse response = httpClient.execute(post);
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 携带一个params数据发送Post请求到指Url
	 */
	public static String handlePost(String url, String jsonStr, Map<String, Object> headerMap) throws Exception {
		if (headerMap == null) {
			headerMap = new HashMap<String, Object>();
		}

		HttpPost httpPost = new HttpPost(url);
		CloseableHttpClient httpClient = buildHttpClient();

		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Integer.valueOf(defaultTimeOut) * 1000).setConnectTimeout(Integer.valueOf(defaultTimeOut) * 1000).build();
		httpPost.setConfig(requestConfig);

		StringEntity param = new StringEntity(jsonStr, "UTF-8");
		param.setContentType("application/json");//发送json数据需要设置contentType
		httpPost.setEntity(param);

		CloseableHttpResponse response = httpClient.execute(httpPost);
		String result = null;
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				//把响应结果转成String
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			} else {
				throw new JyException(response.getStatusLine().getReasonPhrase());
			}
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}
	/**
	 * get请求
	 *
	 * @param url
	 * @param cookie
	 * @param refer
	 * @return
	 * @throws IOException
	 */
	public static String doHttpsGet(String url, String cookie, String refer) throws IOException {
		String result = null;

		HttpGet get = new HttpGet(url);
		CloseableHttpClient httpClient = buildHttpClient();
		// 设置请求和传输超时时间
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(Integer.valueOf(defaultTimeOut) * 1000).setConnectTimeout(Integer.valueOf(defaultTimeOut) * 1000).build();
		get.setConfig(requestConfig);

		if (cookie != null) {
			get.setHeader("Cookie", cookie);
		}
		if (refer != null) {
			get.setHeader("Referer", refer);
		}

		CloseableHttpResponse response = httpClient.execute(get);
		try {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), "UTF-8");
			}
			return result;
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	/**
	 * 构建CloseableHttpClient
	 *
	 * @return
	 */
	private static CloseableHttpClient buildHttpClient() {
		enableSSL();
		RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).setExpectContinueEnabled(true).setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST)).setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC)).build();
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory> create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", socketFactory).build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(defaultRequestConfig).build();
		return httpClient;
	}

	/**
	 * https网站一般情况下使用了安全系数较低的SHA-1签名，因此首先我们在调用SSL之前需要重写验证方法，取消检测SSL。
	 */
	private static TrustManager manager = new X509TrustManager() {
		@Override
		public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

		}

		@Override
		public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
	};

	/**
	 * @Description:调用SSL
	 */
	private static void enableSSL() {
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { manager }, null);
			socketFactory = new SSLConnectionSocketFactory(context, NoopHostnameVerifier.INSTANCE);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 连接工厂
	 */
	private static SSLConnectionSocketFactory socketFactory;

	public HttpsUtil() {

	}
}
