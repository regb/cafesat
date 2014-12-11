package cafesat

import util.Logger

object Settings {

  var stats = false

  var timeout: Option[Int] = None

  var restartInterval: Int = 0
  var restartFactor: Double = 1.1

  var logLevel: Logger.LogLevel = Logger.Warning

  /*
   * SMT-LIB interpreter command-line/programmable options (by opposition to SMT-LIB scripting options)
   */

  //We would like to turn off :print-success for integration testing
  var printSuccess: Option[Boolean] = None //None means one should use default value of interpreter
  //tell the SMT-LIB interpreter to use the logger from context instead of default logger specified by standard
  //var useContextLogger: Boolean = false

}
