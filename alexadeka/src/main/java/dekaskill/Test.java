package dekaskill;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;


public class Test {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		FileReader fReader = new FileReader("C:\\Temp\\veranstaltung_json.txt");
		
		
		StringBuilder sb = new StringBuilder();
		BufferedReader bf = new BufferedReader(fReader);
		String line;
		while((line=bf.readLine())!=null){
			sb.append(line);
		}
		
		String jString = sb.toString();
		System.out.println(jString);
		Begriff[] jObject = (Begriff[]) new Gson().fromJson(new InputStreamReader(new FileInputStream("C:\\Temp\\veranstaltung_json.txt"),StandardCharsets.UTF_8), Begriff[].class);
		
		System.out.println(jObject[0].getBegriff());
		
		bf.close();
		fReader.close();
	}
}
