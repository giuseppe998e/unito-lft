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

  public void prog() {
    switch (token.getTag()) {
      case Tag.LPT:
        stat();
        match(Tag.EOF);
        break;
      default:
        error("start() Erroneous char found: " + token);
    }
  }

  private void statlist() {
    switch (token.getTag()) {
      case Tag.LPT:
        stat();
        statlistp();
      default:
        error("statlist() Erroneous char found: " + token);
    }
  }

  private void statlistp() {
    switch (token.getTag()) {
      case Tag.LPT:
        stat();
        statlistp();
      case Tag.RPT:
        break;
      default:
        error("statlistp() Erroneous char found: " + token);
    }
  }

  private void stat() {
    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        statp();
        match(Tag.RPT);
        break;
      default:
        error("stat() Erroneous char found: " + token);
    }
  }

  private void statp() {
    switch (token.getTag()) {
      case Tag.ASN:
        match(Tag.ASN);
        match(Tag.ID);
        expr();
        break;
      case Tag.COND:
        match(Tag.COND);
        bexpr();
        stat();
        elseopt();
        break;
      case Tag.WHILE:
        match(Tag.WHILE);
        bexpr();
        stat();
        break;
      case Tag.DO:
        match(Tag.DO);
        statlist();
        break;
      case Tag.PRINT:
        match(Tag.PRINT);
        exprlist();
        break;
      case Tag.READ:
        match(Tag.READ);
        match(Tag.ID);
        break;
      default:
        error("statp() Erroneous char found: " + token);
    }
  }
  
  private void elseopt() {
    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        match(Tag.ELSE);
        stat();
        match(Tag.RPT);
        break;
      case Tag.RPT:
        break;
      default:
        error("elseopt() Erroneous char found: " + token);
    }
  }

  private void bexpr() {
    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        bexprp();
        match(Tag.RPT);
        break;
      default:
        error("bexpr() Erroneous char found: " + token);
    }
  }

  private void bexprp() {
    switch (token.getTag()) {
      case Tag.RELOP:
        match(Tag.RELOP);
        expr();
        expr();
        break;
      default:
        error("bexprp() Erroneous char found: " + token);
    }
  }

  private void expr() {
    switch (token.getTag()) {
      case Tag.NUM:
        match(Tag.NUM);
        break;
      case Tag.ID:
        match(Tag.ID);
        break;
      case Tag.LPT:
        match(Tag.LPT);
        exprp();
        match(Tag.RPT);
        break;
      default:
        error("expr() Erroneous char found: " + token);
    }
  }

  private void exprp() {
    switch (token.getTag()) {
      case Tag.ADD:
        match(Tag.ADD);
        exprlist();
        break;
      case Tag.SUB:
        match(Tag.SUB);
        expr();
        expr();
        break;
      case Tag.MUL:
        match(Tag.MUL);
        exprlist();
        break;
      case Tag.DIV:
        match(Tag.DIV);
        expr();
        expr();
        break;
      default:
        error("exprp() Erroneous char found: " + token);
    }
  }

  private void exprlist() {
    switch (token.getTag()) {
      case Tag.NUM:
      case Tag.ID:
      case Tag.LPT:
        expr();
        exprlistp();
        break;
      default:
        error("exprlist() Erroneous char found: " + token);
    }
  }

  private void exprlistp() {
    switch (token.getTag()) {
      case Tag.NUM:
      case Tag.ID:
      case Tag.LPT:
        expr();
        exprlistp();
        break;
      case Tag.RPT:
        break;
      default:
        error("exprlistp() Erroneous char found: " + token);
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
      parser.prog();

      System.out.println("Input: OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
