package org.knowm.xchart.internal.chartpart;

import java.math.BigDecimal;
import org.knowm.xchart.internal.Utils;
import org.knowm.xchart.internal.chartpart.Axis.Direction;
import org.knowm.xchart.style.AxesChartStyler;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for
 * rendering the axis ticks for logarithmic axes
 *
 * @author timmolter
 */
class AxisTickCalculator_Logarithmic extends AxisTickCalculator_ {

  private final Formatter_LogNumber formatterLogNumber;

  /**
   * Constructor
   *
   * @param axisDirection
   * @param workingSpace
   * @param minValue
   * @param maxValue
   * @param styler
   */
  public AxisTickCalculator_Logarithmic(
      Direction axisDirection,
      double workingSpace,
      double minValue,
      double maxValue,
      AxesChartStyler styler) {

    super(axisDirection, workingSpace, minValue, maxValue, styler);
    formatterLogNumber = new Formatter_LogNumber(styler, axisDirection);
    axisFormat = formatterLogNumber;
    calculate();
  }

  /**
   * Constructor
   *
   * @param axisDirection
   * @param workingSpace
   * @param minValue
   * @param maxValue
   * @param styler
   * @param yIndex
   */
  public AxisTickCalculator_Logarithmic(
      Direction axisDirection,
      double workingSpace,
      double minValue,
      double maxValue,
      AxesChartStyler styler,
      int yIndex) {

    super(axisDirection, workingSpace, minValue, maxValue, styler);
    formatterLogNumber = new Formatter_LogNumber(styler, axisDirection, yIndex);
    axisFormat = formatterLogNumber;
    calculate();
  }

  @Override
  protected void calculate() {

    // a check if all axis data are the exact same values
    if (minValue == maxValue) {
      tickLabels.add(formatterLogNumber.format(BigDecimal.valueOf(maxValue).doubleValue()));
      tickLocations.add(workingSpace / 2.0);
      return;
    }

    // tick space - a percentage of the working space available for ticks
    double tickSpace = styler.getPlotContentSize() * workingSpace; // in plot space

    // this prevents an infinite loop when the plot gets sized really small.
    if (tickSpace < styler.getXAxisTickMarkSpacingHint()) {
      return;
    }

    // where the tick should begin in the working space in pixels
    double margin =
        Utils.getTickStartOffset(
            workingSpace,
            tickSpace); // in plot space double gridStep = getGridStepForDecimal(tickSpace);

    // System.out.println("minValue: " + minValue);
    // System.out.println("maxValue: " + maxValue);
    int logMin = (int) Math.floor(Math.log10(minValue));
    int logMax = (int) Math.ceil(Math.log10(maxValue));
    // System.out.println("logMin: " + logMin);
    // System.out.println("logMax: " + logMax);

    // if (axisDirection == Direction.Y && styler.getYAxisMin() != null) {
    // logMin = (int) (Math.log10(styler.getYAxisMin())); // no floor
    // }
    // if (axisDirection == Direction.Y && styler.getYAxisMax() != null) {
    // logMax = (int) (Math.log10(styler.getYAxisMax())); // no floor
    // }
    // if (axisDirection == Direction.X && styler.getXAxisMin() != null) {
    // logMin = (int) (Math.log10(styler.getXAxisMin())); // no floor
    // }
    // if (axisDirection == Direction.X && styler.getXAxisMax() != null) {
    // logMax = (int) (Math.log10(styler.getXAxisMax())); // no floor
    // }

    int firstPosition = 1;
    // System.out.println("firstPosition: " + firstPosition);
    double tickStep = Utils.pow(10, logMin - 1);

    boolean axisDecadeOnly =
        (axisDirection == Direction.X)
            ? styler.isXAxisLogarithmicDecadeOnly()
            : styler.isYAxisLogarithmicDecadeOnly();

    for (int i = logMin; i <= logMax; i++) { // for each decade

      // System.out.println("tickStep: " + tickStep);
      // System.out.println("firstPosition: " + firstPosition);
      // System.out.println("i: " + i);
      // System.out.println("Utils.pow(10, i): " + Utils.pow(10, i));

      for (int j = firstPosition; j <= 10; j++) {
        double tickValue = Math.pow(10, i) * j;

        // System.out.println("tickValue: " + tickValue);
        // System.out.println(Math.log10(tickValue) % 1);

        if (tickValue < minValue - tickStep) {
          // System.out.println("continue");
          continue;
        }

        if (tickValue > maxValue + tickStep) {
          // System.out.println("break");
          break;
        }

        // only add labels for the decades
        if (!axisDecadeOnly || j == 1 || j == 10) {
          tickLabels.add(formatterLogNumber.format(tickValue));
        } else {
          tickLabels.add(null);
        }

        // add all the tick marks though
        double tickLabelPosition =
            (int)
                (margin
                    + (Math.log10(tickValue) - Math.log10(minValue))
                        / (Math.log10(maxValue) - Math.log10(minValue))
                        * tickSpace);
        tickLocations.add(tickLabelPosition);
      }
      tickStep = Math.pow(10, i);
      firstPosition = 2;
    }
    if (tickLocations.size() <= 1) {
      tickLabels.add(formatterLogNumber.format(minValue));
      tickLocations.add(margin);
      tickLabels.add(formatterLogNumber.format(maxValue));
      tickLocations.add(margin + tickSpace);
    }
  }
}