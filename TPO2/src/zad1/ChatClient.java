/**
 *
 *  @author Legięć Bartosz S19129
 *
 */

package zad1;

import java.net.*;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ChatClient extends Thread {
	private SocketChannel channel = null;
	private String id;
	private List<String> logs = new ArrayList<String>();
    private final Lock lock = new ReentrantLock();
	private String host;
	private int port;
	
	public ChatClient(String host, int port, String id) throws UnknownHostException, IOException {
		this.id = id;
		this.host = host;
		this.port = port;
		
		this.logs.add(String.format("\n=== %s chat view", id));
	}
	
	public void login() {
		try {
			this.channel = SocketChannel.open(new InetSocketAddress(this.host, this.port));
			channel.configureBlocking(false);

			this.makeRequest("login", this.id);
			
			this.start();
		} catch (Exception e) {
			this.logException(e);
		}
	}
	
	public void logout() {
		try {
			this.makeRequest("logout", this.id);
		
			this.logs.add(String.format("%s logged out", this.id));
		
            lock.lock();
            this.interrupt();
        } catch (Exception e) {
			this.logException(e);
        } finally {
            lock.unlock();
        }
	}
	
	public void send(String request) {
		this.makeRequest("message", request);
	}
	
	private void makeRequest(String event, String data) {
		try {	
			String requestString = String.format("%s\t%s", event, data);
			
	        this.channel.write(StandardCharsets.UTF_8.encode(requestString + '\n'));
		} catch (Exception e) {
			this.logException(e);
		}
	}
	
	public String getChatView() {
		return String.join("\n", this.logs);
	}

	public void run() {
		ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
		int bytesRead = 0;

		while (!this.isInterrupted()) {
			Charset charset = Charset.forName("UTF-8");
			StringBuffer stringBuffer = new StringBuffer();
			boolean hasReadFullLine = false;

			do {
				try {
					lock.lock();
					bytesRead = channel.read(buffer);
					if (bytesRead > 0) {
						buffer.flip();
			            CharBuffer cbuf = charset.decode(buffer);
			            while (cbuf.hasRemaining()) {
			            	char c = cbuf.get();
			            	
			            	if (c == '\n') {
			            		hasReadFullLine = true;
			            	} else {
			            		stringBuffer.append(c);
			            	}
			            }
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				} finally {
					lock.unlock();
				}
			} while (bytesRead == 0 && !this.isInterrupted() && !hasReadFullLine);

			String response = stringBuffer.toString();
			
			if (!response.equals("")) {
				this.logs.add(response);
			}
			
			buffer.clear();
			bytesRead = 0;
		}
	}

	private void logException(Exception exception) {
		this.logs.add(String.format("*** %s", exception.toString()));
	}
}
