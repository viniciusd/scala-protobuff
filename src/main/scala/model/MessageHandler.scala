package model

import akka.actor.{Actor, ActorLogging, ActorRef}

import message.Person.Person

class MessageHandler(writer: ActorRef) extends Actor with ActorLogging {
  override def receive: Receive = {
    case person:Person =>
      log.info("Received a person message: {}", person.toByteArray.map("%02X" format _).mkString)
      writer ! person.toByteArray
  }
}
