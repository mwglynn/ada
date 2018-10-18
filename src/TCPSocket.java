public abstract interface TCPSocket {
    abstract void WriteToSocket(String msg);
    abstract void Close();
}
