import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane
import scalafx.scene.media.AudioClip

class Fish(fishImageView: ImageView, gamePane: AnchorPane)
  extends GameObject(fishImageView, gamePane) {

  private val jumpHeight = 70.0
  private var yDelta = 12.0

  private val swimSoundResource = getClass.getResource("/swim.mp3")
  private val swimSound: AudioClip =
    if (swimSoundResource != null) {
      new AudioClip(swimSoundResource.toString)
    }
    else{
      println("Swim sound file not found.")
      null
    }

  def swim(): Unit = {
    val totalY = imageView.layoutY.value + imageView.y.value
    if (totalY <= jumpHeight) {
      moveY(-totalY)
    } else {
      moveY(-jumpHeight)
    }
    time.value = 0

    if (swimSound != null){
      swimSound.volume = 0.5
      swimSound.play()
    }

  }

  override def calculateYDelta(): Double = yDelta * time.value

  def isDead: Boolean = {
    val fishY = imageView.layoutY.value + imageView.y.value
    fishY >= gamePane.height.value
  }

  def checkCollision(obstacles: List[(ImageView, ImageView, Boolean)]): Boolean = {
    val fishBounds = getBounds()

    val fishMinX = fishBounds.getMinX + 10
    val fishMinY = fishBounds.getMinY
    val fishMaxX = fishBounds.getMaxX - 10
    val fishMaxY = fishBounds.getMaxY - 10

    obstacles.exists { case (topObstacle, bottomObstacle, _) =>
      val topObstacleBounds = topObstacle.boundsInParent.value
      val bottomObstacleBounds = bottomObstacle.boundsInParent.value

      val topObstacleMinX = topObstacleBounds.getMinX + 10
      val topObstacleMinY = topObstacleBounds.getMinY + 10
      val topObstacleMaxX = topObstacleBounds.getMaxX - 10
      val topObstacleMaxY = topObstacleBounds.getMaxY - 10

      val bottomObstacleMinX = bottomObstacleBounds.getMinX + 10
      val bottomObstacleMinY = bottomObstacleBounds.getMinY + 10
      val bottomObstacleMaxX = bottomObstacleBounds.getMaxX - 10
      val bottomObstacleMaxY = bottomObstacleBounds.getMaxY - 10

      val intersectsTop = fishMinX < topObstacleMaxX &&
        fishMaxX > topObstacleMinX &&
        fishMinY < topObstacleMaxY &&
        fishMaxY > topObstacleMinY

      val intersectsBottom = fishMinX < bottomObstacleMaxX &&
        fishMaxX > bottomObstacleMinX &&
        fishMinY < bottomObstacleMaxY &&
        fishMaxY > bottomObstacleMinY

      intersectsTop || intersectsBottom
    }
  }
}