package net.sliceofcode.thumbnailer.video

import java.awt.image.BufferedImage

import org.scalatest.{ BeforeAndAfter, FlatSpec, Matchers }

class VideoReaderSpec extends FlatSpec with Matchers with BeforeAndAfter {
  val TEST_VIDEO_FILE = "/SampleVideo_360x240_1mb.mp4"
  val TEST_AUDIO_FILE = "/point1sec.mp3"

  val input = getClass.getResource(TEST_VIDEO_FILE).toURI
  val audioOnlyInput = getClass.getResource(TEST_AUDIO_FILE).toURI

  it should "build a video reader if everything is ok" in {
    val reader = VideoReader.fromFile(input)
    reader should not be empty
  }

  it should "return an image frame on first read" in {
    val reader = VideoReader.fromFile(input)
    reader.map(_.getImageStream.take(1).head match {
      case img: VideoImage =>
        img.bufferedImage should not be null
        img.bufferedImage.getWidth shouldEqual 320
        img.bufferedImage.getHeight shouldEqual 240
      case _ => fail
    }).getOrElse(fail)

  }

  it should "return a different image on each read" in {
    val reader = VideoReader.fromFile(input).get
    val img1 = reader.getImageStream.take(1).head
    val img2 = reader.getImageStream.drop(1).head
    img1 match {
      case i1: VideoImage =>
        img2 match {
          case i2: VideoImage => bufferedImagesEqual(i1.bufferedImage, i2.bufferedImage) should be(true)
          case _ => fail
        }
      case _ => fail
    }

  }

  //TODO:can be nicer
  private def bufferedImagesEqual(img1: BufferedImage, img2: BufferedImage) = {
    (img1.getWidth == img2.getWidth && img1.getHeight == img2.getHeight) && {
      (0 until img1.getWidth()).flatMap { x =>
        (0 until img1.getHeight()).map { y =>
          img1.getRGB(x, y) == img2.getRGB(x, y)
        }
      }.contains(false)
    }
  }
}

