/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2006 University of Waikato, Hamilton, New Zealand
 */

package weka_predictions.filters.unsupervised.attribute;

import weka_predictions.core.Instances;
import weka_predictions.filters.AbstractFilterTest;
import weka_predictions.filters.Filter;
import weka_predictions.filters.unsupervised.attribute.ClassAssigner;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests ClassAssigner. Run from the command line with:<p>
 * java weka.filters.unsupervised.attribute.ClassAssignerTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class ClassAssignerTest extends AbstractFilterTest {
  
  public ClassAssignerTest(String name) { 
    super(name);
  }

  /** Creates a default ClassAssigner */
  public Filter getFilter() {
    return new ClassAssigner();
  }

  public void testTypical() {
    Instances result = useFilter();
    // Number of attributes and instances shouldn't change
    assertEquals(m_Instances.numAttributes(), result.numAttributes());
    assertEquals(m_Instances.numInstances(), result.numInstances());
  }

  public static Test suite() {
    return new TestSuite(ClassAssignerTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }
}
