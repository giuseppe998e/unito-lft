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
    int returnVal = 0;

    if(token.getTag() == Tag.NUM || token.getTag() == Tag.LPT) {
      returnVal = expr();
      match(Tag.EOF);
    } else {
      error("start() error");
    }

    return returnVal;
  }

  private int expr() {
    int returnVal = 0;
    
    if(token.getTag() == Tag.NUM || token.getTag() == Tag.LPT) {
      int termVal = term();
      returnVal = exprp(termVal);
    } else {
      error("expr() error");
    }

    return returnVal;
  }

  private int exprp(int inputVal) {
    int returnVal = 0;

    switch (token.getTag()) {
      case Tag.PLS:
        match(Tag.PLS);
        int termVal1 = term();
        returnVal = exprp(inputVal + termVal1);
        break;
      case Tag.MIN:
        match(Tag.MIN);
        int termVal2 = term();
        returnVal = exprp(inputVal - termVal2);
        break;
      case Tag.EOF:
      case Tag.RPT:
        returnVal = inputVal;
        break;
      default:
        error("exprp() error");
    }

    return returnVal;
  }

  private int term() {
    int returnVal = 0;

    if(token.getTag() == Tag.NUM || token.getTag() == '(') {
      int factVal = fact();
      returnVal = termp(factVal);
    } else {
      error("term() error");
    }

    return returnVal;
  }

  private int termp(int inputVal) {
    int returnVal = 0;

    switch(token.getTag()) {
      case Tag.MUL:
        match(Tag.MUL);
        int factVal1 = fact();
        returnVal = termp(inputVal * factVal1);
        break;
      case Tag.DIV:
        match(Tag.DIV);
        int factVal2 = fact();
        returnVal = termp(inputVal / factVal2);
        break;
      case Tag.PLS:
      case Tag.MIN:
      case Tag.RPT:
      case Tag.EOF:
        returnVal = inputVal;
        break;
      default:
        error("termp() error");
    }

    return returnVal;
  }
  
  private int fact() {
    int returnVal = 0;

    if (token.getTag() == Tag.NUM) {
      returnVal = ((Number) token).getValue();
      match(Tag.NUM);
    } else if (token.getTag() == Tag.LPT) {
      match(Tag.LPT);
      returnVal = expr();
      match(Tag.RPT);
    } else {
      error("fact() error: NUMBER or '(' expected");
    }

    return returnVal;
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
      
      Evaluator evaluator = new Evaluator(lexer, bReader);
      int evaluatorStart = evaluator.start();

      System.out.println("Input: OK");
      System.out.println("Output: " + evaluatorStart);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}