package com.uniformlyrandom.slogr

import org.slf4j.{Marker, Logger => Underlying}

import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object Slogr {

  /**
    * Create a [[Slogr]] wrapping the given underlying [[org.slf4j.Logger]].
    */
  def apply(underlying: Underlying): Slogr =
    new Slogr(underlying)
}

final class Slogr private (val underlying: Underlying = null) {

  // Error

  def error(message: String)(implicit mDCContext: MDCContext): Int =
    macro LoggerMacro.errorMessage

  def error(message: String, cause: Throwable)(
      implicit mDCContext: MDCContext): Int =
    macro LoggerMacro.errorMessageCause

  def error(message: String, args: AnyRef*)(
      implicit mDCContext: MDCContext): Int =
    macro LoggerMacro.errorMessageArgs

  // Warn

  def warn(message: String)(implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.warnMessage

  def warn(message: String, cause: Throwable)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.warnMessageCause

  def warn(message: String, args: AnyRef*)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.warnMessageArgs

  // Info

  def info(message: String)(implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.infoMessage

  def info(message: String, cause: Throwable)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.infoMessageCause

  def info(message: String, args: AnyRef*)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.infoMessageArgs

  def info(marker: Marker, message: String)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.infoMessageMarker

  def info(marker: Marker, message: String, cause: Throwable)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.infoMessageCauseMarker

  def info(marker: Marker, message: String, args: AnyRef*)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.infoMessageArgsMarker

  // Debug

  def debug(message: String)(implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.debugMessage

  def debug(message: String, cause: Throwable)(
      implicit mDCContext: MDCContext): Unit =
    macro LoggerMacro.debugMessageCause

  def debug(message: String, args: AnyRef*)(
      implicit mDCContext: MDCContext): Unit = {

    mDCContext.getValues.foreach { case ((k, v)) => org.slf4j.MDC.put(k, v) }
    mDCContext.getValues.keys.foreach(org.slf4j.MDC.remove)
  }
  //    macro LoggerMacro.debugMessageArgs

}

private object LoggerMacro {

  type LoggerContext = blackbox.Context { type PrefixType = Slogr }

  // Error

  def errorMessage(c: LoggerContext)(message: c.Expr[String])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Int] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Int](q"""
      if ($underlying.isErrorEnabled) {
          val errId = ($message + java.util.UUID.randomUUID().toString).hashCode
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          org.slf4j.MDC.put("errorId", errId)
          $underlying.error($message)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
          org.slf4j.MDC.remove("errorId")
          errId
        } else  -1
      """)
  }

  def errorMessageCause(c: LoggerContext)(message: c.Expr[String],
                                          cause: c.Expr[Throwable])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Int] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Int](q"""
        if ($underlying.isErrorEnabled) {
          val errId = ($message + java.util.UUID.randomUUID().toString).hashCode
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          org.slf4j.MDC.put("errorId", errId)
          $underlying.error($message, $cause)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
          org.slf4j.MDC.remove("errorId")
          errId
        } else  -1
      """)
  }

  def errorMessageArgs(c: LoggerContext)(
      message: c.Expr[String],
      args: c.Expr[AnyRef]*)(mDCContext: c.Expr[MDCContext]): c.Expr[Int] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Int](if (args.length == 2) {
      q"""
       if ($underlying.isErrorEnabled) {
          val errId = ($message + java.util.UUID.randomUUID().toString).hashCode
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          org.slf4j.MDC.put("errorId", errId)
          $underlying.error($message, List(${args(0)}, ${args(0)}): _* )
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
          org.slf4j.MDC.remove("errorId")
          errId
        } else  -1
      """
    } else {
      q"""
       if ($underlying.isErrorEnabled) {
          val errId = ($message + java.util.UUID.randomUUID().toString).hashCode
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          org.slf4j.MDC.put("errorId", errId)
          $underlying.error($message, ..$args)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
          org.slf4j.MDC.remove("errorId")
          errId
        } else  -1
      """
    })
  }

  // Warn

  def warnMessage(c: LoggerContext)(message: c.Expr[String])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Unit](q"""
        if ($underlying.isWarnEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.warn($message)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
  }

  def warnMessageCause(c: LoggerContext)(message: c.Expr[String],
                                         cause: c.Expr[Throwable])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Unit](q"""
        if ($underlying.isWarnEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.warn($message,$cause)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
  }

  def warnMessageArgs(c: LoggerContext)(
      message: c.Expr[String],
      args: c.Expr[AnyRef]*)(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    if (args.length == 2) {
      c.Expr[Unit](q"""
        if ($underlying.isWarnEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.warn($message, List(${args(0)}, ${args(1)}): _*)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
    } else {
      c.Expr[Unit](q"""
        if ($underlying.isWarnEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.warn($message, ..$args)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
    }
  }

  // Info

  def infoMessageMarker(c: LoggerContext)(
      marker: c.Expr[Marker],
      message: c.Expr[String])(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] =
    infoMessageImpl(c)(message)(mDCContext)

  def infoMessageCauseMarker(c: LoggerContext)(
      marker: c.Expr[Marker],
      message: c.Expr[String],
      cause: c.Expr[Throwable])(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] =
    infoMessageCauseImpl(c)(message, cause)(mDCContext)

  def infoMessageArgsMarker(c: LoggerContext)(
      marker: c.Expr[Marker],
      message: c.Expr[String],
      args: c.Expr[AnyRef]*)(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] =
    infoMessageArgsImpl(c)(message, args: _*)(mDCContext)

  def infoMessage(c: LoggerContext)(message: c.Expr[String])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] =
    infoMessageImpl(c)(message)(mDCContext)

  def infoMessageCause(c: LoggerContext)(
      message: c.Expr[String],
      cause: c.Expr[Throwable])(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] =
    infoMessageCauseImpl(c)(message, cause)(mDCContext)

  def infoMessageArgs(c: LoggerContext)(
      message: c.Expr[String],
      args: c.Expr[AnyRef]*)(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] =
    infoMessageArgsImpl(c)(message, args: _*)(mDCContext)

  def infoMessageImpl(c: LoggerContext)(message: c.Expr[String])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Unit](q"""
        if ($underlying.isInfoEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.info($message)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)

  }

  def infoMessageCauseImpl(c: LoggerContext)(message: c.Expr[String],
                                             cause: c.Expr[Throwable])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Unit](q"""
        if ($underlying.isInfoEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.info($message, $cause)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
  }

  def infoMessageArgsImpl(c: LoggerContext)(
      message: c.Expr[String],
      args: c.Expr[AnyRef]*)(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    if (args.length == 2) {
      c.Expr[Unit](q"""
        if ($underlying.isInfoEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.info($message, List(${args(0)}, ${args(1)}): _*)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
    } else {
      c.Expr[Unit](q"""
        if ($underlying.isInfoEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.info($message, ..$args)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
    }
  }

  // Debug

  def debugMessage(c: LoggerContext)(message: c.Expr[String])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Unit](q"""
        if ($underlying.isDebugEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.debug($message)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
  }

  def debugMessageCause(c: LoggerContext)(message: c.Expr[String],
                                          cause: c.Expr[Throwable])(
      mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    c.Expr[Unit](q"""
        if ($underlying.isDebugEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.debug($message, $cause)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
  }

  def debugMessageArgs(c: LoggerContext)(
      message: c.Expr[String],
      args: c.Expr[AnyRef]*)(mDCContext: c.Expr[MDCContext]): c.Expr[Unit] = {
    import c.universe._
    val underlying = q"${c.prefix}.underlying"
    if (args.length == 2) {
      c.Expr[Unit](q"""
        if ($underlying.isDebugEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.debug($message, List(${args(0)}, ${args(1)}): _*)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
      """)
    } else {
      c.Expr[Unit](q"""
        if ($underlying.isDebugEnabled) {
          $mDCContext.getValues.foreach { case ((k,v)) => org.slf4j.MDC.put(k,v) }
          $underlying.debug($message, ..$args)
          $mDCContext.getValues.keys.foreach(key => org.slf4j.MDC.remove(key))
        }
        """)
    }
  }
}
