public class Word extends Token {
  public final String lexeme;

  public Word(int tag, String lexeme) { 
    super(tag); 
    this.lexeme = lexeme; 
  }

  public String toString() {
    return "<" + tag + ", " + lexeme + ">";
  }

  // --------------------------------------------
  // Known Words
  public static final Word COND = new Word(Tag.COND, "cond"),
                           THEN = new Word(Tag.THEN, "then"),
                           WHEN = new Word(Tag.WHEN, "when"),
                           ELSE = new Word(Tag.ELSE, "else"),
                           WHILE = new Word(Tag.WHILE, "while"),
                           DO = new Word(Tag.DO, "do"),
                           SEQ = new Word(Tag.SEQ, "seq"),
                           PRINT = new Word(Tag.PRINT, "print"),
                           READ = new Word(Tag.READ, "read"),
                           OR = new Word(Tag.OR, "||"),
                           AND = new Word(Tag.AND, "&&"),
                           LT = new Word(Tag.RELOP, "'<'"),
                           GT = new Word(Tag.RELOP, "'>'"),
                           EQ = new Word(Tag.RELOP, "=="),
                           LE = new Word(Tag.RELOP, "<="),
                           NE = new Word(Tag.RELOP, "<>"),
                           GE = new Word(Tag.RELOP, ">=");
}