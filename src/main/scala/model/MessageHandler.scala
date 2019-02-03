package model

import scala.collection.JavaConverters._

import akka.actor.{Actor, ActorLogging, ActorSystem}
import com.typesafe.config.{Config, ConfigFactory}

import message.Person.Person

object MessageHandler {

  def config: Config = ConfigFactory.load()
  def system: ActorSystem = ActorSystem("PersistenceSystem")

  case class Persist(person: Person)
  case class MessagePersisted(person: Person)
}

class MessageHandler extends Actor with ActorLogging {
  import MessageHandler._
  implicit val ec = context.dispatcher

  override def receive: Receive = {

    case Persist(person) =>
      sender() ! MessagePersisted(person.withId(person.id + 1))
  }
}
