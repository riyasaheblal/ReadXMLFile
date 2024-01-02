package com.example.ReadXMLFile.Main;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

public class XMLReader {

	public static final String Green= "\033[1;32m";
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\033[1;31m";
	public static final String BLUE = "\033[1;34m";

	public static void main(String[] args) {
		ArrayList<String> dataList = new ArrayList<>();

		try {
			String directoryPath = "D:\\Data\\XML_File\\Read_Request_File\\Response_XML\\test";
			File directory = new File(directoryPath);

			if (directory.isDirectory()) {
				//	File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
				File[] files = directory.listFiles();

				if (files != null) {
					for (File xmlFile : files) {
						if (xmlFile.isFile() && xmlFile.getName().toLowerCase().endsWith(".xml") && !isProcessed(xmlFile)) {

							processXMLFile(xmlFile, dataList);
							//	renameFile(xmlFile);
						}else {
							System.out.println(BLUE+"this file already in read format "+ xmlFile.getName()+RESET);
						}
					}
				}
			} else {
				System.out.println("Provided path is not a directory.");
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean isProcessed(File file) {
		String fileName = file.getName();
		String processedSuffix = ".xml.processed";
		return fileName.toLowerCase().endsWith(processedSuffix) && !fileName.toLowerCase().contains(".processed");
	}


	//rename xml file
	private static void renameFile(File file) {
		Path source = file.toPath();
		Path renamed = Paths.get(file.getParent(), file.getName().replace(".xml", ".xml.processed"));

		try {
			Files.move(source, renamed);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void processXMLFile(File xmlFile, ArrayList<String> dataList) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList accountsList = doc.getElementsByTagName("Accounts");

			for (int temp = 0; temp < accountsList.getLength(); temp++) {
				NodeList accountList = doc.getElementsByTagName("Account");
				for (int i = 0; i < accountList.getLength(); i++) {
					Element accountElement = (Element) accountList.item(i);
					String ahDetailsData = extractAHDetailsData(accountElement);
					dataList.add(ahDetailsData);
				}
			}
			int ResponseRead = dataList.size();
			int accountsUpdated = 0;
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root");
			CallableStatement callableStatement = connection.prepareCall("{CALL UpdateDataFromPipeSeparated(?)}");
			for (String concatenatedAccount : dataList) {
				callableStatement.setString(1, concatenatedAccount);
				callableStatement.execute();
				accountsUpdated++;
			}
			if (ResponseRead != accountsUpdated) {
				System.out.println(RED+"Insertion count does not match the read count!"+RESET);
			}
			else {
				System.out.println(Green+"Read and update count of the data. Read count: " + ResponseRead + ", Inserted count: " + accountsUpdated + RESET);
			}
			callableStatement.close();
			connection.close();


			for (String concatenatedAccount : dataList) {
				System.out.println(concatenatedAccount);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static String extractAHDetailsData(Element element) {
		Element ahElement = (Element) element.getElementsByTagName("AH").item(0);

		return element.getElementsByTagName("ReqMsgId").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AccountNumber").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AccountValidity").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AccountStatus").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AccountType").item(0).getTextContent() + "|" +
		element.getElementsByTagName("BSRCode").item(0).getTextContent() + "|" +
		element.getElementsByTagName("IFSCCode").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AccountOpenDate").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AccountCloseDate").item(0).getTextContent() + "|" +
		ahElement.getAttribute("Name") + "|" +
		ahElement.getAttribute("Mobile") + "|" +
		ahElement.getAttribute("Gender") + "|" +
		ahElement.getAttribute("DOB") + "|" +
		ahElement.getAttribute("AddressLine1") + "|" +
		ahElement.getAttribute("AddressLine2") + "|" +
		element.getElementsByTagName("AHOTHDTL1").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AHOTHDTL2").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AHOTHDTL3").item(0).getTextContent() + "|" +
		element.getElementsByTagName("AHOTHDTL4").item(0).getTextContent() ;
	}
}
