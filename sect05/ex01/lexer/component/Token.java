package lexer.component;

import java.lang.UnsupportedOperationException;

public class Token {
  protected final int tag;

  public Token(int tag) {
    this.tag = tag;
  }

  public int getTag() {
    return tag;
  }

  public int getValue() {
    throw new UnsupportedOperationException("Not a number token");
  }

  public String getLexeme() {
    throw new UnsupportedOperationException("Not a word token");
  }

  public String toString() {
    String string = String.format("{tag: %3d, char: '%c'}", tag, (char)tag);
    return string; 
  }

  // -------------------------------------------------------
  // STATIC TOKENS          // Single Char Tokens
  public static final Token NOT   = new Token(Tag.NOT),
                            LPT   = new Token(Tag.LPT),
                            RPT   = new Token(Tag.RPT),
                            LPG   = new Token(Tag.LPG),
                            RPG   = new Token(Tag.RPG),
                            ADD   = new Token(Tag.ADD),
                            SUB   = new Token(Tag.SUB),
                            MUL   = new Token(Tag.MUL),
                            DIV   = new Token(Tag.DIV),
                            ASN   = new Token(Tag.ASN),
                            SCL   = new Token(Tag.SCL),
                            LT    = new Word("<",   Tag.RELOP),
                            GT    = new Word(">",   Tag.RELOP),
                            EOF   = new Word("EOF", Tag.EOF),
                            // Double Char Tokens
                            EQ    = new Word("==", Tag.RELOP),
                            LE    = new Word("<=", Tag.RELOP),
                            NE    = new Word("<>", Tag.RELOP),
                            GE    = new Word(">=", Tag.RELOP),
                            OR    = new Word("||", Tag.OR),
                            AND   = new Word("&&", Tag.AND),
                            // Word Tokens
                            COND  = new Word("cond",  Tag.COND),
                            WHEN  = new Word("when",  Tag.WHEN),
                            THEN  = new Word("then",  Tag.THEN),
                            ELSE  = new Word("else",  Tag.ELSE),
                            WHILE = new Word("while", Tag.WHILE),
                            DO    = new Word("do",    Tag.DO),
                            SEQ   = new Word("seq",   Tag.SEQ),
                            PRINT = new Word("print", Tag.PRINT),
                            READ  = new Word("read",  Tag.READ);
}