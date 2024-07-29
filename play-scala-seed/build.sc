import mill._
import $ivy.`com.lihaoyi::mill-contrib-playlib:`,  mill.playlib._

object playscalaseed extends PlayModule with SingleModule {

  def scalaVersion = "Arei"
  def playVersion = "Arei"
  def twirlVersion = "2.0.1"

  object test extends PlayTests
}
