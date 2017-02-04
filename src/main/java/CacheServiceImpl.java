import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by abhinav.jaiswal on 03/02/17.
 */

public class CacheServiceImpl<K, V> implements CacheService<K, V> {

    private static final Logger logger = Logger.getLogger(CacheServiceImpl.class.getName());

    private static final int NUMBER_OF_KEYS_TO_EVICT_IN_ONE_TRY = 100;
    private static final int SLEEP_TIME_IN_MS_AFTER_EVICT_TRY = 1000;


    private final List<Cachable<K, V>> values = new ArrayList<Cachable<K, V>>();
    private final Map<K, Integer> keyToValueIndex = new ConcurrentHashMap<K, Integer>();

    public CacheServiceImpl() {
        Thread thread = new Thread(){
            public void run() {
                while (true) {
                    tryEviction(NUMBER_OF_KEYS_TO_EVICT_IN_ONE_TRY);
                    try {
                        Thread.sleep(SLEEP_TIME_IN_MS_AFTER_EVICT_TRY);
                    } catch (InterruptedException e) {
                        logger.warning("cleanup thread got interrupted while sleeping " + e);
                    }
                }
            }
        };
        thread.start();
    }

    // Time Complexity O(1)
    public void put(K key, V value, long ttlInMs) {
        if (keyToValueIndex.get(key) != null) {
            values.set(keyToValueIndex.get(key), new Cachable<K, V>(ttlInMs, System.currentTimeMillis(), key, value));
        } else {
            synchronized (this) {
                values.add(new Cachable<K, V>(ttlInMs, System.currentTimeMillis(), key, value));
                keyToValueIndex.put(key, values.size() - 1);
            }
        }
    }

    // Time Complexity O(1)
    public V get(K key) {
        Integer index = keyToValueIndex.get(key);
        if (index != null && index < values.size()) {
            if (values.get(index).getStartTime() + values.get(index).getTtlInMs() >= System.currentTimeMillis()) {
                return values.get(index).getValue();
            } else {
                remove(key);
            }
        }
        return null;
    }

    // Time Complexity O(1)
    private synchronized void remove(K key) {
        Integer index = keyToValueIndex.remove(key);
        if (index != null && index < values.size()) {
            int size = values.size();
            Cachable<K, V> last = values.get(size - 1);
            Collections.swap(values, index, size - 1);
            values.remove(size - 1);
            keyToValueIndex.put(last.getKey(), index);
        }
    }

    // Time Complexity O(NUMBER_OF_KEYS_TO_EVICT_IN_ONE_TRY)
    private int tryEviction(int entriesToEvict) {
        int evictedEntries = 0;
        Random rand = new Random();

        for (int i = 0; i < entriesToEvict; i++) {
            if (values.size() > 0) {
                int index = rand.nextInt(values.size());

                Cachable<K, V> value = values.get(index);
                if (value.getStartTime() + value.getTtlInMs() < System.currentTimeMillis()) {
                    remove(value.getKey());
                    evictedEntries++;
                }
            }
        }
        return evictedEntries;
    }
}
