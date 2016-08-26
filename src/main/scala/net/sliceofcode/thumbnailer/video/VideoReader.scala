package net.sliceofcode.thumbnailer.video

import java.net.URI

import io.humble.video._
import io.humble.video.awt.MediaPictureConverterFactory

import scala.util.Try

class VideoReader {

  import VideoReader._

  private lazy val (decoder, picture, streamIndex) = {
    lookupVideoStream(demuxer).map { stream =>
      val mediaPicture = MediaPicture.make(stream.decoder.getWidth, stream.decoder.getHeight, stream.decoder.getPixelFormat)
      (stream.decoder, mediaPicture, stream.streamIndex)
    }.getOrElse(throw VideoStreamMissingException)
  }
  private lazy val converter = MediaPictureConverterFactory.createConverter(MediaPictureConverterFactory.HUMBLE_BGR_24, picture)
  private lazy val readVideoStream: Stream[VideoResult] = {
    Stream.continually(demuxer.read(packet))
      .takeWhile(_ >= 0).flatMap(_ => {
      new PacketIterator(decoder, packet, picture, converter, streamIndex).toStream.filter(_ match {
        case valid: VideoImage => true
        case _ => false
      })
    })
  }
  private val demuxer = Demuxer.make
  private val packet = MediaPacket.make

  def getImageStream: Stream[VideoResult] = readVideoStream

  def close(): Unit = demuxer.close()

  private def open(sourceFile: URI): VideoReader = {
    //TODO: ZOMG I see nulls everywhere!
    demuxer.open(sourceFile.getPath, null, false, true, null, null)
    decoder.open(null, null)
    this
  }

  private def lookupVideoStream(demuxer: Demuxer): Option[VideoStream] = {
    val numStreams = demuxer.getNumStreams
    (0 until numStreams).map(streamID => {
      val stream = demuxer.getStream(streamID)
      VideoStream(stream.getDecoder, streamID)
    }).find(_.decoder.getCodecType == MediaDescriptor.Type.MEDIA_VIDEO)
  }

}

object VideoReader {
  def fromFile(sourceFile: URI): Option[VideoReader] = Try(new VideoReader().open(sourceFile)).toOption
  private case class VideoStream(decoder: Decoder, streamIndex: Int)

  //Exceptions
  object VideoStreamMissingException extends RuntimeException

}

