package com.example.ReadXMLFile.Main;

import org.w3c.dom.*;

import com.example.ReadXMLFile.Model.AccountData;

import javax.xml.parsers.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
	public static final String Green= "\033[1;32m";
	public static final String RESET = "\u001B[0m";
	public static final String RED = "\033[1;31m";
	public static final String BLUE = "\033[1;34m";

	public static void main(String[] args) {
		// String filePath = "C:\\Users\\riya.g\\Downloads\\SKDCBMAVREQ221220231.xml";
		//      AccountData accountData = XMLParser.parseXML(filePath);

		//	read files from folder
		String directoryPath = "D:\\Data\\XML_File\\Read_Request_File\\Request_XML\\test";

		File folder = new File(directoryPath);
		File[] files = folder.listFiles();

		if (files != null) {
			for (File xmlFile : files) {
				if (xmlFile.isFile() && xmlFile.getName().toLowerCase().endsWith(".xml") && !isProcessed(xmlFile)) {
					//	AccountData accountData = parseXML(xmlFile.getPath());
					parseXML(xmlFile.getPath());
					//		renameFile(xmlFile);

				}
				else {
					System.out.println(BLUE+"this file already in read format "+ xmlFile.getName()+RESET);
				}}}
	}


	//check the xml file ends with .processed
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


	//read xml file
	public static void parseXML(String filePath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File file = new File(filePath);
			Document document = builder.parse(file);
			document.getDocumentElement().normalize();

			Element root = document.getDocumentElement();

			String xmlns = root.getAttribute("xmlns");
			String recordsCount = root.getAttribute("RecordsCount");
			String bankName = root.getAttribute("BankName");
			String bankCode = root.getAttribute("BankCode");
			String destination = root.getAttribute("Destination");
			String source = root.getAttribute("Source");
			String messageId = root.getAttribute("MessageId");

			List<String> accountList = new ArrayList<>();
			NodeList accountNodeList = root.getElementsByTagName("Account");

			for (int i = 0; i < accountNodeList.getLength(); i++) {
				Node accountNode = accountNodeList.item(i);
				if (accountNode.getNodeType() == Node.ELEMENT_NODE) {
					Element accountElement = (Element) accountNode;

					String accountNumber = accountElement.getElementsByTagName("AccountNumber").item(0).getTextContent();
					String entityCode = accountElement.getElementsByTagName("EntityCode").item(0).getTextContent();
					String dataRequired = accountElement.getElementsByTagName("DataRequired").item(0).getTextContent();



					String concatenatedDetails = messageId + "|" + source + "|" + bankCode + "|" + destination + "|"
							+ bankName + "|" + recordsCount + "|" + xmlns + "|" + accountNumber + "|"
							+ entityCode + "|" + dataRequired;

					accountList.add(concatenatedDetails);
				}
			}

			int accountsRead = accountList.size();
			int accountsInserted = 0;
			//	uat DB connection
			//			Class.forName("oracle.jdbc.driver.OracleDriver");
			//			String url = "jdbc:oracle:thin:@10.43.4.15:1532:infuatdb";
			//			String username = "infuat";
			//			String password = "infuat_123";
			//			Connection connection = DriverManager.getConnection(url, username, password);


			//uat insert producer test
			//	CallableStatement callableStatement = connection.prepareCall("{CALL sp_insert_data(?)}");

			//sqllite
			//Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\riya.g\\Desktop\\Test.db");

			//mysql connection test
			Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "root");

			//mysql insert producer
			CallableStatement callableStatement = connection.prepareCall("{CALL InsertDataFromPipeSeparated(?)}");

			// Iterate through concatenated account details and call the stored procedure
			for (String concatenatedAccount : accountList) {
				callableStatement.setString(1, concatenatedAccount);
				callableStatement.execute();
				accountsInserted++;


			}

			if (accountsRead != accountsInserted) {
				System.out.println(RED+"Insertion count does not match the read count!"+RESET);
			}
			else {
				System.out.println(Green+"Sucessfully inserted the data and the count are same of 'read' file is "+"'"+ accountsRead+"'"+" and the 'inserted' file is "+ "'"+accountsInserted+"'"+RESET);
			}

			callableStatement.close();
			connection.close();           



			for (String concatenatedAccount : accountList) {
				System.out.println(concatenatedAccount);
			}



		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}
