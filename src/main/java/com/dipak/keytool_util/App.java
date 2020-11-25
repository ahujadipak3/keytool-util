package com.dipak.keytool_util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * 
 * @author dipak.ahuja
 *
 */
public class App {

	final static boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	public static void main(String[] args) throws Exception {
		
		if(args.length != 1) {
			throw new Exception("Incorrect usage! Please provide proper arguments.");
		}
	
		FileReader reader=new FileReader(args[0]);  
	    Properties p=new Properties();  
	    p.load(reader);  
		
		String credentials = p.getProperty("credentials");
		String clientpwd = p.getProperty("clientpwd");
		generateKeyPair(credentials, clientpwd);
		generateCSR(clientpwd);
		signCSR();
		generateP12(clientpwd);
	}

	
	public static void generateP12(String clientpwd) {
		String command = "-importkeystore -srckeystore clientcrt.jks -destkeystore clientcrt.p12 -srcstoretype JKS -deststoretype PKCS12 -srcstorepass "+ clientpwd+" -deststorepass "+clientpwd;
		execute(command);
	}

	public static void generateKeyPair(String credentials, String clientpwd) {
		String command = "-genkey -v -alias clientcrt -keyalg RSA -validity 3650 -keystore clientcrt.jks -storepass "+ clientpwd + " -keypass "+ clientpwd
				+ " -dname "+ credentials;
		execute(command);
	}

	public static void signCSR() throws Exception {
		Process p;
		String command = "openssl ca -batch -startdate 200716080000Z -enddate 300715090000Z -keyfile rootCA.key -cert rootCA.crt -policy policy_anything -out clientcrt.crt -infiles clientcrt.csr";
		Runtime r = Runtime.getRuntime();

		try {
			p = r.exec(command);
			int exitVal = /* p.exitValue(); */ p.waitFor();
			if (exitVal == 0) {
				System.out.println("CSR signed successfully!");
			} else {
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				final String errorResponse = reader.lines().collect(Collectors.joining(""));
				System.out.println(errorResponse);
				System.out.println("An error while signing the CSR!");
				throw new Exception(errorResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	// Execute the commands
	public static void execute(String command) {
		try {
			printCommand(command);
			sun.security.tools.keytool.Main.main(parse(command));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Parse command
	private static String[] parse(String command) {
		String[] options = command.trim().split("\\s+");
		return options;
	}

	private static void generateCSR(String clientpwd) {
		String command = "-certreq -alias clientcrt -file clientcrt.csr -keystore clientcrt.jks -storepass "+clientpwd;
		execute(command);
	}

	// Print the command
	private static void printCommand(String command) {
		System.out.println(command);
	}
}
