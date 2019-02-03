package model

import java.io.FileOutputStream

import akka.actor.{Actor, ActorLogging}

import com.typesafe.config.{Config, ConfigFactory}


object WriterHandler {
  def config: Config = ConfigFactory.load()

  case object Rollover
}

class WriterHandler extends Actor with ActorLogging {
  import WriterHandler._

  var outputFile = new FileOutputStream(config.getString("persistence.path"))

  override def receive: PartialFunction[Any, Unit] = {
    case byteArray:Array[Byte] =>
      outputFile.write(byteArray)

    case Rollover =>
      outputFile = new FileOutputStream(config.getString("persistence.path"))
  }
}
