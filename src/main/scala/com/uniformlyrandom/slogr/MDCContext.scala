package com.uniformlyrandom.slogr

trait MDCContext {
  def getValues: Map[String, String]
}
