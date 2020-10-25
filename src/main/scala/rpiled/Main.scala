package rpiled

import java.io.FileWriter
import java.io.Writer
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.eclipse.jetty.server.Request
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.AbstractHandler

object Main {
  val writer: Writer = new FileWriter("/var/fifo")
  val ipAddress = InetAddress.getLocalHost().getHostAddress()
  val onlineMessage: String = ipAddress + ":o\n"
  val requestMessage: String = ipAddress + ":r\n"
  val server = new Server(8080)
  val onlineBlinkPeriod: Long = Option(System.getenv("ONLINE_BLINK_PERIOD")).map(_.toLong).getOrElse(1000L)

  def main(args: Array[String]): Unit = {
    registerOnlineMarker()
    server.setHandler(new AbstractHandler() {
      override def handle(
        target: String, baseRequest: Request,
        request: HttpServletRequest, response: HttpServletResponse
      ): Unit = onRequest(target, baseRequest, response)
    })

    server.start()
    server.join()
  }

  private[this] def onRequest(target: String, baseRequest: Request, response: HttpServletResponse): Unit = {
    println("Context path: '" + target + "'")

    if (target.equals("/")) {
      response.setContentType("text/html; charset=utf-8")
      response.setStatus(HttpServletResponse.SC_OK)
      response.getWriter().println("<h1>Hello World</h1>")
      baseRequest.setHandled(true)
      writeMessage(requestMessage)
    }
  }

  private[this] def registerOnlineMarker(): Unit = {
    Executors.newScheduledThreadPool(1).scheduleAtFixedRate(
        () => writeMessage(onlineMessage), 0, onlineBlinkPeriod, TimeUnit.MILLISECONDS
    )
  }

  private[this] def writeMessage(message: String): Unit = {
    try {
      writer.write(message)
      writer.flush()
    } catch {
      case t: Throwable =>
        t.printStackTrace()
    }
  }
}
