package zad2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Currency;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class Utils {
	static public String getCountryCode(String country) {
		for (String iso : Locale.getISOCountries()) {
	        Locale locale = new Locale("", iso);

	        if (locale.getDisplayCountry().equals(country)) {
	        	return iso;
	        }
	    }

		return null;
	}
	
	static public String getCurrencyCode(String country) {
		for (Locale locale: Locale.getAvailableLocales()) {
	        if (locale.getDisplayCountry().equals(country)) {
	        	Currency currency = Currency.getInstance(locale);

	        	return currency.getCurrencyCode();
	        }
	    }

		return null;
	}
	
	static public String makeRequest(String requestUrl) throws IOException {
		URL url = new URL(requestUrl);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		InputStream inputStream = connection.getInputStream();
			        
        String text = new BufferedReader(new InputStreamReader(inputStream))
	        .lines()
	        .collect(Collectors.joining("\n"));
        
        connection.disconnect();

        return text;
	}
	
	static public Document convertStringToXMLDocument(String xmlString) {
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        	DocumentBuilder builder = factory.newDocumentBuilder();
             
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
