import scalafx.application.Platform
import scalafx.scene.image.ImageView
import scalafx.scene.layout.AnchorPane
import scala.util.Random

class Obstacles(gamePane: AnchorPane, obstacleTopTemplate: ImageView, obstacleBottomTemplate: ImageView)
  extends GameObject(null, gamePane) {

  private val obstacleSpeed = 200.0
  private val obstacleGap = 180.0
  private val obstacleWidth = 60.0
  private var lastObstacleSpawnTime = 0L
  private var obstacleSpawnInterval = 1200000000L
  private val minObstacleSpawnInterval = 800000000L
  private val intervalDecrement = 20000000L
  private var obstacles = List.empty[(ImageView, ImageView, Boolean)]
  private var lastUpdateTime = System.nanoTime()

  override def calculateYDelta(): Double = 0.0

  def moveObstacles(): Unit = {
    val now = System.nanoTime()
    val deltaTime = (now - lastUpdateTime) / 1e9
    lastUpdateTime = now

    obstacles.foreach { case (topObstacle, bottomObstacle, passed) =>
      topObstacle.layoutX.value -= obstacleSpeed * deltaTime
      bottomObstacle.layoutX.value -= obstacleSpeed * deltaTime
    }

    obstacles = obstacles.filter { case (topObstacle, bottomObstacle, passed) =>
      val isOnScreen = topObstacle.layoutX.value + obstacleWidth > 0
      if (!isOnScreen) {
        Platform.runLater {
          gamePane.children.removeAll(topObstacle, bottomObstacle)
        }
      }
      isOnScreen
    }
  }

  def spawnObstacle(now: Long): Unit = {
    if (now - lastObstacleSpawnTime >= obstacleSpawnInterval) {
      val random = new Random()
      val minObstacleHeight = 50.0
      val maxObstacleHeight = gamePane.height.value - obstacleGap - 50.0

      val topObstacleHeight = minObstacleHeight + random.nextDouble() * (maxObstacleHeight - minObstacleHeight)
      val bottomObstacleHeight = gamePane.height.value - topObstacleHeight - obstacleGap

      val topObstacle = new ImageView {
        image = new scalafx.scene.image.Image(obstacleTopTemplate.image.value.impl_getUrl())
        fitWidth = obstacleWidth
        fitHeight = topObstacleHeight
        layoutX = gamePane.width.value
        layoutY = 0
      }

      val bottomObstacle = new ImageView {
        image = new scalafx.scene.image.Image(obstacleBottomTemplate.image.value.impl_getUrl())
        fitWidth = obstacleWidth
        fitHeight = bottomObstacleHeight
        layoutX = gamePane.width.value
        layoutY = topObstacleHeight + obstacleGap
      }

      obstacles = (topObstacle, bottomObstacle, false) :: obstacles
      Platform.runLater {
        gamePane.children.addAll(topObstacle, bottomObstacle)
      }

      lastObstacleSpawnTime = now

      if (obstacleSpawnInterval > minObstacleSpawnInterval) {
        obstacleSpawnInterval -= intervalDecrement
      }
    }
  }

  def getObstacles: List[(ImageView, ImageView, Boolean)] = obstacles

  def updatePassedStatus(obstacle: ImageView): Unit = {
    obstacles = obstacles.map {
      case (`obstacle`, bottomObstacle, passed) => (obstacle, bottomObstacle, true)
      case (topObstacle, `obstacle`, passed) => (topObstacle, obstacle, true)
      case other => other
    }
  }

  override def reset(): Unit = {
    Platform.runLater {
      obstacles.foreach { case (topObstacle, bottomObstacle, _) =>
        gamePane.children.removeAll(topObstacle, bottomObstacle)
      }

      obstacles = List.empty[(ImageView, ImageView, Boolean)]
    }

    lastObstacleSpawnTime = 0L

    obstacleSpawnInterval = 1200000000L
  }
}