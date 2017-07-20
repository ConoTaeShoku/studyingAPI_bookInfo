package naver;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Naver 검색 api에서 도서 상세 내용을 가져오는 class
 * 상세 검색은 책 제목(d_titl), 저자명(d_auth), 목차(d_cont), ISBN(d_isbn), 출판사(d_publ) 5개 항목 중에서 1개 이상 값을 입력해야 함.
 * 참고 site url = https://developers.naver.com/docs/search/book/
 */
public class AccessBookApi {

	private final int RESULT_NUMBER = 10;

	public void test() {
		System.out.println("accessSimple(이은선)");
		System.out.println(Arrays.toString(accessSimple("이은선")));

		System.out.println("\n\nsearchByTitle(수학귀신)");
		System.out.println(Arrays.toString(searchByTitle("수학귀신")));

		System.out.println("\n\nsearchByAuthor(한스 마그누스 엔첸스베르거)");
		System.out.println(Arrays.toString(searchByAuthor("한스 마그누스 엔첸스베르거")));

		System.out.println("\n\nsearchByContents(첫 번째 밤)");
		System.out.println(Arrays.toString(searchByContents("첫 번째 밤")));

		System.out.println("\n\nsearchByIsbn(9788949190013)");
		System.out.println(Arrays.toString(searchByIsbn("9788949190013")));
	}

	public static void main(String[] args) {
		new AccessBookApi().test();
	}

	public String[] searchByTitle(String userInput) {
		return accessSpecific("d_titl", userInput);
	}

	public String[] searchByAuthor(String userInput) {
		return accessSpecific("d_auth", userInput);
	}

	public String[] searchByContents(String userInput) {
		return accessSpecific("d_cont", userInput);
	}

	public String[] searchByIsbn(String userInput) {
		return accessSpecific("d_isbn", userInput);
	}

	public String[] accessSpecific(String type, String userInput) {
		String clientId = "";
		String clientSecret = "";
		try {
			String keyword = URLEncoder.encode(userInput, "UTF-8");
			// 1) json
			// String apiURL = "https://openapi.naver.com/v1/search/book_adv.json?"+type+"="+keyword+"&display="+RESULT_NUMBER+"&start=1";
			// 2) xml
			String apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?" + type + "=" + keyword + "&display=" + RESULT_NUMBER + "&start=1";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			if (apiURL.matches(".*xml.*")) {
				return parseXml(response.toString());
			} else if (apiURL.matches(".*json.*")) {
				return parseJson(response.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] accessSimple(String searchInput) {
		String clientId = "";
		String clientSecret = "";
		try {
			String keyword = URLEncoder.encode(searchInput, "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + keyword + "&display=" + RESULT_NUMBER + "&start=1";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) {
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			return parseXml(response.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	private String[] parseXml(String response) {
		String[] resultList = new String[RESULT_NUMBER];
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(response.getBytes("utf-8"));
			Document doc = builder.parse(is);
			Element searchResult = doc.getDocumentElement();
			NodeList books = searchResult.getElementsByTagName("item");
			for (int i = 0; i < RESULT_NUMBER; i++) {
				Node book = books.item(i);
				if (book == null) {
					break;
				} else {
					if (book.getNodeType() == Node.ELEMENT_NODE) {
						Element eBook = (Element) book;
						// System.out.println((i + 1) + "th book");
						// System.out.println("title : " + eBook.getElementsByTagName("title").item(0).getTextContent());
						// System.out.println("description : " + eBook.getElementsByTagName("description").item(0).getTextContent());
						resultList[i] = eBook.getElementsByTagName("description").item(0).getTextContent();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	private String[] parseJson(String response) {
		String[] resultList = new String[RESULT_NUMBER];
		try {
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObj = (JSONObject) jsonParser.parse(response);
			JSONArray bookArray = (JSONArray) jsonObj.get("items");
			for (int i = 0; i < RESULT_NUMBER; i++) {
				JSONObject tempObj = (JSONObject) bookArray.get(i);
				if (tempObj == null) {
					break;
				} else {
					// System.out.println("\n" + (i + 1) + "th Book!");
					// System.out.println("title: " + tempObj.get("title"));
					// System.out.println("description: " + tempObj.get("description"));
					resultList[i] = (String) tempObj.get("description");
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return resultList;
	}

}
