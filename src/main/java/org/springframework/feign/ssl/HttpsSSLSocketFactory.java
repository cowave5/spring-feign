package org.springframework.feign.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author shanhm1991@163.com
 *
 */
public class HttpsSSLSocketFactory extends SSLSocketFactory implements X509TrustManager, X509KeyManager {

	private final SSLSocketFactory sslSocket;

	public HttpsSSLSocketFactory() throws Exception {
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		// 忽略证书校验
        TrustManager[] trustAllCertificates = new TrustManager[]{
            new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;  // 接受所有证书
                }
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    // 忽略客户端证书校验
                }
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    // 忽略服务器证书校验
                }
            }
        };
		sslcontext.init(null, trustAllCertificates, new SecureRandom());
		sslSocket = sslcontext.getSocketFactory();
	}

	public HttpsSSLSocketFactory(String sslCertPath, String sslPasswd) throws Exception {
		try (InputStream certiInput = HttpsSSLSocketFactory.class.getResourceAsStream(sslCertPath)) {
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			keyStore.load(certiInput, sslPasswd.toCharArray());

			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, sslPasswd.toCharArray());

			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{this}, new SecureRandom());
			sslSocket = sslcontext.getSocketFactory();
		}
	}

	@Override
	public String[] getDefaultCipherSuites() {
		return null;
	}

	@Override
	public String[] getSupportedCipherSuites() {
		return null;
	}

	@Override
	public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
		return SSLSocket.class.cast(sslSocket.createSocket(s, host, port, autoClose));
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
		return SSLSocket.class.cast(sslSocket.createSocket(host, port));
	}

	@Override
	public Socket createSocket(InetAddress host, int port) throws IOException {
		return SSLSocket.class.cast(sslSocket.createSocket(host, port));
	}

	@Override
	public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
		return SSLSocket.class.cast(sslSocket.createSocket(host, port, localHost, localPort));
	}

	@Override
	public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
		return SSLSocket.class.cast(sslSocket.createSocket(address, port, localAddress, localPort));
	}

	@Override
	public String[] getClientAliases(String keyType, Principal[] issuers) {
		return null;
	}

	@Override
	public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
		return null;
	}

	@Override
	public String[] getServerAliases(String keyType, Principal[] issuers) {
		return null;
	}

	@Override
	public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
		return null;
	}

	@Override
	public X509Certificate[] getCertificateChain(String alias) {
		return null;
	}

	@Override
	public PrivateKey getPrivateKey(String alias) {
		return null;
	}

	@Override
    public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {

    }

    @Override
    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {

    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
