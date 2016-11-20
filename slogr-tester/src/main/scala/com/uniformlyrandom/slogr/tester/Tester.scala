package com.uniformlyrandom.slogr.tester

import com.uniformlyrandom.slogr.{MDCContext, Slogr}
import org.slf4j.LoggerFactory

object Tester {

  val logger = Slogr(LoggerFactory.getLogger(getClass))

  case class LoggingContext(
                           appName: String,
                           version: Int,
                           randomData: String
                           ) extends MDCContext {
    override def getValues: Map[String, String] =
      getClass.getDeclaredFields.foldLeft(Map.empty[String,String]) {
        (out,field) =>
          field.setAccessible(true)
          out + (field.getName -> field.get(this).toString)
      }
  }


  def main(args: Array[String]): Unit = {

    implicit val lc = LoggingContext("myapp",3, "randomDatazzz")
    logger.info("this is a logger message")
    logger.debug("this is a debug messsage")
    logger.error("this is a debug messsage", new RuntimeException("I'm bad"))



  }
}