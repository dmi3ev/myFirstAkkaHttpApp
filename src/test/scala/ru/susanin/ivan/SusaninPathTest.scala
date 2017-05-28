package ru.susanin.ivan

import org.scalatest.FunSuite

import scala.concurrent.Await
import scala.concurrent.duration._

class SusaninPathTest extends FunSuite {
  val waitTime = 5 seconds

  test("Get кинотеарт") {
    val result = Await.result(SusaninPath.get("кинотеарт"), waitTime)
    assert(result.length == 5)
  }

  test("Get яд") {
    val result = Await.result(SusaninPath.get("яд"), waitTime)
    assert(result.length == 3)
  }

  test("Get щщ") {
    val result = Await.result(SusaninPath.get("щщ"), waitTime)
    assert(result.length == 0)
  }
}
