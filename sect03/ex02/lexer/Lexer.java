package lexer;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.RuntimeException;

import lexer.component.Tag;
import lexer.component.Token;
import lexer.component.Identifier;
import lexer.component.Number;

public class Lexer {
  private int line;
  private char peek;

  public Lexer() {
    line = 1;
    peek = ' ';
  }

  public Token scan(BufferedReader br) {
    // Spaces, Tag.DIV Token and Comments (Single line or Multi line)
    while (Character.isWhitespace(peek) || peek == Tag.DIV) {
      if (peek == '\n') line += 1;

      //  Tag.DIV Token and Comments (Single line or Multi line)
      if (peek == Tag.DIV) {
        readChar(br);
        
        if (peek == Tag.MUL) {
          int state = 0;
          while (state < 2 && peek != (char) Tag.EOF) {
            readChar(br);
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

          peek = ' ';
        } else if (peek == Tag.DIV) {
          while(peek != '\n') readChar(br);
        } else {
          return Token.DIV;
        }
      }

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
//    case Tag.DIV:
//      peek = ' ';
//      return Token.DIV;
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

    // Word Tokens, Numbers and Identifiers
    if (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_') {
      String lexeme = "";
      do {
        lexeme += peek;
        readChar(br);
      } while (Character.isLetter(peek) || Character.isDigit(peek) || peek == '_');

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

      // DFA for Identifiers and Numbers
      int state = 0;
      for(int i = 0; state > -1 && i < lexeme.length(); i++) {
        final char ch = lexeme.charAt(i);

        switch(state) {
          case 0:
            if (Character.isLetter(ch)) state = 1;
            else if (Character.isDigit(ch)) state = 2;
            else if (ch != '_') state = -1;
            break;
          case 1:
            if (!(Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')) state = -1;
            break;
          case 2:
            if (!Character.isDigit(ch)) state = -1;
            break;
        }
      }

      // If Identifier
      if (state == 1) {
        return new Identifier(lexeme);
      }

      // If Number
      if (state == 2) {
        return new Number(Integer.valueOf(lexeme));
      }
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
}
