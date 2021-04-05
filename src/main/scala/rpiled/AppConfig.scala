package rpiled

import java.nio.file.Path

case class AppConfig(
  fifoPath: Path,
  listenPort: Int,
  onlineBlinkPeriodMillis: Long,
  autoShutdownPeriodMillis: Option[Long],
  busyLoopCount: Long
)

