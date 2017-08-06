package popit.ai.exhaustiveAIs;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BlockingStack<T> implements BlockingQueue<T> {
    private BlockingDeque<T> backbone = new LinkedBlockingDeque<>();

    @Override
    public boolean add(T t) {
        return backbone.offerFirst(t);
    }

    @Override
    public boolean offer(T t) {
        return backbone.offerFirst(t);
    }

    @Override
    public T remove() {
        return backbone.pop();
    }

    @Override
    public T poll() {
        return backbone.pollFirst();
    }

    @Override
    public T element() {
        return backbone.element();
    }

    @Override
    public T peek() {
        return backbone.peek();
    }

    @Override
    public void put(T t) throws InterruptedException {
        backbone.offerFirst(t);
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        return backbone.offerFirst(t, timeout, unit);
    }

    @Override
    public T take() throws InterruptedException {
        return backbone.takeFirst();
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return backbone.pollFirst(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return backbone.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return backbone.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return backbone.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return backbone.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return backbone.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return backbone.retainAll(c);
    }

    @Override
    public void clear() {
        backbone.clear();
    }

    @Override
    public int size() {
        return backbone.size();
    }

    @Override
    public boolean isEmpty() {
        return backbone.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return backbone.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return backbone.iterator();
    }

    @Override
    public Object[] toArray() {
        return backbone.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        //noinspection SuspiciousToArrayCall
        return backbone.toArray(a);
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        return backbone.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        return backbone.drainTo(c, maxElements);
    }
}
