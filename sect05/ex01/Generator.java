public class Generator {
  // -------------------------------------
  // Application Main
  public static void main(String[] args) {
    Lexer lexer = new Lexer();

    try (FileReader fReader = new FileReader(args[0]);
          BufferedReader bReader = new BufferedReader(fReader)) {
     
      Parser parser = new Parser(lexer, bReader);
      parser.prog();

      System.out.println("Input: OK");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}