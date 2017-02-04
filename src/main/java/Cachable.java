import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by abhinav.jaiswal on 03/02/17.
 */

@Getter
@AllArgsConstructor
public class Cachable<K, V> {

    private long ttlInMs;

    private long startTime;

    private K key;

    private V value;
}
