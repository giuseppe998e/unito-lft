import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
  private final Map<String, Integer> offsetMap;
  private int address;

  public SymbolTable() {
    this.offsetMap = new HashMap<>();
    address = 0;
  }

  public int insert(String s) {
    return insert(s, this.address++);
  }

  public int insert(String s, int _address) {
    if (offsetMap.containsValue(_address)) {
      throw new IllegalArgumentException("Reference to a memory location already occupied by another variable");
    }

    offsetMap.put(s, _address);
    return _address;
  }

  public int lookupAddress(String s) {
    return offsetMap.getOrDefault(s, -1);
  }
}