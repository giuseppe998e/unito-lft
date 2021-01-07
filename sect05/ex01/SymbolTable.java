import java.util.Map;
import java.util.HashMap;

public class SymbolTable {
  private final Map<String, Integer> offsetMap;

	public SymbolTable() {
		this.offsetMap = new HashMap<>();
	}

  public void insert(String s, int address) {
    if (!offsetMap.containsValue(address)) {
      offsetMap.put(s, address);
		} else {
      throw new IllegalArgumentException("Riferimento ad una locazione di memoria già occupata da un’altra variabile");
		}
  }

  public int lookupAddress(String s) {
    return offsetMap.containsKey(s) ? offsetMap.get(s) : -1;
  }
}