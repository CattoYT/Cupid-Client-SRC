package us.myles.viaversion.libs.javassist.tools.web;

import java.io.IOException;
import java.net.Socket;

class ServiceThread extends Thread {
  Webserver web;
  
  Socket sock;
  
  public ServiceThread(Webserver w, Socket s) {
    this.web = w;
    this.sock = s;
  }
  
  public void run() {
    try {
      this.web.process(this.sock);
    } catch (IOException iOException) {}
  }
}


/* Location:              C:\Users\Joona\Downloads\Cupid.jar\\us\myles\viaversion\libs\javassist\tools\web\ServiceThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */