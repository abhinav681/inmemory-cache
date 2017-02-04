/**
 * Created by abhinav.jaiswal on 03/02/17.
 */
public interface CacheService<K, V> {

    void put(K key, V value, long ttlInMs);

    V get(K key);
}
