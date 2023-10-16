package bg.sofia.uni.fmi.mjt.splitwise.repository;

import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

public interface Repository<K, V> {
    Collection<V> getAll();

    V get(K k);

    void insert(K key, V val);

    boolean contains(K key);

    void delete(K k);

    void load(Reader reader);
    void load(String path);


    void save(Writer writer);
    void save(String path);
}
