package main;

import sintatico.Parser;
import lexical.LexicalAnalyzer;
import java_cup.runtime.Symbol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
    	String filePath = "src/Tests/Test.txt";
    	String fileClass = "src/Tests/TestClass.txt";
    	String Interface = "src/Tests/Interface.txt";
    	String fileInterface = "src/Tests/TestInterface.txt";
    	List<String> classes = new ArrayList<String>();
    	classes.add(filePath);
    	classes.add(fileClass);
    	classes.add(Interface);
    	classes.add(fileInterface);
        LexicalAnalyzer lex = null;
        for (String classe : classes) {
        	Symbol s = processaArquivos(classe, lex);
            System.out.println("Done!");
            System.out.println(s);
		}
      
    }

	private static Symbol processaArquivos(String filePath, LexicalAnalyzer lex) {
		try {

            lex = new LexicalAnalyzer(new BufferedReader(new FileReader(filePath)));

        } catch (FileNotFoundException e1) {
            System.out.println(e1.getMessage());
        }

        Parser parser = new Parser(lex);

        Symbol s = null;
        try {
            s = parser.parse();
        } catch (Exception e) {

            System.out.println(e.getMessage());
        }
		return s;
	}

}