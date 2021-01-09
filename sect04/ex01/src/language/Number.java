package language;

public final class Number extends Token {
  private final int value;

  public Number (int value) { 
    super(Tag.NUM); 
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public String toString() {
    String string = String.format("{tag: %3d, value: %d}", tag, value);
    return string; 
  }
}
