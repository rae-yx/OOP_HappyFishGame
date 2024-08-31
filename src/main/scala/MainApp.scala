import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.layout.AnchorPane
import scalafx.scene.media.{Media, MediaPlayer}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}


object MainApp extends JFXApp {
  private val startPageResource = getClass.getResource("/StartPage.fxml")
  private val gameResource = getClass.getResource("/Game.fxml")

  private val startPageLoader = new FXMLLoader(startPageResource, NoDependencyResolver)
  private val gameLoader = new FXMLLoader(gameResource, NoDependencyResolver)

  startPageLoader.load()
  gameLoader.load()

  private val startPageRoots = new AnchorPane(startPageLoader.getRoot[javafx.scene.layout.AnchorPane])
  val gameRoots = new AnchorPane(gameLoader.getRoot[javafx.scene.layout.AnchorPane])

  private var mediaPlayer: MediaPlayer = _

  stage = new PrimaryStage {
    title = "Happy Fish"
    scene = new Scene(startPageRoots)
    resizable = false
  }

  val musicResource = getClass.getResource("/music.mp3")
  if (musicResource != null) {
    val media = new Media(musicResource.toString)
    mediaPlayer = new MediaPlayer(media)

    mediaPlayer.cycleCount = MediaPlayer.Indefinite
    mediaPlayer.volume = 0.5
    mediaPlayer.play()
  } else {
    println("Music file not found.")
  }

  def showGamePage(): Unit = {
    stage.scene = new Scene(gameRoots)
    AnchorPane.setTopAnchor(gameRoots, 0.0)
    AnchorPane.setBottomAnchor(gameRoots, 0.0)
    AnchorPane.setLeftAnchor(gameRoots, 0.0)
    AnchorPane.setRightAnchor(gameRoots, 0.0)
    gameRoots.requestFocus()

  }
}