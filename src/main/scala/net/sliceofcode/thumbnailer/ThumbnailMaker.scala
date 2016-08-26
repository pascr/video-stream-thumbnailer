package net.sliceofcode.thumbnailer

import java.io.File
import java.util.UUID
import javax.imageio.ImageIO

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Sink, Source }
import net.sliceofcode.thumbnailer.video.{ VideoImage, VideoReader, VideoResult }
import org.slf4j.LoggerFactory

import scala.concurrent.Future

object ThumbnailMaker extends App {
  implicit lazy val actorSystem = ActorSystem()
  implicit lazy val materializer = ActorMaterializer()
  implicit val ec = actorSystem.dispatcher
  val input = getClass.getResource(TEST_VIDEO_FILE).toURI
  val source = Source(reader.getImageStream)
  private val TEST_VIDEO_FILE = "/BigBuckBunny_320x180.mp4"
  private val log = LoggerFactory.getLogger(getClass)
  private val reader = VideoReader.fromFile(input).get

  source.runWith(Sink.foreach(saveImage)).onComplete(_ => {
    reader.close()
    actorSystem.terminate()
  })

  def saveImage(image: VideoResult): Unit = {
    image match {
      case img: VideoImage =>
        log.info("saving an image")
        Future {
          val outputFile = new File(s"target/${UUID.randomUUID().toString}.jpg")
          ImageIO.write(img.bufferedImage, "jpg", outputFile)
        }
      case _ => //nothing here
    }

  }

}
