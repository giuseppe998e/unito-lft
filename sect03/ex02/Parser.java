import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

public class Parser {
  private Lexer lexer;
  private BufferedReader reader;
  private Token token;

  public Parser(Lexer lexer, BufferedReader reader) {
    this.lexer = lexer;
    this.reader = reader;
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
    switch(token.getTag()) {
      case Tag.ASN: // =
        match(Token.ASN.getTag());
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
      if (token.getTag() == Tag.ELSE) {
        match(Tag.ELSE);
        stat();
        match(Tag.RPT);
      } else {
        error("elseopt() error");
      }
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
    switch(token.getTag()) {
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
    switch(token.getTag()) {
      case Tag.PLS: // +
        match(Token.PLS.getTag());
        exprlist();
        break;
      case Tag.MIN: // -
        match(Token.MIN.getTag());
        expr();
        expr();
        break;
      case Tag.MUL: // *
        match(Token.MUL.getTag());
        exprlist();
        break;
      case Tag.DIV: // /
        match(Token.DIV.getTag());
        expr();
        expr();
        break;
      default:
        error("exprp() error");
    }
  }

  private void exprlist() {
    switch(token.getTag()) {
      case Tag.NUM:
      case Tag.ID:
      case Tag.LPT:
        expr();
        exprlistp();
        break;
      default:
        error("exprlist() error");
    }
  }

  private void exprlistp() {
    switch(token.getTag()) {
      case Tag.NUM:
      case Tag.ID:
      case Tag.LPT:
        expr();
        exprlistp();
        break;
      default:
        if (token.getTag() != Tag.RPT) {
          error("exprlistp() error");
        }
    }
  }

  private void move() {
    token = lexer.scan(reader);
    System.out.println("token = " + token);
  }
    
  private void match(int t) {
    if (token.getTag() == t) {
      if (token.getTag() != Tag.EOF) move();
    } else error("syntax error, '" + t + "' expected, '" + token.getTag() + "' found");
  }

  private void error(String s) {
    throw new RuntimeException("near line " + lexer.getLine() + ": " + s);
  }

  // --------------------------------------------
  // Main static method

  public static void main(String[] args) {
    Lexer lexer = new Lexer();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      Parser parser = new Parser(lexer, reader);
      
      parser.prog();
      reader.close();
      System.out.println("Input OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}