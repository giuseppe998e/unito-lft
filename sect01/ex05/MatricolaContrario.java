import java.util.Scanner;
import java.lang.Character;

public class MatricolaContrario {
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
          if (Character.isLetter(ch))
            if (ch >= 'a' && ch <= 'k') state = 1;
            else state = 2;
          else state = -1;
          break;
        case 1: // A-K
          if (Character.isDigit(ch))
            if (intValue(ch) % 2 == 0) state = 3;
            else state = 5;
          else if (!Character.isLetter(ch)) state = -1;
          break;
        case 2: // L-Z
          if (Character.isDigit(ch))
            if (intValue(ch) % 2 != 0) state = 4;
            else state = 6;
          else if (!Character.isLetter(ch)) state = -1;
          break;
        case 3: 
          if (Character.isDigit(ch)) 
            if (intValue(ch) % 2 != 0) state = 4;
          else state = -1;
          break;
        case 4:
          if (Character.isDigit(ch)) 
            if (intValue(ch) % 2 == 0) state = 6;
          else state = -1;
          break;
        case 5:
          if (Character.isDigit(ch))
            if (intValue(ch) % 2 == 0) state = 3;
          else state = -1;
          break;
        case 6:
          if (Character.isDigit(ch)) 
            if (intValue(ch) % 2 != 0) state = 4;
          else state = -1;
          break;
     }
    }
 
    return state == 3 || state == 4;
   }

   private static int intValue(char ch) {
    return Character.getNumericValue(ch);
  }
}