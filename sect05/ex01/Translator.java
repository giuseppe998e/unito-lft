import java.io.*;

import lexer.Lexer;
import lexer.component.Tag;
import lexer.component.Token;
import lexer.component.Number;
import lexer.component.Identifier;

public class Translator {
  private final BufferedReader bReader;
	private final SymbolTable symTable;
	private final CodeGenerator codeGen;
  private final Lexer lexer;
  private Token token;

	public Translator(Lexer lexer, BufferedReader bReader) {
		this.symTable = new SymbolTable();
		this.codeGen = new CodeGenerator();

		this.lexer = lexer;
		this.bReader = bReader;

		move();
	}

  private void match(int t) {
    if (token.getTag() == t) {
      if (token.getTag() != Tag.EOF) move();
    } else error("syntax error, '" + t + "' expected, '" + token.getTag() + "' found");
  }

  private void move() {
    token = lexer.scan(bReader);
    // System.out.println("token = " + token);
  }

  private void error(String s) {
    throw new RuntimeException("near line " + lexer.getLine() + ": " + s);
  }

  // -------------------------------------
  // Application Main
  public static void main(String[] args) {
    Lexer lexer = new Lexer();

    try (FileReader fReader = new FileReader(args[0]);
          BufferedReader bReader = new BufferedReader(fReader)) {
      
      Translator translator = new Translator(lexer, bReader);
      translator.start();

      System.out.println("Input: OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}