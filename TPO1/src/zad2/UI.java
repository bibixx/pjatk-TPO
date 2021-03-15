package zad2;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import javax.swing.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class UI { 
    private JFXPanel jfxPanel;
    private WebEngine engine;

	public UI() {
		JFrame frame = new JFrame();
	    this.jfxPanel = new JFXPanel();
	 
	    this.createScene();
	    
	    JPanel panel = new JPanel(new BorderLayout());

	    JComboBox<String> countriesComboBox = new JComboBox<String>(this.getCountriesList());
	    JComboBox<String> currenciesComboBox = new JComboBox<String>(this.getCurrenciesList());
	    JTextField cityInput = new JTextField();
	    JButton submitButton = new JButton("Submit");
	    
        JPanel inputPanel = new JPanel(new GridLayout(0, 1));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        inputPanel.add(new JLabel("Country:"));
        inputPanel.add(countriesComboBox);

        inputPanel.add(new JLabel("City:"));
        inputPanel.add(cityInput);

        inputPanel.add(new JLabel("Currency:"));
        inputPanel.add(currenciesComboBox);
        
        inputPanel.add(submitButton);
 
        panel.add(inputPanel, BorderLayout.NORTH);
 
        
        JPanel outputPanel = new JPanel(new BorderLayout());
        JPanel outputLabelsPanel = new JPanel(new GridLayout(0, 1));

	    JLabel weatherLabel = new JLabel("");
	    outputLabelsPanel.add(weatherLabel);
	    JLabel exchangeLabel = new JLabel("");
	    outputLabelsPanel.add(exchangeLabel);
	    JLabel nbpLabel = new JLabel("");
	    outputLabelsPanel.add(nbpLabel);
	    
	    outputPanel.add(outputLabelsPanel, BorderLayout.NORTH);
	    outputPanel.add(this.jfxPanel, BorderLayout.CENTER);

        panel.add(outputPanel, BorderLayout.CENTER);
 
        frame.getContentPane().add(panel);
 
        frame.setPreferredSize(new Dimension(1024, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
        submitButton.addActionListener((ActionEvent e) -> {
        	String country = countriesComboBox.getSelectedItem().toString();
        	String currency = currenciesComboBox.getSelectedItem().toString();
        	String city = cityInput.getText();
        	
        	Service s = new Service(country);
        	
        	String weatherDescription = s.getWeatherDescription(city);
        	if (weatherDescription == null) {
        		weatherDescription = "N/A";	
        	}
        	weatherLabel.setText(String.format("Weather: %s", weatherDescription));
        	
        	Double rate = s.getRateFor(currency);
        	String rateText = "N/A";
        	if (rate != null) {
        		rateText = rate.toString();
        	}
        	exchangeLabel.setText(String.format("Exchange rate: %s", rateText));

        	Double nbpRate = s.getNBPRate();
        	String nbpRateText = "N/A";
        	if (nbpRate != null) {
        		nbpRateText = nbpRate.toString();
        	}
        	nbpLabel.setText(String.format("NBP: %s", nbpRateText));
        	
        	String url = String.format("https://en.wikipedia.org/wiki/%s", city);
        	this.loadUrl(url);
        });
	}
	
	private String[] getCountriesList() {
	    ArrayList<String> countriesList = new ArrayList<String>();
	    
		for (Locale locale: Locale.getAvailableLocales()) {
			String displayCountry = locale.getDisplayCountry();

			if (!displayCountry.equals("")) {
				countriesList.add(displayCountry);
			}
	    }
		
		Collections.sort(countriesList);
		
		return countriesList
			.toArray(new String[countriesList.size()]);
	}
	
	private String[] getCurrenciesList() {
		ArrayList<String> currenciesList = new ArrayList<String>();

		for(Currency currency : Currency.getAvailableCurrencies()) {
			currenciesList.add(currency.getCurrencyCode());
		}
		
		Collections.sort(currenciesList);
		
		return currenciesList
			.toArray(new String[currenciesList.size()]);
	}
	

    private void createScene() {  
    	Platform.runLater(() -> {
        	WebView view = new WebView();
            engine = view.getEngine();
            
            jfxPanel.setScene(new Scene(view));
        });
    }
    
    private void loadUrl(String url) {
    	Platform.runLater(() -> {
    		engine.load(url);	
    	});
    }
}