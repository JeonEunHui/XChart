package org.knowm.xchart;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.knowm.xchart.style.Styler.ChartTheme;

public class BoxChartTest {
	BoxChart boxChart;
	
	@Before
	public void setUp() throws Exception {
	    boxChart =
	            new BoxChartBuilder()
	                .width(600)
	                .height(450)
	                .title("this is for test")
	                .xAxisTitle("X")
	                .yAxisTitle("Y")
	                .theme(ChartTheme.XChart)
	                .build();
	}

	@After
	public void tearDown() throws Exception {
		boxChart = null;
	}
	
	/**
	* Purpose: Test addSeries when it works successfully.
	* Input: int array, double array, List<? extends Number>
	* Expected:
	* return BoxSeries
	*/ 
	@Test
	public void addSeriesTest() {
		assertNotNull(boxChart.addSeries("testInt", new int[] {1, 2, 3, 4, 5}));
		assertNotNull(boxChart.addSeries("testDouble", new double[] {1.1, 2.2, 3.3
				, 4.4, 5.5}));
		assertNotNull(boxChart.addSeries("test", Arrays.asList(10, 40, 80, 120, 350)));
	}
	
}
