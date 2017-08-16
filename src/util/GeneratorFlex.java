package util;

import java.io.File;

public class GeneratorFlex {

	public static void main(String[] args) {
		
		String file = "src//lexical//language.flex";

        File sourceCode = new File(file);

        jflex.Main.generate(sourceCode);
        
        
	}

}
