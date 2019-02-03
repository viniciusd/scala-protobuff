import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import spray.json.{DefaultJsonProtocol, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._
import scala.util.{Failure, Success}

import message.Person.Person
import model.MessageHandler
import model.WriterHandler

trait Protocol extends DefaultJsonProtocol {
  implicit val personFormat = jsonFormat2(Person.apply)
}

trait Service extends Protocol {

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  def writerHandler: ActorRef

  val routes =
    pathSingleSlash {
      post {
        entity(as[JsValue]) { json =>
          val messageHandler = system.actorOf(Props(new MessageHandler(writerHandler)))
          messageHandler ! json.convertTo[Person]
          complete(OK)
        }
      }
    }
}

object AkkaHttpService extends App with Service {
  override implicit val system = ActorSystem()
  override implicit val executor = system.dispatcher
  override implicit val materializer = ActorMaterializer()

  override val config = ConfigFactory.load()
  override val logger = Logging(system, getClass)
  val writerHandler = system.actorOf(Props[WriterHandler])

  system.scheduler.schedule(0 seconds, 10 seconds, writerHandler, WriterHandler.Rollover)

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
