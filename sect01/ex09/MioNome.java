import java.util.Scanner;
import java.lang.Character;

public class MioNome {
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
        case 0: // Greek epsilon
          state = (ch == 'g') ? 1 : 11;
          break;
        
        case 1: // G
          state = (ch == 'i') ? 2 : 22;
          break;
        case 11: // G fallback
          state = (ch == 'i') ? 22 : -1;
          break;

        case 2: // I
          state = (ch == 'u') ? 3 : 33;
          break;
        case 22: // I fallback
          state = (ch == 'u') ? 33 : -1;
          break;

        case 3: // U
          state = (ch == 's') ? 4 : 44;
          break;
        case 33: // U fallback
          state = (ch == 's') ? 44 : -1;
          break;

        case 4: // S
          state = (ch == 'e') ? 5 : 55;
          break;
        case 44: // S fallback
          state = (ch == 'e') ? 55 : -1;
          break;

        case 5: // E
          state = (ch == 'p') ? 6 : 66;
          break;
        case 55: // E fallback
          state = (ch == 'p') ? 66 : -1;
          break;

        case 6: // P
          state = (ch == 'p') ? 7 : 77;
          break;
        case 66: // P fallback
          state = (ch == 'p') ? 77 : -1;
          break;

        case 7: // P
          state = (ch == 'e') ? 8 : 88;
          break;
        case 77: // P fallback
          state = (ch == 'e') ? 88 : -1;
          break;

        case 8:  // *E
        case 88: // *E fallback  // <- Optional
          state = -1;
          break;
      }
    }

    return state == 8 || state == 88;
  }
}
