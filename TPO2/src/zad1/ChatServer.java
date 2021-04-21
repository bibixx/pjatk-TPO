/**
 *
 *  @author Legięć Bartosz S19129
 *
 */

package zad1;

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.nio.charset.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ChatServer extends Thread {

	private String host;
	private int port;
	private ServerSocketChannel serverChannel;
    private Selector selector = null;
    private final Lock lock = new ReentrantLock();
    
    private List<String> logs = new ArrayList<String>(); 
    private final Map<SocketChannel, String> clients = new HashMap<>();

	public ChatServer(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void startServer() throws IOException {
		this.serverChannel = ServerSocketChannel.open();
		this.serverChannel.socket().bind(new InetSocketAddress(this.host, this.port));
		this.serverChannel.configureBlocking(false);
		
		this.selector = Selector.open();
		this.serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
		
		this.start();
		
		System.out.println("\nServer started");
	}

	public void stopServer() throws IOException {
		try {
			lock.lock();
			this.interrupt();
			this.selector.close();
			this.serverChannel.close();

			System.out.println("\nServer stopped");
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public void run() {
		while (!this.isInterrupted()) {
			try {
				selector.select();
				
                if (this.isInterrupted()){
                    break;
                }
				
				Set<SelectionKey> keys = selector.selectedKeys();

				Iterator<SelectionKey> iter = keys.iterator();
				while (iter.hasNext()) {
					SelectionKey key = iter.next();
					iter.remove();
					
					if (key.isAcceptable()) {
						SocketChannel clientChannel = this.serverChannel.accept();
						clientChannel.configureBlocking(false);
						clientChannel.register(selector, SelectionKey.OP_READ);
						continue;
					}

					if (key.isReadable()) {
						SocketChannel clientChannel = (SocketChannel) key.channel();
						this.handleRequest(clientChannel);
						continue;
					}
				}
			} catch(Exception exc) {
				exc.printStackTrace();
				continue;
			}
		}
    }
	
	private String readSocket(SocketChannel socketChannel) throws IOException {
	    try {
	    	this.lock.lock();

		    StringBuffer stringBuffer = new StringBuffer();
		    ByteBuffer buffer = ByteBuffer.allocate(1024);
		    Charset charset = Charset.forName("UTF-8");

		    while (true) {
		    	int n = socketChannel.read(buffer);
	          	if (n > 0) {
	          		buffer.flip();
		            CharBuffer cbuf = charset.decode(buffer);
		            while (cbuf.hasRemaining()) {
		            	char c = cbuf.get();
		            	if (c == '\n') {
		            		return stringBuffer.toString();
		            	}

		            	stringBuffer.append(c);
		            }
		        }
		    }
	    } catch (Exception e) {
	    	throw e;
	    } finally {
	    	this.lock.unlock();	
	    }
	}
	
	private void handleRequest(SocketChannel socketChannel) throws IOException {
	    String request = this.readSocket(socketChannel);
	    
	    Pattern r = Pattern.compile("([^\\t]+)\\t(.+)");
	    Matcher m = r.matcher(request);
	    
	    if (!m.find()) {
	    	return;
	    }
	    
	    String event = m.group(1);
	    String message = m.group(2);

	    if (event.equals("login")) {
	    	clients.put(socketChannel, message);
	    }
	    
	    String clientId = clients.get(socketChannel);
	    String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
	    
	    String response = this.getLogMessage(event, message, clientId);
	    
	    this.logs.add(String.format("%s %s", time, response));
	    
	    this.broadcast(response);
	}
	
	private void broadcast(String response) throws IOException {
		for (Map.Entry<SocketChannel, String> entry : this.clients.entrySet()) {
			SocketChannel clientSocket = entry.getKey();

			clientSocket.write(StandardCharsets.UTF_8.encode(response + '\n'));
		}
	}
	
	private String getLogMessage(String event, String message, String clientId) {
		switch (event) {
		    case "login": {
				return String.format("%s logged in", clientId);
			}
			case "message": {
				return String.format("%s: %s", clientId, message);
			}
		    case "logout": {
		    	return String.format("%s logged out", clientId);
			}
		}
		
		return "";
	}
	
	public String getServerLog() {
		return String.join("\n", this.logs);
	}
}
