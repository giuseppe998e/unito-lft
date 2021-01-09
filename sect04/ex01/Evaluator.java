import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import lexer.Lexer;
import lexer.component.Tag;
import lexer.component.Token;
import lexer.component.Number;

class Evaluator {
  private final BufferedReader bReader;
  private final Lexer lexer;
  private Token token;

  public Evaluator(Lexer lexer, BufferedReader bReader) {
    this.lexer = lexer;
    this.bReader = bReader;
    move();
  }

  public int start() {
    int exprVal = expr();
    match(Tag.EOF);

    return exprVal;
  }

  private int expr() {
    int termVal = term();
    return exprp(termVal);
  }

  private int exprp(int inputVal) {
    int termVal = 0;

    switch (token.getTag()) {
      case Tag.ADD:
        match(Tag.ADD);
        termVal = term();
        return exprp(inputVal + termVal);
      case Tag.SUB:
        match(Tag.SUB);
        termVal = term();
        return exprp(inputVal - termVal);
      default:
        return inputVal;
    }
  }

  private int term() {
    int factVal = fact();
    return termp(factVal);
  }

  private int termp(int inputVal) {
    int factVal = 0;

    switch (token.getTag()) {
      case Tag.MUL:
        match(Tag.MUL);
        factVal = fact();
        return termp(inputVal * factVal);
      case Tag.DIV:
        match(Tag.DIV);
        factVal = fact();
        return termp(inputVal / factVal);
      default:
        return inputVal;
    }
  }
  
  private int fact() {
    int returnVal = -1;

    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        returnVal = expr();
        match(Tag.RPT);
        break;
      case Tag.NUM:
        returnVal = ((Number) token).getValue();
        match(Tag.NUM);
        break;
      default:
        error("fact() error");
    }

    return returnVal;
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
      
      Evaluator evaluator = new Evaluator(lexer, bReader);
      int evaluatorStart = evaluator.start();

      System.out.println("Input: OK");
      System.out.println("Output: " + evaluatorStart);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}