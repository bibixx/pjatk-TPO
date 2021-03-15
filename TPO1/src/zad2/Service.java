/**
 *
 *  @author Legięć Bartosz S19129
 *
 */

package zad2;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Service {
	static private final String OPENWEATHER_API_KEY = "cbc1ca1955c56b1632c86a7c617202a9";
	static private final String OPENWEATHER_API_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s";
	static private final String EXCHANGE_RATES_API_URL = "https://api.exchangeratesapi.io/latest?base=%s&symbols=%s";
	
	private String country;

	public Service(String country) {
		this.country = country;
	}

	public String getWeather(String city) {
		String countryISOCode = Utils.getCountryCode(this.country);
		String apiUrl = String.format(
			Service.OPENWEATHER_API_URL,
			city,
			countryISOCode,
			Service.OPENWEATHER_API_KEY
		);
		
		try {
			return Utils.makeRequest(apiUrl);	
		} catch (IOException e) {
			return null;
		}
	}
	
	public String getWeatherDescription(String city) {
		String data = this.getWeather(city);
		
		if (data == null) {
			return null;
		}
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			JsonNode json = mapper.readTree(data);
			
			return json.get("weather").get(0).get("description").asText();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public Double getRateFor(String currencyCode) {
		String baseCurrencyCode = Utils.getCurrencyCode(this.country);
		
		String apiUrl = String.format(Service.EXCHANGE_RATES_API_URL, currencyCode, baseCurrencyCode);
		String data;

		try {
			data = Utils.makeRequest(apiUrl);
		} catch (IOException e1) {
			return null;
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(data);
			JsonNode currencyObject = json.get("rates").get(baseCurrencyCode); 
			
			if (currencyObject == null) {
				return null;
			}

			return currencyObject.asDouble();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		return null;
	}

	private Double getNBPRate(String table) {
		String currencyCode = Utils.getCurrencyCode(this.country);
		
		if (currencyCode == null) {
			return null;
		}
		
		if (currencyCode.equals("PLN")) {
			return 1d;
		}
		
		String tableUrl = String.format("https://www.nbp.pl/kursy/kursy%s.html", table);

		String tablePageData;

		try {
			tablePageData = Utils.makeRequest(tableUrl);
		} catch (IOException e1) {
			return null;
		}

		Matcher matcher = Pattern
			.compile("href=\"/kursy/xml/(.+?)\"")
			.matcher(tablePageData);
	    
	    
	    if (!matcher.find()) {
	        return null;
	    }
	    
	    String fileName = matcher.group(1);
	    String apiUrl = String.format("https://www.nbp.pl/kursy/xml/%s", fileName);

		String data;
		try {
			data = Utils.makeRequest(apiUrl);
		} catch (IOException e1) {
			return null;
		}

		Document doc = Utils.convertStringToXMLDocument(data);

		try {
			XPath xpath = XPathFactory.newInstance().newXPath();

			String expression = String.format(
				"/tabela_kursow/pozycja[descendant::kod_waluty[text()=\"%s\"]]/kurs_sredni",
				currencyCode
			);
			
			NodeList nodeList = (NodeList) xpath
				.compile(expression)
				.evaluate(doc, XPathConstants.NODESET);
			
			if (nodeList.getLength() == 0) {
				return null;
			}
			
			Node node = nodeList.item(0);
			String textValue = node.getTextContent().replace(',', '.');
			Double value = Double.parseDouble(textValue);
			
			return value;
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public Double getNBPRate() {
		Double rateA = this.getNBPRate("a");
		
		if (rateA != null) {
			return rateA;
		}

		Double rateB = this.getNBPRate("b");
		if (rateB != null) {
			return rateB;
		}

		return this.getNBPRate("c");
	}
	
}  
