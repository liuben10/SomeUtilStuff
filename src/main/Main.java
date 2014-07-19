package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Main {
	

	
	public static void main(String...args) {
//		simpleTest();
		try {
			if (args.length == 0) return;
			String directoryPath = args[0];
			File source = new File(directoryPath);
			File[] files = source.listFiles();
			if (files.length == 0) return;
			String trackingNumber = "104";
			ReplacementUtils replacementUtils = new ReplacementUtils(trackingNumber);
			for(int i = 0; i < files.length; i++) {
				replacementUtils.replaceWithGoodCode(files[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void simpleTest() {
		String s = "\"104\"";
		System.out.println(s.replaceAll("\\(\\)", "(trackingNumber)"));
	}
	


}
