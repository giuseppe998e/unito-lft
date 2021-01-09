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

  public void prog(String outDir) {
    int lnextNew = codeGen.newLabel();
    stat(lnextNew);
    codeGen.emitLabel(lnextNew);

    match(Tag.EOF);
    codeGen.toJasmin(outDir);
  }

  private void statlist(int lnext) {
    int lnextNew = codeGen.newLabel();
    stat(lnextNew);
    codeGen.emitLabel(lnextNew);

    statlistp(lnext);
  }

  private void statlistp(int lnext) {
    if (token.getTag() == Tag.LPT) {
      /*
        int lnextNew = codeGen.newLabel();
        stat(lnextNew);
        codeGen.emitLabel(lnextNew);
    
        statlistp(lnext);
      == */ statlist(lnext);
    } // else EPSILON
  }

  private void stat(int lnext) {
    match(Tag.LPT);
    statp(lnext);
    match(Tag.RPT);
  }

  private void statp(int lnext) {
    switch (token.getTag()) {
      case Tag.ASN:
        match(Tag.ASN);
        int idAddr1 = matchID();
        expr();
        codeGen.emit(OpCode.istore, idAddr1);
        break;
      case Tag.COND:
        match(Tag.COND);
        int lcondTrue  = codeGen.newLabel(),
            lcondFalse = codeGen.newLabel();
        bexpr(lcondTrue, lcondFalse);  // If true, "continue"
        codeGen.emitLabel(lcondTrue);  // <-/
        stat(lcondTrue);               //
        codeGen.emitLabel(lcondFalse); // If false, jump here
        elseopt(lcondFalse);           //
        break;
      case Tag.WHILE:
        match(Tag.WHILE);
        int lwhileTrue = codeGen.newLabel(),
            lwhileLoop = codeGen.newLabel();
        codeGen.emitLabel(lwhileLoop);         // While...
        bexpr(lwhileTrue, lnext);              // If true, "continue"
        codeGen.emitLabel(lwhileTrue);         // <-/ 
        stat(lwhileLoop);                      //
        codeGen.emit(OpCode.GOto, lwhileLoop); // ...do
        break;
      case Tag.DO:
        match(Tag.DO);
        statlist(lnext);
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
  
  private void elseopt(int lnext) {
    if (token.getTag() == Tag.LPT) {
      match(Tag.LPT);
      match(Tag.ELSE);
      stat(lnext);
      match(Tag.RPT);
    } // else EPSILON
  }

  private void bexpr(int ltrue, int lfalse) {
    match(Tag.LPT);
    bexprp(ltrue, lfalse);
    match(Tag.RPT);
  }

  private void bexprp(int ltrue, int lfalse) {
    if (token.getTag() == Tag.RELOP) {
      String type = token.getLexeme();
      match(Tag.RELOP);
      expr();
      expr();

      switch (type) {
        case "<":
          codeGen.emit(OpCode.if_icmplt, ltrue);
          break;
        case ">":
          codeGen.emit(OpCode.if_icmpgt, ltrue);
          break;
        case "<=":
          codeGen.emit(OpCode.if_icmple, ltrue);
          break;
        case ">=":
          codeGen.emit(OpCode.if_icmpge, ltrue);
          break;
        case "<>":
          codeGen.emit(OpCode.if_icmpne, ltrue);
          break;
        case "==":
          codeGen.emit(OpCode.if_icmpeq, ltrue);
          break;
      }

      codeGen.emit(OpCode.GOto, lfalse);
    } else {
      error("bexprp() error");
    }
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
        /*expr();
        exprlistp();
        ==*/ exprlist();
    }

    if (token.getTag() == Tag.LPT) {
      match(Tag.RPT);
    }
  }

  // -------------------------------------
  // Util methods
  private int matchID() {
    String identifier = token.getLexeme();
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
      
      Translator translator = new Translator(lexer, bReader);

      String outDir = (args.length > 1) ? args[1] + "/" : "";
      translator.prog(outDir);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}