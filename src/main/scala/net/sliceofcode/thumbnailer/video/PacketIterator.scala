package net.sliceofcode.thumbnailer.video

import io.humble.video.awt.MediaPictureConverter
import io.humble.video.{ Decoder, MediaPacket, MediaPicture }

class PacketIterator(decoder: Decoder, packet: MediaPacket, picture: MediaPicture, converter: MediaPictureConverter, streamId: Int) extends Iterator[VideoResult] {
  var offset = 0 // mutable state

  def hasNext = packet.getStreamIndex == streamId && offset < packet.getSize

  def next() = {
    offset += decoder.decode(picture, packet, offset)

    if (picture.isComplete && picture.getCodedPictureNumber % 100 == 0) {
      VideoImage(converter.toImage(null, picture))
    } else {
      PartialImage
    }

  }
}

