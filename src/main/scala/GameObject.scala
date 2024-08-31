import scalafx.beans.property.DoubleProperty
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane

abstract class GameObject(protected val imageView: ImageView, protected val gamePane: AnchorPane) {
  protected var time = DoubleProperty(0.0)
  private var lastUpdateTime: Option[Long] = None

  def moveY(positionChange: Double): Unit = {
    imageView.y.value += positionChange
  }

  def calculateYDelta(): Double

  def update(): Unit = {
    val currentTime = System.nanoTime()
    val elapsedTime = lastUpdateTime match {
      case Some(lastTime) => (currentTime - lastTime) / 1e9
      case None =>
        lastUpdateTime = Some(currentTime)
        0.0
    }

    lastUpdateTime = Some(currentTime)

    time.value += elapsedTime
    moveY(calculateYDelta())
  }

  def reset(): Unit = {
    imageView.y.value = 0
    time.value = 0
    lastUpdateTime = None
  }

  def getBounds() = imageView.boundsInParent.value
}