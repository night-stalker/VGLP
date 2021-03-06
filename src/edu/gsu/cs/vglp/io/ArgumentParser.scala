package edu.gsu.cs.vglp.io

import net.sourceforge.argparse4j.ArgumentParsers
import java.io.{FileOutputStream, PrintStream, File, OutputStream}
import net.sourceforge.argparse4j.inf.ArgumentParserException

/**
 * Created with IntelliJ IDEA.
 * User: aartyomenko
 * Date: 6/7/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
object ArgumentParser {

  private val READS_PARAMETER = "reads"
  private val K_PARAMETER = "k"
  private val OUTPUT_PARAMETER = "out"
  private val MCN_PARAMETER = "mcn"
  private val MCM_PARAMETER = "mcm"
  private val THRESHOLD_PARAMETER = "tr"
  private val OUTPUT_HAPLOTYPES_PARAMETER = "outh"
  private val CONSENSUS_PARAMETER = "consensus"

  /**
   * Method for parsing command line parameters
   * and handle mistakes in parameters list
   * (Monte Carlo version)
   * @param args
   * Array of command line parameters
   * @return
   * Tuple of parsed and converted parameters
   */
  @deprecated
  def parseMC(args: Array[String]) = {
    val parser = ArgumentParsers.newArgumentParser("KGEM")
      .description("Error correction based on KGEM.")

    var k = 50
    var fl: File = null
    var out = System.out
    var tr = 0.05
    var mcn = 4
    var mcm = 3

    parser.addArgument(READS_PARAMETER)
      .metavar("ReadsFile")
      .help("File containing preprocessed sequencing data"
      + " file with extension (.txt) or (.sam) "
      + "reads in extended format")
      .`type`(classOf[File])

    parser.addArgument("-k").dest(K_PARAMETER)
      .metavar("SampleSize")
      .`type`(classOf[Integer])
      .help("Parameter k - the size of sample being randomly chosen "
      + "as seeds. Depends on expectation of variability expected "
      + "on exploring region (Default: " + k + ")")

    parser.addArgument("-o", "--out").dest(OUTPUT_PARAMETER)
      .metavar("Output")
      .setDefault[PrintStream](out)
      .`type`(classOf[FileOutputStream])
      .help("Output file name. (Default: stdout)")

    parser.addArgument("-tr").dest(THRESHOLD_PARAMETER)
      .metavar("Threshold")
      .`type`(classOf[java.lang.Double])
      .help("Threshold - the percentage level threshold. On each " +
      "step genotypes with frequency below will be dropped (Default: " + tr + ")")

    parser.addArgument("-mcn").dest(MCN_PARAMETER)
      .metavar("n")
      .`type`(classOf[Integer])
      .help("Number of runs in Monte Carlo series (Default: " + mcn + ")")

    parser.addArgument("-mcm").dest(MCM_PARAMETER)
      .metavar("m")
      .`type`(classOf[Integer])
      .help("Minimum number of repeats for Monte Carlo experiment (Default: " + mcm + ")")


    try {
      val n = parser.parseArgs(args)
      val kk = n.get(K_PARAMETER).asInstanceOf[Int]
      k = if (kk > 1) kk else k
      fl = n.get(READS_PARAMETER).asInstanceOf[File]
      val outO = n.get(OUTPUT_PARAMETER)
      if (outO.isInstanceOf[FileOutputStream]) out = new PrintStream(outO.asInstanceOf[OutputStream])
      val trtmp = n.get(THRESHOLD_PARAMETER)
      if (trtmp != null) tr = trtmp.asInstanceOf[Double]
      val mcnt = n.get(MCN_PARAMETER)
      if (mcnt != null) mcn = mcnt.asInstanceOf[Int]
      val mcmt = n.getInt(MCM_PARAMETER)
      if (mcmt != null) mcm = mcmt.asInstanceOf[Int]
    } catch {
      case e: ArgumentParserException => {
        parser.handleError(e)
        System.exit(1)
      }
    }
    val message = ("Parameters:\n" +
      "Number of runc in MC:               %d\n" +
      "Minimum repeats to count:           %d\n" +
      "Threshold (%s):                      %.2f\n" +
      "Size of sample for seeds selection: %d\n" +
      "Path of the input file: %s")
    println(message.format(mcn, mcm, "%", tr, k, fl.getAbsolutePath))
    tr *= 0.01
    (k, mcn, mcm, tr, fl, out)
  }

  /**
   * Method for parsing command line parameters
   * and handle mistakes in parameters list
   * (Max Hamming Distance version)
   * @param args
   * Array of command line parameters
   * @return
   * Tuple of parsed and converted parameters
   */
  def parseMaxHD(args: Array[String]) = {
    val parser = ArgumentParsers.newArgumentParser("KGEM")
      .description("Error correction based on KGEM.")

    var k = 50
    var fl: File = null
    var out = System.out
    var outh = System.out
    var tr = 3
    var cfl: File = null

    parser.addArgument(READS_PARAMETER)
      .metavar("ReadsFile")
      .help("File containing preprocessed sequencing data"
      + " file with extension (.txt) or (.sam) "
      + "reads in extended format")
      .`type`(classOf[File])

    parser.addArgument("-g").dest(CONSENSUS_PARAMETER)
      .metavar("ConsensusFile")
      .help("File containing consensus sequence"
      + " file in fasta format")
      .`type`(classOf[File])

    parser.addArgument("-k").dest(K_PARAMETER)
      .metavar("SampleSize")
      .`type`(classOf[Integer])
      .help("Parameter k - the size of sample being randomly chosen "
      + "as seeds. Depends on expectation of variability expected "
      + "on exploring region (Default: " + k + ")")

    parser.addArgument("-o", "--out").dest(OUTPUT_PARAMETER)
      .metavar("Output")
      .setDefault[PrintStream](out)
      .`type`(classOf[FileOutputStream])
      .help("Output file name. (Default: stdout)")

    parser.addArgument("-tr", "--threshold").dest(THRESHOLD_PARAMETER)
      .metavar("Threshold")
      .`type`(classOf[Integer])
      .help("Threshold - min Hamming distance between " +
      "seeds for init stage. (Default: " + tr + ")")

    parser.addArgument("-oh", "--outh").dest(OUTPUT_HAPLOTYPES_PARAMETER)
      .metavar("Output_hapls")
      .`type`(classOf[FileOutputStream])
      .help("Output file name for haplotypes. (Default: stdout)")


    try {
      val n = parser.parseArgs(args)
      val kk = n.get(K_PARAMETER).asInstanceOf[Int]
      k = if (kk > 1) kk else k
      fl = n.get(READS_PARAMETER).asInstanceOf[File]
      cfl = n.get(CONSENSUS_PARAMETER).asInstanceOf[File]
      val outO = n.get(OUTPUT_PARAMETER)
      if (outO.isInstanceOf[FileOutputStream]) out = new PrintStream(outO.asInstanceOf[OutputStream])
      val outH = n.get(OUTPUT_HAPLOTYPES_PARAMETER)
      if (outH.isInstanceOf[FileOutputStream]) outh = new PrintStream(outH.asInstanceOf[OutputStream])
      val trtmp = n.get(THRESHOLD_PARAMETER)
      if (trtmp != null) tr = trtmp.asInstanceOf[Int]
    } catch {
      case e: ArgumentParserException => {
        parser.handleError(e)
        System.exit(1)
      }
    }
    val message = ("Parameters:\n" +
      "Threshold:                                  %d\n" +
      "Maximal size of sample for seeds selection: %d\n" +
      "Path of the input file: %s")
    println(message.format(tr, k, fl.getAbsolutePath))
    (k, tr, fl, cfl, out, outh)
  }
}