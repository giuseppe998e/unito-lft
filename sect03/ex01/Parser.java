import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

// TODO
public class Parser {
  private Lexer lexer;
  private BufferedReader reader;
  private Token token;

  public Parser(Lexer lexer, BufferedReader reader) {
    this.lexer = lexer;
    this.reader = reader;
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
    token = lexer.scan(reader);
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

  // --------------------------------------------
  // Main static method

  public static void main(String[] args) {
    Lexer lexer = new Lexer();

    try {
      BufferedReader reader = new BufferedReader(new FileReader(args[0]));
      Parser parser = new Parser(lexer, reader);
      
      parser.start();
      System.out.println("Input OK");
      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}