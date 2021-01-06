import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;
import java.lang.Character;

public class Lexer {
  private int line;
  private char peek;

  public Lexer() {
    line = 1;
    peek = ' ';
  }

  public Token lexicalScan(BufferedReader br) {
    skipSpaces(br);

    switch (peek) {
      case '!':
        peek = ' ';
        return Token.NOT;
      case '(':
        peek = ' ';
        return Token.LPT;
      case ')':
        peek = ' ';
        return Token.RPT;
      case '{':
        peek = ' ';
        return Token.LPG;
      case '}':
        peek = ' ';
        return Token.RPG;
      case '+':
        peek = ' ';
        return Token.PLS;
      case '-':
        peek = ' ';
        return Token.MIN;
      case '*':
        peek = ' ';
        return Token.MUL;
      case '/':
        peek = ' ';
        return Token.DIV;
      case ';':
        peek = ' ';
        return Token.SCL;
      case '|':
        readCh(br);
        if (peek == '|') {
            peek = ' ';
            return Word.OR;
        } else {
            System.err.println("Erroneous character after | : "  + peek );
            return null;
        }
      case '=':
        readCh(br);
        if (peek == '=') {
            peek = ' ';
            return Word.EQ;
        } else {
            return Token.ASN;
        }
      case '<':
        readCh(br);
        if (peek == '=') {
            peek = ' ';
            return Word.LE;
        } else if (peek == '>') {
            peek = ' ';
            return Word.NE;
        } else {
            return Word.LT;
        }
      case '>':
        readCh(br);
        if (peek == '=') {
            peek = ' ';
            return Word.GE;
        } else {
            return Word.GT;
        }
      case '&':
        readCh(br);
        if (peek == '&') {
          return Word.AND;
        } else {
          System.err.println("Erroneous character after & : "  + peek );
          return null;
        }
      case (char) -1:
        return Token.EOF;
    }

    // Words, Numbers and Identifiers
    if (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
      String str = "" + peek;
      readCh(br);

      while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
        str += peek;
        readCh(br);
      }

      // If is a Word
      switch(str.toLowerCase()) {
        case "cond":
          return Word.COND;
        case "then":
          return Word.THEN;
        case "when":
          return Word.WHEN;
        case "else":
          return Word.ELSE;
        case "while":
          return Word.WHILE;
        case "do":
          return Word.DO;
        case "seq":
          return Word.SEQ;
        case "print":
          return Word.PRINT;
        case "read":
          return Word.READ;
      }

      // DFA for Identifiers and Numbers
      int state = 0;
      for(int i = 0; state > -1 && i < str.length(); i++) {
        final char ch = str.charAt(i);

        switch(state) {
          case 0:
            if (Character.isLetter(ch)) state = 1;
            else if (Character.isDigit(ch)) state = 2;
            else if (ch != '_') state = -1;
            break;
          case 1: // Identifiers Final
            if (!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) state = -1;
            break;
          case 2: // Numbers Final
            if (!Character.isDigit(ch)) state = -1;
            break;
        }
      }
      
      // If Identifier
      if (state == 1) {
        return new Word(Tag.ID, str);
      }

      // If Number
      if (state == 2) {
        return new Number(Tag.NUM, str);
      }

      // Unknown
      System.err.println("Erroneous identifier: " + str);
      return null;
    }

    System.err.println("Erroneous character: " + peek);
    return null;
  }

  private void skipSpaces(BufferedReader br) {
    while (Character.isWhitespace(peek)) {
      readCh(br);
    }
  }

  private void readCh(BufferedReader br) {
    try {
      peek = (char) br.read();
    } catch (IOException exc) {
      peek = (char) -1; // ERROR
    }
  }

  // --------------------------------------------
  // Main static method

  public static void main(String[] args) {
    Lexer lex = new Lexer();

    try {
      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      Token tok;

      do {
        tok = lex.lexicalScan(br);
        System.out.println("Scan: " + tok);
      } while (tok.tag != Tag.EOF);

      br.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}