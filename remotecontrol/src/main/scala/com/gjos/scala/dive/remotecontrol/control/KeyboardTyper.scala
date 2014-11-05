package com.gjos.scala.dive.remotecontrol.control

import java.awt.Robot
import java.awt.event.KeyEvent
import javax.swing.KeyStroke

class KeyboardTyper() {
  import Gesture._
  private val robot = new Robot()

  private var jumpKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0)
  private var crouchKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0)
  private var crouchHold = false

  def executeGesture(gesture: Gesture) = gesture match {
    case Jump => pressAndRelease(jumpKeyStroke)
    case Crouch => if (crouchHold) robot.keyPress(crouchKeyStroke.getKeyCode) else pressAndRelease(crouchKeyStroke)
    case Uncrouch => if (crouchHold) robot.keyRelease(crouchKeyStroke.getKeyCode) else pressAndRelease(crouchKeyStroke)
    case _ =>
  }

  def setJumpKey(s: String) {
    jumpKeyStroke = strToKey(s)
  }

  def setCrouchKey(s: String, hold: Boolean) {
    crouchKeyStroke = strToKey(s)
    crouchHold = hold
  }

  def jumpKeyName: String = keyToStr(jumpKeyStroke)
  def crouchKeyName: String = keyToStr(crouchKeyStroke)

  // Not exhaustive, but it doesn't really need to be atm.
  private def strToKey(s: String): KeyStroke = s match {
    case "" => KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0)
    case "ctrl" => KeyStroke.getKeyStroke(KeyEvent.VK_CONTROL, 0)
    case "shift" => KeyStroke.getKeyStroke(KeyEvent.VK_SHIFT, 0)
    case "alt" => KeyStroke.getKeyStroke(KeyEvent.VK_ALT, 0)
    case "altgr" => KeyStroke.getKeyStroke(KeyEvent.VK_ALT_GRAPH, 0)
    case "caps" => KeyStroke.getKeyStroke(KeyEvent.VK_CAPS_LOCK, 0)
    case "tab" => KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0)
    case "esc" => KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0)
    case _ => KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(s(0)), 0)
  }

  private def keyToStr(ks: KeyStroke): String = {
    val text = KeyEvent.getKeyText(ks.getKeyCode)
    val mods = KeyEvent.getKeyModifiersText(ks.getModifiers)
    Seq(mods, text).filter(_.nonEmpty).mkString(" ")
  }

  private def pressAndRelease(k: KeyStroke) {
    robot.keyPress(k.getKeyCode)
    robot.keyRelease(k.getKeyCode)
  }
}

