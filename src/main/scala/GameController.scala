import MainApp.gameRoots
import scalafx.animation.AnimationTimer
import scalafx.application.Platform
import scalafx.scene.image.ImageView
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.scene.layout.AnchorPane
import scalafx.scene.media.AudioClip
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

@sfxml
class GameController(
                      private val gameAp: AnchorPane,
                      private val fish: ImageView,
                      private val background: ImageView,
                      private val obstacleTopTemplate: ImageView,
                      private val obstacleBottomTemplate: ImageView,
                      private val scoreText: Text,
                      private val hintText: Text
                    ) {
  private var score = 0
  private val fishController = new Fish(fish, gameAp)
  private val obstaclesController = new Obstacles(gameAp, obstacleTopTemplate, obstacleBottomTemplate)
  private var paused = false
  private var restartText: Option[AnchorPane] = None
  private var gameStarted = false

  private val collisionSoundResource = getClass.getResource("/collision.mp3")
  private val collisionSound: AudioClip =
    if (collisionSoundResource != null) {
      new AudioClip(collisionSoundResource.toString)
    }
    else {
      println("Collision sound file not found.")
      null
    }

  val gameLoop: AnimationTimer = AnimationTimer { now =>
    if (!paused) {
      update(now)
    }
  }

  def startGame(): Unit = {
    gameLoop.start()
    gameRoots.requestFocus()
  }

  def pressed(event: KeyEvent): Unit = {
    if (!gameStarted && event.code == KeyCode.Space) {
      gameStarted = true
      hintText.visible = false
      startGame()
    } else if (paused && event.code == KeyCode.Space) {
      restartGame()
    } else if (event.code == KeyCode.Space) {
      fishController.swim()
    }
  }

  private def update(now: Long): Unit = {
    fishController.update()
    obstaclesController.moveObstacles()

    if (fishController.checkCollision(obstaclesController.getObstacles)) {
      if (collisionSound != null) {
        collisionSound.play()
      }
      pauseGame()
    } else {
      obstaclesController.spawnObstacle(now)
      updateScore()
    }

    if (fishController.isDead) fishController.reset()
  }

  private def updateScore(): Unit = {
    val obstacles = obstaclesController.getObstacles

    for ((topObstacle, bottomObstacle, passed) <- obstacles if !passed && fish.layoutX.get() > topObstacle.layoutX.get() + topObstacle.fitWidth.get()) {
      score += 1
      obstaclesController.updatePassedStatus(topObstacle)
      updateScoreText()
    }
  }

  private def updateScoreText(): Unit = {
    scoreText.text = score.toString
  }

  private def pauseGame(): Unit = {
    paused = true
    displayRestartText()
  }

  private def displayRestartText(): Unit = {
    Platform.runLater {
      val overlayResource = getClass.getResource("/RestartOverlay.fxml")
      val overlayLoader = new FXMLLoader(overlayResource, NoDependencyResolver)

      overlayLoader.load()
      val overlayPane = new AnchorPane(overlayLoader.getRoot[javafx.scene.layout.AnchorPane])

      gameAp.children.add(overlayPane)
      restartText = Some(overlayPane)
    }
  }

  private def removeOverlay(): Unit = {
    Platform.runLater {
      restartText.foreach(gameAp.children.remove)
      restartText = None
    }
  }

  private def restartGame(): Unit = {
    paused = false
    fishController.reset()
    obstaclesController.reset()
    score = 0
    updateScoreText()
    removeOverlay()
    gameLoop.start()
    gameRoots.requestFocus()
  }
}