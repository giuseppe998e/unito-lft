public class Token {
  public final int tag;

  public Token(int tag) { 
    this.tag = tag;
  }

  public String toString() { 
    return "<" + tag + ">";
  }

  // --------------------------------------------
  // Known Tokens
  public static final Token NOT = new Token('!'),
                            LPT = new Token('('),
                            RPT = new Token(')'),
                            LPG = new Token('{'),
                            RPG = new Token('}'),
                            PLS = new Token('+'),
                            MIN = new Token('-'),
                            MUL = new Token('*'),
                            DIV = new Token('/'),
                            ASN = new Token('='),
                            SCL = new Token(';'),
                            EOF = new Token(Tag.EOF);
}