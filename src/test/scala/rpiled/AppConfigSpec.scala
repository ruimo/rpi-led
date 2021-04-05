package rpiled

import java.nio.file.Paths

import org.specs2.Specification
import com.typesafe.config.ConfigFactory
import configs.syntax._

class AppConfigSpecSpec extends Specification { def is = s2"""
 This is a specification to check the 'AppConfig'
 The 'AppConfig' should
   read config file                                         $canRead
   can treat optional                                       $canReadOptional
"""

  def canRead = {
    val config = ConfigFactory.parseString("""
      parm {
        fifo-path = "/var/fifo"
        listen-port = 8080
        online-blink-period-millis = 1000
        auto-shutdown-period-millis = 10000
        busy-loop-count = 2000
      }
    """)

    val appConf: AppConfig = config.get[AppConfig]("parm").value
    appConf.fifoPath === Paths.get("/var/fifo")
    appConf.listenPort === 8080
    appConf.onlineBlinkPeriodMillis === 1000
    appConf.autoShutdownPeriodMillis === Some(10000L)
    appConf.busyLoopCount === 2000L
  }

  def canReadOptional = {
    val config = ConfigFactory.parseString("""
      parm {
        fifo-path = "/var/fifo"
        listen-port = 8080
        online-blink-period-millis = 1000
        busy-loop-count = 2000
      }
    """)

    val appConf: AppConfig = config.get[AppConfig]("parm").value
    appConf.fifoPath === Paths.get("/var/fifo")
    appConf.listenPort === 8080
    appConf.onlineBlinkPeriodMillis === 1000
    appConf.autoShutdownPeriodMillis === None
    appConf.busyLoopCount === 2000L
  }
}
