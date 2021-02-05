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
    switch (token.getTag()) {
      case Tag.LPT:
      case Tag.NUM:
        expr();
        match(Tag.EOF);
        break;
      default:
        error("start() Erroneous char found: " + token);
    }
  }

  private void expr() {
    switch (token.getTag()) {
      case Tag.LPT:
      case Tag.NUM:
        term();
        exprp();
        break;
      default:
        error("expr() Erroneous char found: " + token);
    }
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
      case Tag.RPT:
      case Tag.EOF:
        break;
      default:
        error("exprp() Erroneous char found: " + token);
    }
  }

  private void term() {
    switch (token.getTag()) {
      case Tag.LPT:
      case Tag.NUM:
        fact();
        termp();
        break;
      default:
        error("term() Erroneous char found: " + token);
    }
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
      case Tag.ADD:
      case Tag.SUB:
      case Tag.RPT:
      case Tag.EOF:
        break;
      default:
        error("termp() Erroneous char found: " + token);
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
        error("fact() Erroneous char found: " + token);
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
