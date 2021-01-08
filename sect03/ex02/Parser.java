import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import lexer.Lexer;
import lexer.component.Tag;
import lexer.component.Token;

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
    stat();
    match(Tag.EOF);
  }

  private void statlist() {
    stat();
    statlistp();
  }

  private void statlistp() {
    if (token.getTag() == Tag.LPT) {
      stat();
      statlistp();
    } // else EPSILON
  }

  private void stat() {
    match(Tag.LPT);
    statp();
    match(Tag.RPT);
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
        error("statp() error");
    }
  }
  
  private void elseopt() {
    if (token.getTag() == Tag.LPT) {
      match(Tag.LPT);
      match(Tag.ELSE);
      stat();
      match(Tag.RPT);
    } // else EPSILON
  }

  private void bexpr() {
    match(Tag.LPT);
    bexprp();
    match(Tag.RPT);
  }

  private void bexprp() {
    match(Tag.RELOP);
    expr();
    expr();
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
        error("expr() error");
    }
  }

  private void exprp() {
    switch (token.getTag()) {
      case Tag.PLS:
        match(Tag.PLS);
        exprlist();
        break;
      case Tag.MIN:
        match(Tag.MIN);
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
        error("exprp() error");
    }
  }

  private void exprlist() {
    expr();
    exprlistp();
  }

  private void exprlistp() {
    switch (token.getTag()) {
      case Tag.NUM:
      case Tag.ID:
      case Tag.LPT:
        expr();
        exprlistp();
    }

    if (token.getTag() == Tag.LPT) {
      match(Tag.RPT);
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