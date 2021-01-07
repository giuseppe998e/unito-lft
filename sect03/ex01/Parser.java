import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import lexer.Lexer;
import lexer.component.Tag;
import lexer.component.Token;

public class Parser {
  private BufferedReader bReader;
  private Lexer lexer;
  private Token token;

  public Parser(Lexer lexer, BufferedReader bReader) {
    this.lexer = lexer;
    this.bReader = bReader;
    move();
  }

  public void start() {
    if(token.getTag() == Tag.NUM || token.getTag() == Tag.LPT) {
      expr();
      match(Tag.EOF);
    } else error("start error");
  }

  private void expr() {
    if(token.getTag() == Tag.NUM || token.getTag() == Tag.LPT) {
      term();
      exprp();
    } else error("expr error");
  }

  private void exprp() {
    switch (token.getTag()) {
      case Tag.PLS:
        match(Tag.PLS);
        term();
        exprp();
        break;
      case Tag.MIN:
        match(Tag.MIN);
        term();
        exprp();
        break;
      case Tag.EOF:
      case Tag.RPT:
        break;
      default:
        error("exprp error");
    }
  }

  private void term() {
    if(token.getTag() == Tag.NUM || token.getTag() == '(') {
      fact();
      termp();
    } else error("term error");
  }

  private void termp() {
    switch(token.getTag()) {
      case Tag.MUL:
        match(Tag.MUL);
        fact();
        termp();
        break;
      case Tag.DIV:
        match(Tag.DIV);
        fact();
        termp();
        break;
      case Tag.PLS:
      case Tag.MIN:
      case Tag.RPT:
      case Tag.EOF:
        break;
      default:
        error("termp error");
    }
  }
  
  private void fact() {
    if (token.getTag() == Tag.LPT) {
      match(Tag.LPT);
      expr();
      match(Tag.RPT);
    } else if (token.getTag() == Tag.NUM) {
      match(Tag.NUM);
    } else error("fact error: number or '(' expected");
  }

  private void move() {
    token = lexer.scan(bReader);
    System.out.println("token = " + token);
  }
  
  private void error(String s) {
    throw new RuntimeException("near line " + lexer.getLine() + ": " + s);
  }
    
  private void match(int t) {
    if (token.getTag() == t) {
      if (token.getTag() != Tag.EOF) move();
    } else error("syntax error, '" + t + "' expected");
  }

  // -------------------------------------
	// Application Main
  public static void main(String[] args) {
    Lexer lexer = new Lexer();

    try (FileReader fReader = new FileReader(args[0]);
          BufferedReader bReader = new BufferedReader(fReader)) {
     
      Parser parser = new Parser(lexer, bReader);
      parser.start();

      System.out.println("Input OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}