package dekaskill;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpContent {
	
	//private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("cc-proxy.dekager.dekabank.intern", 81));
	//private static String encoded = new String(Base64.getEncoder().encode("B022616:zidane3§*".getBytes()));

	
	public String getContent(String urlString){
		URL url = null;
		HttpsURLConnection https = null;
		BufferedReader br = null;
		String httpsContent = "";
		
		
		try {
			url = new URL(urlString);
			https = (HttpsURLConnection) url.openConnection();
			//https = (HttpsURLConnection) url.openConnection(proxy);
			//https.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
			String charset = https.getContentType().split("=")[1];
			br = new BufferedReader(new InputStreamReader(https.getInputStream(),charset));
			String line = null;
			
			while((line=br.readLine())!=null){
				httpsContent += line;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			https.disconnect();
		}
		
		return httpsContent;
	}
}
