import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import language.Tag;
import language.Token;

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
    switch (token.getTag()) {
      case Tag.LPT:
        int lnextNew = codeGen.newLabel();
        stat(lnextNew);
        codeGen.emitLabel(lnextNew);

        match(Tag.EOF);
        codeGen.toJasmin(outDir);
        break;
      default:
        error("prog() Erroneous char found: " + token);
    }
  }

  private void statlist(int lnext) {
    switch (token.getTag()) {
      case Tag.LPT:
        int lnextNew = codeGen.newLabel();
        stat(lnextNew);
        codeGen.emitLabel(lnextNew);
        statlistp(lnext);
        break;
      default:
        error("statlist() Erroneous char found: " + token);
    }
  }

  private void statlistp(int lnext) {
    switch (token.getTag()) {
      case Tag.LPT: // statlist(lnext)
        int lnextNew = codeGen.newLabel();
        stat(lnextNew);
        codeGen.emitLabel(lnextNew);
        statlistp(lnext);
      case Tag.RPT:
        break;
      default:
        error("statlistp() Erroneous char found: " + token);
    }
  }

  private void stat(int lnext) {
     switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        statp(lnext);
        match(Tag.RPT);
        break;
      default:
        error("stat() Erroneous char found: " + token);
    }
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
        int lcondTrue = codeGen.newLabel(),
            lcondFalse = codeGen.newLabel();
        bexpr(lcondTrue, lcondFalse);     // Check condition
        codeGen.emitLabel(lcondTrue);     // If TRUE, "continue"...
        stat(lnext);                      // <-/
        codeGen.emit(OpCode.GOto, lnext); // ...and jump to the next statement
        codeGen.emitLabel(lcondFalse);    // If FALSE, jump
        elseopt(lnext);                   // <-/
        break;
      case Tag.WHILE:
        match(Tag.WHILE);
        int lwhileTrue = codeGen.newLabel(),
            lwhileLoop = codeGen.newLabel();
        codeGen.emitLabel(lwhileLoop);        // While (TRUE) {...
        bexpr(lwhileTrue, lnext);             // Check condition
        codeGen.emitLabel(lwhileTrue);        // If TRUE, "continue"
        stat(lnext);                          //<-/
        codeGen.emit(OpCode.GOto, lwhileLoop); // ...} do  // Next Label - 1 == Actual label
        // (If FALSE, jump to "lnext")
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
        error("statp() Erroneous char found: " + token);
    }
  }

  private void elseopt(int lnext) {
    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        match(Tag.ELSE);
        stat(lnext);
        match(Tag.RPT);
        break;
      case Tag.RPT:
        break;
      default:
        error("elseopt() Erroneous char found: " + token);
    }
  }

  private void bexpr(int ltrue, int lfalse) {
    switch (token.getTag()) {
      case Tag.LPT:
        match(Tag.LPT);
        bexprp(ltrue, lfalse);
        match(Tag.RPT);
        break;
      default:
        error("bexpr() Erroneous char found: " + token);
    }
  }

  private void bexprp(int ltrue, int lfalse) {
    switch (token.getTag()) {
      case Tag.AND:
        match(Tag.AND);

        int lbexprpTrue = codeGen.newLabel();
        bexpr(lbexprpTrue, lfalse);
        codeGen.emitLabel(lbexprpTrue);
        bexpr(ltrue, lfalse);

        return;
      case Tag.OR:
        match(Tag.OR);

        int lbexprpFalse = codeGen.newLabel();
        bexpr(ltrue, lbexprpFalse);
        codeGen.emitLabel(lbexprpFalse);
        bexpr(ltrue, lfalse);

        return;
      case Tag.NOT:
        match(Tag.NOT);

        bexpr(lfalse, ltrue);

        return;
    }

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
        default:
          error("bexprp(type) Erroneous char found: " + token);
      }

      codeGen.emit(OpCode.GOto, lfalse);
    } else {
      error("bexprp() Erroneous char found: " + token);
    }
  }

  private void expr() {
    switch (token.getTag()) {
      case Tag.NUM:
        int numVal = matchNUM();
        codeGen.emit(OpCode.ldc, numVal);
        break;
      case Tag.ID:
        String identifier = token.getLexeme();
        int idAddr = symTable.lookupAddress(identifier);
        if (idAddr == -1) error("expr() Identifier not found: " + identifier);
        match(Tag.ID);
        codeGen.emit(OpCode.iload, idAddr);
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
