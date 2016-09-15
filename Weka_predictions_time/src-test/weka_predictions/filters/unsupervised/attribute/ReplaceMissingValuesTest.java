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
 * Copyright (C) 2002 University of Waikato 
 */

package weka_predictions.filters.unsupervised.attribute;

import weka_predictions.core.Attribute;
import weka_predictions.core.Instances;
import weka_predictions.filters.AbstractFilterTest;
import weka_predictions.filters.Filter;
import weka_predictions.filters.unsupervised.attribute.ReplaceMissingValues;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests ReplaceMissingValues. Run from the command line with:<p>
 * java weka.filters.unsupervised.attribute.ReplaceMissingValuesTest
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision: 8034 $
 */
public class ReplaceMissingValuesTest extends AbstractFilterTest {
  
  public ReplaceMissingValuesTest(String name) { super(name);  }

  /** Creates a default ReplaceMissingValues */
  public Filter getFilter() {
    return new ReplaceMissingValues();
  }

  public void testTypical() {
    Instances result = useFilter();
    // Number of attributes and instances shouldn't change
    assertEquals(m_Instances.numAttributes(), result.numAttributes());
    assertEquals(m_Instances.numInstances(), result.numInstances());
    for (int j = 0; j < m_Instances.numAttributes(); j++) {
      Attribute inatt = m_Instances.attribute(j);
      Attribute outatt = result.attribute(j);
      for (int i = 0; i < m_Instances.numInstances(); i++) {
        if (m_Instances.attribute(j).isString()) {
          if (m_Instances.instance(i).isMissing(j)) {
            assertTrue("Missing values in strings cannot be replaced",
                   result.instance(i).isMissing(j));
          } else {
            assertEquals("String values should not have changed",
                         inatt.value((int)m_Instances.instance(i).value(j)),
                         outatt.value((int)result.instance(i).value(j)));
          }
        } else {
          assertTrue("All non-string missing values should have been replaced",
                 !result.instance(i).isMissing(j));
        }
      }
    }
  }

  public static Test suite() {
    return new TestSuite(ReplaceMissingValuesTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }

}
