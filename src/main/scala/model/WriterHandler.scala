package model

import java.io.FileOutputStream

import akka.actor.{Actor, ActorLogging}

object WriterHandler {
   case object Rollover
}

class WriterHandler extends Actor with ActorLogging {
  import WriterHandler._

  var outputFile = new FileOutputStream("blob.dat")

  override def receive: PartialFunction[Any, Unit] = {
    case byteArray:Array[Byte] =>
      outputFile.write(byteArray)

    case Rollover =>
      outputFile = new FileOutputStream("blob.dat")
  }
}
