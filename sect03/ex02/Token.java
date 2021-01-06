public class Token {
  private final int tag;

  public Token(int tag) { 
    this.tag = tag;
  }

  public final int getTag() {
    return tag;
  }

  public String toString() { 
    return "<" + getTag() + ">";
  }

  // --------------------------------------------
  // Known Tokens
  public static final Token NOT = new Token(Tag.NOT),
                            LPT = new Token(Tag.LPT),
                            RPT = new Token(Tag.RPT),
                            LPG = new Token(Tag.LPG),
                            RPG = new Token(Tag.RPG),
                            PLS = new Token(Tag.PLS),
                            MIN = new Token(Tag.MIN),
                            MUL = new Token(Tag.MUL),
                            DIV = new Token(Tag.DIV),
                            ASN = new Token(Tag.ASN),
                            SCL = new Token(Tag.SCL),
                            EOF = new Token(Tag.EOF);
}