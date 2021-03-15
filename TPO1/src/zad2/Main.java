/**
 *
 *  @author Legięć Bartosz S19129
 *
 */

package zad2;

import javax.swing.SwingUtilities;

public class Main {
  public static void main(String[] args) {	  
    Service s = new Service("Poland");
    String weatherJson = s.getWeather("Warsaw");
    Double rate1 = s.getRateFor("USD");
    Double rate2 = s.getNBPRate();
    
    System.out.println(weatherJson);
    System.out.println(rate1);
    System.out.println(rate2);
    
    SwingUtilities.invokeLater(() -> {
    	new UI();
    });
  }
}
