package naver;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class JavaTest {

	public static void main(String[] args) {
		String clientId = "clientId";// 애플리케이션 클라이언트 아이디값";
		String clientSecret = "clientSecret";// 애플리케이션 클라이언트 시크릿값";
		try {

			// https://openapi.naver.com/v1/search/book.xml 책 기본 검색
			// https://openapi.naver.com/v1/search/book_adv.xml 책 상세 검색
			// https://openapi.naver.com/v1/search/book_adv.json 책 상세 검색

			// 1) xml 기본 검색
			// 기본 검색의 query 변수 = 검색을 원하는 문자열로서 UTF-8로 인코딩한다.
			String author = URLEncoder.encode("이은선", "UTF-8");
			String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + author + "&display=10&start=1";

			// 2) json 상세검색
			// 상세 검색은 책 제목(d_titl), 저자명(d_auth), 목차(d_cont), ISBN(d_isbn),
			// 출판사(d_publ) 5개 항목 중에서 1개 이상 값을 입력해야 함.
			// String apiURL =
			// "https://openapi.naver.com/v1/search/book_adv.json?d_titl=%EC%A3%BC%EC%8B%9D&display=10&start=1";

			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("X-Naver-Client-Id", clientId);
			con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
			int responseCode = con.getResponseCode();
			BufferedReader br;
			if (responseCode == 200) { // 정상 호출
				br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} else { // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
			}
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = br.readLine()) != null) {
				response.append(inputLine);
			}
			br.close();
			System.out.println(response.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void parseResponse(int type, StringBuffer response) {

	}

}
