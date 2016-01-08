package util;
/**
 * 
 * This is an ancillary class that provides a (not-so-efficient) reverse iterator on a list
 * 
 * @author zuccong
 *
 */
import java.util.Iterator;
import java.util.List;

public class ReverseIterator<T> implements Iterator<T>, Iterable<T> {

    private final List<T> list;
    private int position;

    public ReverseIterator(List<T> list) {
        this.list = list;
        this.position = list.size() - 1;
    }

    public Iterator<T> iterator() {
        return this;
    }

    public boolean hasNext() {
        return position >= 0;
    }

    public T next() {
        return list.get(position--);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

}
