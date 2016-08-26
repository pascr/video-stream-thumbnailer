package net.sliceofcode.thumbnailer.streams

import java.net.URI

import akka.actor.{ ActorLogging, Props }
import akka.stream.actor.ActorPublisher
import akka.stream.actor.ActorPublisherMessage.{ Cancel, Request }
import akka.stream.actor.ActorSubscriberMessage.OnComplete
import net.sliceofcode.thumbnailer.video.{ VideoReader, VideoResult }

class VideoPublisher(sourceFile: URI) extends ActorPublisher[VideoResult] with ActorLogging {

  val maybeReader = VideoReader.fromFile(sourceFile)
  var totalFrameRequested = 0

  override def receive: Receive = {
    case Request(elementCount) =>
      log.info(s"more images requested $elementCount")

      maybeReader.map(reader => {
        for (i <- totalFrameRequested to totalFrameRequested + elementCount.toInt)
          reader.getImageStream.drop(i) match {
            case Stream.Empty => OnComplete
            case _ => onNext _
          }
        totalFrameRequested += elementCount.toInt
      }).getOrElse(OnComplete)

    case Cancel => context.stop(self)
    case _ => //nothing here continue
  }

  override def postStop(): Unit = maybeReader.foreach(_.close())


}

object VideoPublisher {
  def props(sourceFile: URI) = Props(classOf[VideoPublisher], sourceFile)
}