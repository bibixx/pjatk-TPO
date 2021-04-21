/**
 *
 *  @author Legięć Bartosz S19129
 *
 */

package zad1;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ChatClientTask extends FutureTask<ChatClient> {
    public ChatClientTask(Callable<ChatClient> callable) {
        super(callable);
    }

    public static ChatClientTask create(ChatClient client, List<String> messages, int wait) {
        return new ChatClientTask(() -> {
            try {
            	client.login();
    			sleep(wait);
    			
    			for (String message : messages) {
    				client.send(message);
    				sleep(wait);
    			}
    			
    			client.logout();	
    			sleep(wait);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return client;
        });
    }

	private static void sleep(int waitTime) throws InterruptedException {
		if (waitTime == 0) {
			return;
		}
		
		Thread.sleep(waitTime);
	}
	

    public ChatClient getClient() {
        try {
			return this.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
    }

}
