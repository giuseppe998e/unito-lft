import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import language.Tag;
import language.Token;

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
    switch (token.getTag()) {
      case Tag.LPT:
      case Tag.NUM:
        int exprVal = expr();
        match(Tag.EOF);
        return exprVal;
      default:
        error("start() Erroneous char found: " + token);
        return -1;
    }
  }

  private int expr() {
    switch (token.getTag()) {
      case Tag.LPT:
      case Tag.NUM:
        int termVal = term();
        return exprp(termVal);
      default:
        error("expr() Erroneous char found: " + token);
        return -1;
    }
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
      case Tag.RPT:
      case Tag.EOF:
        return inputVal;
      default:
        error("exprp() Erroneous char found: " + token);
        return -1;
    }
  }

  private int term() {
    switch (token.getTag()) {
      case Tag.LPT:
      case Tag.NUM:
        int factVal = fact();
        return termp(factVal);
      default:
        error("term() Erroneous char found: " + token);
        return -1;
    }
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
      case Tag.ADD:
      case Tag.SUB:
      case Tag.RPT:
      case Tag.EOF:
        return inputVal;
      default:
        error("termp() Erroneous char found: " + token);
        return -1;
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
        returnVal = token.getValue();
        match(Tag.NUM);
        break;
      default:
        error("fact() Erroneous char found: " + token);
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
