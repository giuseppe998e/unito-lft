import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import language.Tag;
import language.Token;

public class Parser {
  private final BufferedReader bReader;
  private final Lexer lexer;
  private Token token;

  public Parser(Lexer lexer, BufferedReader bReader) {
    this.lexer = lexer;
    this.bReader = bReader;
    move();
  }

  public void start() {
    expr();
    match(Tag.EOF);
  }

  private void expr() {
    term();
    exprp();
  }

  private void exprp() {
    switch (token.getTag()) {
      case Tag.ADD:
        match(Tag.ADD);
        term();
        exprp();
        break;
      case Tag.SUB:
        match(Tag.SUB);
        term();
        exprp();
        break;
    }
  }

  private void term() {
    fact();
    termp();
  }

  private void termp() {
    switch (token.getTag()) {
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
    }
  }
  
  private void fact() {
    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        expr();
        match(Tag.RPT);
        break;
      case Tag.NUM:
        match(Tag.NUM);
        break;
      default:
        error("fact() error");
    }
  }

  // -------------------------------------
  // Util methods
  private void match(int t) {
    if (token.getTag() != t) {
      error("Syntax error: '" + t + "' expected, '" + token.getTag() + "' found");
    }

    if (token.getTag() != Tag.EOF) {
      move();
    }
  }

  private void move() {
    token = lexer.scan(bReader);
    // System.out.println("token = " + token);
  }

  private void error(String s) {
    throw new RuntimeException("Near line " + lexer.getLine() + ": " + s);
  }

  // -------------------------------------
  // Application Main
  public static void main(String[] args) {
    Lexer lexer = new Lexer();

    try (FileReader fReader = new FileReader(args[0]);
          BufferedReader bReader = new BufferedReader(fReader)) {
     
      Parser parser = new Parser(lexer, bReader);
      parser.start();

      System.out.println("Input: OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
