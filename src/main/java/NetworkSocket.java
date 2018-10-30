public interface NetworkSocket {
  void WriteToSocket(String msg);

  String ReadFromSocket();

  void Close();
}
