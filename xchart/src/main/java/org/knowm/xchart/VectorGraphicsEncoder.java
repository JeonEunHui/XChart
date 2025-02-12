package org.knowm.xchart;

import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.Processor;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.eps.EPSProcessor;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.knowm.xchart.internal.chartpart.Chart;

/**
 * A helper class with static methods for saving Charts as vectors
 *
 * @author timmolter
 */
public final class VectorGraphicsEncoder {

  /** Constructor - Private constructor to prevent instantiation */
  private VectorGraphicsEncoder() {}

  /** Write a chart to a file. */
  public static void saveVectorGraphic(
      Chart chart, String fileName, VectorGraphicsFormat vectorGraphicsFormat) throws IOException {
    FileOutputStream file = new FileOutputStream(addFileExtension(fileName, vectorGraphicsFormat));

    try {
      saveVectorGraphic(chart, file, vectorGraphicsFormat);
    } finally {
      file.close();
    }
  }

  /** Write a chart to an OutputStream. */
  public static void saveVectorGraphic(
      Chart chart, OutputStream os, VectorGraphicsFormat vectorGraphicsFormat) throws IOException {
    final Processor p;

    switch (vectorGraphicsFormat) {
      case EPS:
        p = new EPSProcessor();
        break;
      case PDF:
        p = new PDFBoxProcessor();
        break;
      case SVG:
        p = new SVGProcessor();
        break;

      default:
        throw new UnsupportedOperationException(
            "Unsupported vector graphics format: " + vectorGraphicsFormat);
    }

    if (VectorGraphicsFormat.PDF != vectorGraphicsFormat) {
      VectorGraphics2D vg2d = new VectorGraphics2D();
      //    vg2d.draw(new Rectangle2D.Double(0.0, 0.0, chart.getWidth(), chart.getHeight()));
      CommandSequence commands = vg2d.getCommands();

      chart.paint(vg2d, chart.getWidth(), chart.getHeight());

      PageSize pageSize = new PageSize(0.0, 0.0, chart.getWidth(), chart.getHeight());
      Document doc = p.getDocument(commands, pageSize);
      doc.writeTo(os);
    } else {
      ((PDFBoxProcessor) p).savePdf(chart, os);
    }
  }

  /**
   * Only adds the extension of the VectorGraphicsFormat to the filename if the filename doesn't
   * already have it.
   *
   * @param fileName
   * @param vectorGraphicsFormat
   * @return filename (if extension already exists), otherwise;: filename + "." + extension
   */
  public static String addFileExtension(
      String fileName, VectorGraphicsFormat vectorGraphicsFormat) {

    String fileNameWithFileExtension = fileName;
    final String newFileExtension = "." + vectorGraphicsFormat.toString().toLowerCase();
    final boolean isFileExist = fileName.length() <= newFileExtension.length();
    final boolean isFileValid = fileName.substring(fileName.length() - newFileExtension.length(), fileName.length()).equalsIgnoreCase(newFileExtension);
    if (isFileExist || !isFileValid) {
      fileNameWithFileExtension = fileName + newFileExtension;
    }
    return fileNameWithFileExtension;
  }

  public enum VectorGraphicsFormat {
    EPS,
    PDF,
    SVG
  }

  private static class PDFBoxProcessor implements Processor {

    @Override
    public Document getDocument(CommandSequence arg0, PageSize arg1) {

      return null;
    }

    public void savePdf(Chart chart, OutputStream os) throws IOException {

      PdfboxGraphicsEncoder.savePdfboxGraphics(chart, os);
    }
  }
}
