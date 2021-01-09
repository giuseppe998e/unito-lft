package lexer.component;

public final class Identifier extends Word {
  public Identifier (String value) { 
    super(value, Tag.ID);
  }
}