package de.peschuster.teamcity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class GraphiteAdapter {
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Charset ANSI = Charset.forName("ISO-8859-1");
    
    private Socket conn = null;
    private BufferedWriter writer = null;
    
    public GraphiteAdapter(String host, int port) throws IOException {
		conn = new Socket(host, port);

		writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), ANSI));
	}
    
	public void Report(String key, int value) {
		
		if (conn == null)
			return;
		
		try {			
			long timestamp = System.currentTimeMillis() / 1000L;
			
            writer.write(sanitize(key));
            writer.write(' ');
            writer.write(Integer.toString(value));
            writer.write(' ');
            writer.write(Long.toString(timestamp));
            writer.write('\n');
            writer.flush();
            
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void Dispose() {
		if (conn != null) {
			try {
				conn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		conn = null;
		writer = null;
	}
	
    protected String sanitize(String s) {
        return WHITESPACE.matcher(s).replaceAll("-");
    }
}
