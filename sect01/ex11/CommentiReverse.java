import java.util.Scanner;
import java.lang.Character;

public class CommentiReverse {
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
          if (ch == '/') state = 1;
          else if (ch != '*' && ch != 'a') state = -1;
          break;
        case 1: 
          if (ch == '*') state = 2;
          else if (ch == 'a') state = 0;
          else if (ch != '/') state = -1;
          break;
        case 2: 
          if (ch == '*') state = 3;
          else if (!(ch == '/' || ch == 'a')) state = -1;
          break;
        case 3:
          if (ch == '/') state = 0;
          else if (ch == 'a') state = 2;
          else if (!(ch == '*')) state = -1;
          break;
      }
    }

    return state == 0 || state == 1;  
  }
}
