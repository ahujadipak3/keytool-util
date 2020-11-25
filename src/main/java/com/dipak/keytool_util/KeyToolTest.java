package com.dipak.keytool_util;

public class KeyToolTest {
    public static void main(String[] args){
        generateKeyPair();
        list();
    }
         
         
    // List keystore
    public static void list(){
        String command = " -list "+
                         " -v "+
                         " -keystore mytest.jks "+
                         " -storepass password";
        execute(command);
    }
     
    // Generate keypair
    public static void generateKeyPair(){
        String command = " -genkeypair "+
                         " -alias mykey "+
                         " -keyalg RSA "+
                         " -sigalg SHA256withRSA "+
                         " -dname CN=Java "+
                         " -storetype JKS "+
                         " -keypass password "+
                         " -keystore mytest.jks "+
                         " -storepass password";
        execute(command);
    }
     
    // Execute the commands
    public static void execute(String command){
        try{
            printCommand(command);
            sun.security.tools.keytool.Main.main(parse(command));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
     
    // Parse command
    private static String[] parse(String command){
        String[] options = command.trim().split("\\s+");
        return options;
    }
     
    // Print the command
    private static void printCommand(String command){
        System.out.println(command);
    }
}