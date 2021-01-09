import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import lexer.Lexer;
import lexer.component.Tag;
import lexer.component.Token;

public class Translator {
  private final BufferedReader bReader;
	private final SymbolTable symTable;
	private final CodeGenerator codeGen;
  private final Lexer lexer;
  private Token token;

	public Translator(Lexer lexer, BufferedReader bReader) {
		this.symTable = new SymbolTable();
		this.codeGen = new CodeGenerator();

		this.lexer = lexer;
		this.bReader = bReader;

		move();
	}

  public void prog(String fileOut) {
    int lnextProg = codeGen.newLabel();
    stat();
    codeGen.emitLabel(lnextProg);
    
    match(Tag.EOF);
    codeGen.toJasmin(fileOut);
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
        int idAddr1 = matchID();
        expr();
        codeGen.emit(OpCode.istore, idAddr1);
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
        codeGen.emit(OpCode.invokestatic, 1);
        break;
      case Tag.READ:
        match(Tag.READ);
        int idAddr2 = matchID();
        codeGen.emit(OpCode.invokestatic, 0);
        codeGen.emit(OpCode.istore, idAddr2);
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
        int numVal = matchNUM();
        codeGen.emit(OpCode.ldc, numVal);
        break;
      case Tag.ID:
        int idAddr = matchID();
        codeGen.emit(OpCode.iload, idAddr);
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
      case Tag.ADD:
        match(Tag.ADD);
        exprlist();
        codeGen.emit(OpCode.iadd);
        break;
      case Tag.SUB:
        match(Tag.SUB);
        expr();
        expr();
        codeGen.emit(OpCode.isub);
        break;
      case Tag.MUL:
        match(Tag.MUL);
        exprlist();
        codeGen.emit(OpCode.imul);
        break;
      case Tag.DIV:
        match(Tag.DIV);
        expr();
        expr();
        codeGen.emit(OpCode.idiv);
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
  private int matchID() {
    String identifier = token.getLexeme()
    int idAddr = symTable.lookupAddress(identifier);
    if (idAddr == -1) {
      idAddr = symTable.insert(identifier);
    }

    match(Tag.ID);
    return idAddr;
  }

  private int matchNUM() {
    int numVal = token.getValue();
    match(Tag.NUM);
    return numVal;
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
      
      Translator translator = new Translator(lexer, bReader);

      String fileOut = (args.length > 1) ? args[1] : "Output.j";
      translator.prog(fileOut);

      System.out.println("Input: OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}