package language;

public class Word extends Token {
  protected final String lexeme;

  public Word (String lexeme, int tag) {
    super(tag);
    this.lexeme = lexeme;
  }

  public String getLexeme() {
    return lexeme;
  }

  public String toString() {
    String string = String.format("{tag: %3d, lexeme: '%s'}", tag, lexeme);
    return string; 
  }
}
