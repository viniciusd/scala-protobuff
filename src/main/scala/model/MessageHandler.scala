package model

import scala.collection.JavaConverters._

import akka.actor.{Actor, ActorLogging, ActorSystem}
import com.typesafe.config.{Config, ConfigFactory}
import spray.json._

object MessageHandler {

  def config: Config = ConfigFactory.load()
  def system: ActorSystem = ActorSystem("PersistenceSystem")

  case class Persist(json: JsValue)
  case class MessagePersisted(json: JsValue)
}

class MessageHandler extends Actor with ActorLogging {
  import MessageHandler._
  implicit val ec = context.dispatcher

  override def receive: Receive = {

    case Persist(json) =>
      sender() ! MessagePersisted(json)

  }
}
