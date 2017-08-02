package main;

import sintatico.Parser;
import lexical.LexicalAnalyzer;
import java_cup.runtime.Symbol;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
        String filePath = "src/Tests/Test.txt";
        LexicalAnalyzer lex = null;
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
            return;
        }

        System.out.println("Done!");

        System.out.println(s);
    }

}