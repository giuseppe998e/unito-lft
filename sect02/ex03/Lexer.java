import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;
import java.lang.Character;

public class Lexer {
  private char peek;
  private int line;

  public Lexer() {
    line = 1;
    peek = Tag.SPC; // "SPACE"
  }

  public Token scan(BufferedReader br) {
    skipSpaces(br);
    skipComments(br);

    switch (peek) {
      case Tag.NOT: // !
        peek = Tag.SPC; // "SPACE"
        return Token.NOT;
      case Tag.LPT: // (
        peek = Tag.SPC; // "SPACE"
        return Token.LPT;
      case Tag.RPT: // )
        peek = Tag.SPC; // "SPACE"
        return Token.RPT;
      case Tag.LPG: // {
        peek = Tag.SPC; // "SPACE"
        return Token.LPG;
      case Tag.RPG: // }
        peek = Tag.SPC; // "SPACE"
        return Token.RPG;
      case Tag.PLS: // +
        peek = Tag.SPC; // "SPACE"
        return Token.PLS;
      case Tag.MIN: // -
        peek = Tag.SPC; // "SPACE"
        return Token.MIN;
      case Tag.MUL: // *
        peek = Tag.SPC; // "SPACE"
        return Token.MUL;
      case Tag.DIV: // /
        peek = Tag.SPC; // "SPACE"
        return Token.DIV;
      case Tag.SCL: // ;
        peek = Tag.SPC; // "SPACE"
        return Token.SCL;
      case Tag.ASN: // = 
        readCh(br);
        if (peek == Tag.ASN) { // =
          peek = Tag.SPC; // "SPACE"
          return Word.EQ;
        } else {
          return Token.ASN;
        }
      case '|':
        readCh(br);
        if (peek == '|') {
          peek = Tag.SPC; // "SPACE"
          return Word.OR;
        } else {
          System.err.println("Erroneous character after | : "  + peek);
          return null;
        }
      case '<':
        readCh(br);
        if (peek == '=') {
          peek = Tag.SPC; // "SPACE"
          return Word.LE;
        } else if (peek == '>') {
          peek = Tag.SPC; // "SPACE"
          return Word.NE;
        } else {
          return Word.LT;
        }
      case '>':
        readCh(br);
        if (peek == '=') {
          peek = Tag.SPC; // "SPACE"
          return Word.GE;
        } else {
          return Word.GT;
        }
      case '&':
        readCh(br);
        if (peek == '&') {
          return Word.AND;
        } else {
          System.err.println("Erroneous character after & : "  + peek);
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

  private void skipComments(BufferedReader br) {
    while (peek == Tag.DIV) {
      char tmpPeek = readChTmp(br);

      if (tmpPeek == Tag.DIV) { // SingleLine
        while (!(peek == '\n' || peek == (char) Tag.EOF)) {
          readCh(br);
        }
      } else if (tmpPeek == Tag.MUL) { // MultiLine
        int state = 0;
        while (state < 2 && peek != (char) Tag.EOF) {
          readCh(br);
          switch(state) {
            case 0:
              if (peek == Tag.MUL) state = 1;
              break;
            case 1:
              if (peek == Tag.DIV) state = 2;
              else state = 0;
          }
        }

        if (state < 2) {
          throw new RuntimeException("Comment area not closed");
        }

        peek = Tag.SPC; // "SPACE"
      } else {
        skipSpaces(br);
        break;
      }

      skipSpaces(br);
    }
  }

  public int getLine() {
    return line;
  }

  private void skipSpaces(BufferedReader br) {
    while (Character.isWhitespace(peek)) {
      if (peek == '\n') line++;
      readCh(br);
    }
  }

  private void readCh(BufferedReader br) {
    try {
      peek = (char) br.read();
    } catch (IOException exc) {
      peek = (char) Tag.EOF; // ERROR
    }
  }

  private char readChTmp(BufferedReader br) {
    char tmpPeek = (char) Tag.EOF;
    try {
      br.mark(1);
      tmpPeek = (char) br.read();
      br.reset();
    } catch (IOException ignored) {
      // EOF reached
    }
    return tmpPeek;
  }

  // --------------------------------------------
  // Main static method

  public static void main(String[] args) {
    Lexer lex = new Lexer();

    try {
      BufferedReader br = new BufferedReader(new FileReader(args[0]));
      Token tok;

      do {
        tok = lex.scan(br);
        System.out.println("Scan: " + tok);
      } while (tok.getTag() != Tag.EOF);

      br.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
