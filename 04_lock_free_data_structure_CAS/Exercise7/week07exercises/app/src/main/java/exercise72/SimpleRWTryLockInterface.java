package exercise72;

interface SimpleRWTryLockInterface {
    public boolean readerTryLock();
    public void readerUnlock();
    public boolean writerTryLock();
    public void writerUnlock();
}
