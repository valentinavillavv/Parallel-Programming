package exercises03;
public interface BoundedBufferInterface<T> {
        public T take() throws Exception;
        public void insert(T elem) throws Exception;
        
}
