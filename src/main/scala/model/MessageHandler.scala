package model

import akka.actor.{Actor, ActorLogging, ActorRef}

import message.Person.Person

object MessageHandler {
  case class Persist(person: Person)
  case class MessagePersisted(person: Person)
}

class MessageHandler(writer: ActorRef) extends Actor with ActorLogging {
  import MessageHandler._
  implicit val ec = context.dispatcher

  override def receive: Receive = {

    case Persist(person) =>
      log.info("Received a person message: {}", person.toByteArray.map("%02X" format _).mkString)
      writer ! person.toByteArray
      sender() ! MessagePersisted(Person.parseFrom(person.withId(person.id + 1).toByteArray))
  }
}
