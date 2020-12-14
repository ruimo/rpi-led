package rpiled

import java.io.FileWriter
import java.io.Writer
import java.net.InetAddress
import java.util.Locale
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
  val readinessSuccessMessage: String = ipAddress + ":rs\n"
  val readinessFailureMessage: String = ipAddress + ":rf\n"
  val livenessSuccessMessage: String = ipAddress + ":ls\n"
  val livenessFailureMessage: String = ipAddress + ":lf\n"
  val server = new Server(8080)
  val onlineBlinkPeriod: Long = Option(System.getenv("ONLINE_BLINK_PERIOD")).map(_.toLong).getOrElse(1000L)
  @volatile private[this] var isReady = true
  @volatile private[this] var isOk = true

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

    def onTop() = {
      response.setContentType("text/html; charset=utf-8")
      response.setStatus(HttpServletResponse.SC_OK)
      response.getWriter().println("<h1>Hello World</h1>")
      baseRequest.setHandled(true)
      writeMessage(requestMessage)
    }

    def onReady() = {
      def onGet() = {
        if (isReady) {
          response.setStatus(HttpServletResponse.SC_OK)
          response.getWriter().println("Ready")
          writeMessage(readinessSuccessMessage)
        } else {
          response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE)
          response.getWriter().println("Not Ready")
          writeMessage(readinessFailureMessage)
        }
        baseRequest.setHandled(true)
      }

      def onPost() = {
        Option(baseRequest.getParameter("isReady")) match {
          case None =>
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            response.getWriter().println("Specify isReady=true/false by request parameter. Ex: /ready?isReady=true")
          case Some(b) => b.toLowerCase(Locale.ENGLISH) match {
            case "true" =>
              isReady = true
              response.setStatus(HttpServletResponse.SC_OK)
              response.getWriter().println("isReady=" + isReady)
            case "false" =>
              isReady = false
              response.setStatus(HttpServletResponse.SC_OK)
              response.getWriter().println("isReady=" + isReady)
            case s: String =>
              response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
              response.getWriter().println("'" + s + "' is invalid. Specify isReady=true/false by request parameter. Ex: /ready?isReady=true")
          }
        }
        baseRequest.setHandled(true)
      }

      val method = baseRequest.getMethod().toUpperCase(Locale.ENGLISH)
      println("Method: '" + method + "'")

      if (method == "GET") {
        onGet()
      } else if (method == "POST") {
        onPost()
      }
    }

    def onOk() = {
      def onGet() = {
        if (isReady) {
          response.setStatus(HttpServletResponse.SC_OK)
          response.getWriter().println("Ok")
          writeMessage(livenessSuccessMessage)
        } else {
          response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE)
          response.getWriter().println("NG")
          writeMessage(livenessFailureMessage)
        }
        baseRequest.setHandled(true)
      }

      def onPost() = {
        Option(baseRequest.getParameter("isOk")) match {
          case None =>
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
            response.getWriter().println("Specify isOk=true/false by request parameter. Ex: /ok?isOk=true")
          case Some(b) => b.toLowerCase(Locale.ENGLISH) match {
            case "true" =>
              isOk = true
              response.setStatus(HttpServletResponse.SC_OK)
              response.getWriter().println("isOk=" + isOk)
            case "false" =>
              isOk = false
              response.setStatus(HttpServletResponse.SC_OK)
              response.getWriter().println("isOk=" + isOk)
            case s: String =>
              response.setStatus(HttpServletResponse.SC_BAD_REQUEST)
              response.getWriter().println("'" + s + "' is invalid. Specify isOk=true/false by request parameter. Ex: /ok?isOk=true")
          }
        }
        baseRequest.setHandled(true)
      }

      val method = baseRequest.getMethod().toUpperCase(Locale.ENGLISH)
      println("Method: '" + method + "'")

      if (method == "GET") {
        onGet()
      } else if (method == "POST") {
        onPost()
      }
    }

    if (target.equals("/")) {
      onTop()
    } else if (target.startsWith("/ready")) {
      onReady()
    } else if (target.startsWith("/ok")) {
      onOk()
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
