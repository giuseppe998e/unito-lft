import java.util.Scanner;
import java.lang.Character;

public class Identificatore {
  public static void main(String[] args) {
    Scanner keyboard = new Scanner(System.in);

    System.out.print("Please enter a string: ");
    String inputStr = keyboard.nextLine();
    keyboard.close();
    
    System.out.println("Is input correct? " + (scan(inputStr) ? "YES" : "NOPE"));
    System.out.println("");
  }
  
  public static boolean scan(String s) {
    int sLength = s.length();
    int state = 0;
    int i = 0;
    
    while (state >= 0 && i < sLength) {
      final char ch = s.charAt(i++);
    
      switch (state) {
        case 0:
          if (Character.isLetter(ch)) state = 2;
          else if (ch == '_') state = 1;
          else state = -1; 
          break;
        case 1:
          if (Character.isLetter(ch) || Character.isDigit(ch)) state = 2;
          else state = -1;
          break;
        case 2:
          if (!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) state -= 1;
          break;
      }
    }

    return state == 2;
  }
}
