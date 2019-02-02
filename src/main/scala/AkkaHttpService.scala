import akka.actor.{ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes.{InternalServerError}
import akka.http.scaladsl.server.Directives._
import akka.pattern.ask
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import model.MessageHandler
import model.MessageHandler._
import spray.json.{DefaultJsonProtocol, JsValue}

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}

trait Service extends DefaultJsonProtocol {

  import scala.concurrent.duration._

  implicit val system: ActorSystem

  implicit def executor: ExecutionContextExecutor

  implicit val materializer: Materializer

  def config: Config

  val logger: LoggingAdapter

  def messageHandler: ActorRef

  implicit def requestTimeout: Timeout = Timeout(5 seconds)

  val routes =
    pathSingleSlash {
      post {
        entity(as[JsValue]) { json =>
          onComplete(messageHandler ? Persist(json)) {
            case Success(actorResponse) => actorResponse match {
              case MessagePersisted(resp) => complete(resp)
            }
            case Failure(_) => complete(InternalServerError)
          }
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
  val messageHandler = system.actorOf(Props[MessageHandler])

  Http().bindAndHandle(routes, config.getString("http.interface"), config.getInt("http.port"))
}
