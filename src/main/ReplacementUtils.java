package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementUtils {
	
	String trackingNumber;
	int count = 103;
	
	public ReplacementUtils(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}
	
	public void setTrackingNumber(File file) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = reader.readLine();
		Pattern patternForRaw = Pattern.compile("\\(\"[a-zA-Z0-9]+\"\\)");
		String extracted = "";
		while(line != null) {
			if (line.contains("xipDashboard.processTransaction")) {
				try {
					Matcher matchRaw = patternForRaw.matcher(line);
					matchRaw.find();
					extracted = matchRaw.group(0);
					break;
				} catch (IllegalStateException e) {
					break;
				}
			}
			line = reader.readLine();
		}
		try {
			Pattern patternForExtract = Pattern.compile("[a-zA-Z0-9]+");
			Matcher matchExtract = patternForExtract.matcher(extracted);
			matchExtract.find();
			if (!matchExtract.group(0).equals("")) {
				trackingNumber = matchExtract.group(0);
			}
		} catch (IllegalStateException e) {
			return;
		}
	}
	
	
	public void replaceWithGoodCode(File file) throws FileNotFoundException, IOException {
		setTrackingNumber(file);
		String filename = file.getName();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		ArrayList<String> lines = new ArrayList<String>();
		String line = reader.readLine();
		boolean toAdd = false;
		ArrayList<String> newLines = new ArrayList<String>();
		while(line != null) {
			System.out.println("OLD LINE: "  + line);
			if (line.contains("public void test0() throws Exception {")) {
				toAdd = true;
				newLines.add(line);
				line = reader.readLine();
				continue;
			}
			if (toAdd) {
				System.out.println("NEW LINE: String trackingNumber = getNextTrackingNumber(\"\", 3)");
				newLines.add("String trackingNumber = getNextTrackingNumberWithoutPadding(2);");
				//newLines.add("String trackingNumber = getNextTrackingNumber(\"\", 3);");
				toAdd = false;
				continue;
			}
			String newLine = padding(line);
			newLine += replaceStringInLine(line);
			System.out.println("NEW LINE: " + newLine);
			newLines.add(newLine);
			line = reader.readLine();
		}
		PrintWriter printer = new PrintWriter(new File("/Users/bliu/Documents/TestDir/" + filename));
		for(int i = 0; i < newLines.size(); i++) {
			printer.println(newLines.get(i));
		}
		printer.close();
		reader.close();
		count += 1;
	}
	
	public String padding(String s) {
		StringBuilder sb = new StringBuilder();
		char[] characters = s.toCharArray();
		for(int i = 0; i < characters.length; i++) {
			if (characters[i] == ' ') {
				sb.append(" ");
			} else {
				break;
			}
		}
		return sb.toString();
	}
	
	private String replaceStringInLine(String line) {
		String[] words = line.split("\\s+");
		StringBuilder newLine = new StringBuilder();
		for(int i = 0; i < words.length; i++) {
			String word = words[i];
			String newWord = word;
			if (word.contains(trackingNumber)) {
				System.out.println("FOUND TRACKING NUMBER REFERENCE: " + word);
				String compiledString = "\"" + trackingNumber + "\"";
				if (word.contains("trackingNumber.setValue")) {
					newWord = word.replaceAll(compiledString, "trackingNum");
				} else {
					newWord = word.replaceAll(compiledString, "trackingNumber");
				}
			} else if (word.contains("ExpectFileFromPartnerCommand(")) {
				System.out.println("Found Expect File form Partner!: " + word);
				newWord = word.replaceAll("ExpectFileFromPartnerCommand\\(", "ExpectFileFromPartnerCommand( \"" + trackingNumber + "\", trackingNumber, ");
			} else if (word.contains("CompareFilesAssert(")) {
				System.out.println("Found Compare Files Assert!: " + word);
				newWord = word.replaceAll("CompareFilesAssert\\(", "CompareFilesAssert( \"" + trackingNumber + "\", trackingNumber, ");
			} else if (word.contains("createTransactionDetail000();")) {
				newWord = word.replaceAll("\\(\\)", "(trackingNumber)");
			} else if (word.contains("createTransactionDetail000()")) {
				newWord = word.replaceAll("\\(\\)", "(String trackingNum)");
			}
			newLine.append(newWord + " ");
		}
		return newLine.toString();
	}

}
