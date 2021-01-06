import java.util.Scanner;
import java.lang.Character;

public class MatricolaSpazi {
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
						if (ch % 2 != 0) state = 1;
						else state = 2;
					else if (ch != ' ')	state = -1;
					break;
				case 1: // Last odd digit
					if (Character.isWhitespace(ch)) state = 4;
					else if (Character.isDigit(ch)) 
						if (ch % 2 == 0) state = 2;
					else state = -1;
					break;
				case 2: // Last even digit
					if (Character.isWhitespace(ch)) state = 4;
					else if (Character.isDigit(ch)) 
						if (ch % 2 != 0) state = 1;
					else state = -1;
					break;
				case 3: // Case of space / odd
					if (Character.isLetter(ch))
						if (ch >= 'l' && ch <= 'z') state = 5;
						else state=-1;
					else if (ch!=' ') state = -1;
					break;
				case 4: // Case of space / even
					if (Character.isLetter(ch))
						if (ch >= 'a' && ch <= 'k') state = 5;
						else state = -1;
					else if (ch != ' ') state = -1;
					break;
				case 5:
					if (!(Character.isLetter(ch) || ch == ' ')) state = -1;
					break;
      }
    }

    return state == 5;
  }
}