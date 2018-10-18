public interface TCPSocket {
    void WriteToSocket(String msg);
    String ReadFromSocket();
    void Close();
}
