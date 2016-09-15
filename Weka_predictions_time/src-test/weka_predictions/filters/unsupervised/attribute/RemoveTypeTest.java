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
import weka_predictions.core.SelectedTag;
import weka_predictions.filters.AbstractFilterTest;
import weka_predictions.filters.Filter;
import weka_predictions.filters.unsupervised.attribute.RemoveType;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests RemoveType. Run from the command line with:<p>
 * java weka.filters.unsupervised.attribute.RemoveTypeTest
 *
 * @author <a href="mailto:len@reeltwo.com">Len Trigg</a>
 * @version $Revision: 8034 $
 */
public class RemoveTypeTest extends AbstractFilterTest {
  
  public RemoveTypeTest(String name) { super(name);  }

  /** Creates a default RemoveType */
  public Filter getFilter() {
    return new RemoveType();
  }

  /** Creates a specialized RemoveType */
  public Filter getFilter(int attType) {
    
    RemoveType af = new RemoveType();
    try {
      af.setAttributeType(new SelectedTag(attType,
                                          RemoveType.TAGS_ATTRIBUTETYPE));
    } catch (Exception ex) {
      ex.printStackTrace();
      fail("Couldn't set up filter with attribute type: " + attType);
    }
    return af;
  }

  public void testNominalFiltering() {
    m_Filter = getFilter(Attribute.NOMINAL);
    Instances result = useFilter();
    for (int i = 0; i < result.numAttributes(); i++) {
      assertTrue(result.attribute(i).type() != Attribute.NOMINAL);
    }
  }

  public void testStringFiltering() {
    m_Filter = getFilter(Attribute.STRING);
    Instances result = useFilter();
    for (int i = 0; i < result.numAttributes(); i++) {
      assertTrue(result.attribute(i).type() != Attribute.STRING);
    }
  }

  public void testNumericFiltering() {
    m_Filter = getFilter(Attribute.NUMERIC);
    Instances result = useFilter();
    for (int i = 0; i < result.numAttributes(); i++) {
      assertTrue(result.attribute(i).type() != Attribute.NUMERIC);
    }
  }

  public void testDateFiltering() {
    m_Filter = getFilter(Attribute.DATE);
    Instances result = useFilter();
    for (int i = 0; i < result.numAttributes(); i++) {
      assertTrue(result.attribute(i).type() != Attribute.DATE);
    }
  }

  public static Test suite() {
    return new TestSuite(RemoveTypeTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }

}
