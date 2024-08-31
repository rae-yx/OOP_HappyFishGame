import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

@sfxml
class StartPageController(private val startPageAp: AnchorPane) {
  def startGame(): Unit = {
    MainApp.showGamePage()
  }
}