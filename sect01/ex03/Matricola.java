import java.util.Scanner;
import java.lang.Character;

public class SerialNumber {
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
					if (Character.isDigit(ch))
					  state = (intValue(ch) % 2 != 0) ? 1 : 2;
          else state = -1;
					break;
				case 1: // Last odd digit
					if (Character.isLetter(ch))
						state = (ch >= 'l' && ch <= 'z') ? 3 : -1; // L-Z
					else if(Character.isDigit(ch)) 
						if (intValue(ch) % 2 == 0) state = 2;
					else state = -1;
					break;
				case 2:  // Last even digit
					if (Character.isLetter(ch))
						state = (ch >= 'a' && ch <= 'k') ? 3 : -1; // A-K
					else if(Character.isDigit(ch)) 
						if(intValue(ch) % 2 != 0) state = 1;
					else state = -1;
					break;
				case 3: // Insertion -> No Numbers -> Only Letters
					if (!Character.isLetter(ch)) state = -1;
					break;
      }
    }

    return state == 3;
	}
	
	private static int intValue(char ch) {
		return Character.getNumericValue(ch);
	}
}