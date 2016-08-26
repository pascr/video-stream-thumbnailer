package net.sliceofcode.thumbnailer.video

import java.awt.image.BufferedImage

sealed trait VideoResult
case class VideoImage(bufferedImage: BufferedImage) extends VideoResult
case object PartialImage extends VideoResult
case object NoImageLeft extends VideoResult