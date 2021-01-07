import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import component.Tag;
import component.Token;
import component.Identifier;
import component.Number;

public class Lexer {
  private int line;
  private char peek;

  public Lexer() {
    line = 1;
    peek = ' ';
  }

  public Token scan(BufferedReader br) {
    // Spaces
    while (Character.isWhitespace(peek)) {
      if (peek == '\n') line += 1;
      readChar(br);
    }

    // Single Char Tokens
    switch (peek) {
      case Tag.NOT:
        peek = ' ';
        return Token.NOT;
      case Tag.LPT:
        peek = ' ';
        return Token.LPT;
      case Tag.RPT:
        peek = ' ';
        return Token.RPT;
      case Tag.LPG:
        peek = ' ';
        return Token.LPG;
      case Tag.RPG:
        peek = ' ';
        return Token.RPG;
      case Tag.PLS:
        peek = ' ';
        return Token.PLS;
      case Tag.MIN:
        peek = ' ';
        return Token.MIN;
      case Tag.MUL:
        peek = ' ';
        return Token.MUL;
      case Tag.DIV:
        peek = ' ';
        return Token.DIV;
      case Tag.SCL:
        peek = ' ';
        return Token.SCL;
      case (char)Tag.EOF:
        return Token.EOF;
    // Double Char Tokens
      case '|':
        readChar(br);
        if (peek == '|') {
          peek = ' ';
          return Token.OR;
        } else {
          throw new RuntimeException("Erroneous character after | : "  + peek );
        }
      case '=':
        readChar(br);
        if (peek == '=') {
          peek = ' ';
          return Token.EQ;
        } else {
          return Token.ASN;
        }
      case '<':
        readChar(br);
        if (peek == '=') {
          peek = ' ';
          return Token.LE;
        } else if (peek == '>') {
          peek = ' ';
          return Token.NE;
        } else {
          return Token.LT;
        }
      case '>':
        readChar(br);
        if (peek == '=') {
          peek = ' ';
          return Token.GE;
        } else {
          return Token.GT;
        }
      case '&':
        readChar(br);
        if (peek == '&') {
          return Token.AND;
        } else {
          throw new RuntimeException("Erroneous character after & : "  + peek );
        }
    }

    // Word Tokens OR Identifiers
    if (Character.isLetter(peek)) {
      String lexeme = "";
      do {
        lexeme += peek;
        readChar(br);
      } while (Character.isLetter(peek));

      switch(lexeme.toLowerCase()) {
        case "cond":
          return Token.COND;
        case "then":
          return Token.THEN;
        case "when":
          return Token.WHEN;
        case "else":
          return Token.ELSE;
        case "while":
          return Token.WHILE;
        case "do":
          return Token.DO;
        case "seq":
          return Token.SEQ;
        case "print":
          return Token.PRINT;
        case "read":
          return Token.READ;
      }
      
      return new Identifier(lexeme);
    }

    // (Non-Negative) Numbers
    if (Character.isDigit(peek)) {
      String number = "";
      do {
        number += peek;
        readChar(br);
      } while (Character.isDigit(peek));

      return new Number(Integer.parseInt(number));
    }

    // Illegal character
    throw new RuntimeException("Erroneous character: " + peek);
  }

  public int getLine() {
    return line;
  }


  private void readChar(BufferedReader br) {
    try {
      peek = (char) br.read();
    } catch (IOException ignored) { 
      peek = (char) Tag.EOF;
    }
  }

	// -------------------------------------
	// Application Main
	public static void main(String[] args) {
		Lexer lexer = new Lexer();

		try (FileReader fReader = new FileReader(args[0]);
          BufferedReader bReader = new BufferedReader(fReader)) {
      
      Token token = null;
      do {
        token = lexer.scan(bReader);
        System.out.println("token = " + token);
      } while (token.getTag() != Tag.EOF);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
	}
}