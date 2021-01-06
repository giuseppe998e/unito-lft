public class Number extends Token {
  public final String number;

  public Number (int tag, String number) { 
    super(tag); 
    this.number = number; 
  }

  public String toString() { 
    return "<" + tag + ", " + number + ">"; 
  }
}