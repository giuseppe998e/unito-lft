package lexer.component;

public class Word extends Token {
  protected final String value;

  public Word (String value, int tag) {
    super(tag);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public String toString() {
    String string = String.format("{tag: %3d, value: '%s'}", tag, value);
    return string; 
  }
}