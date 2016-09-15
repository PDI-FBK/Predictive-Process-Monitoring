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
 * Copyright 2010 University of Waikato, Hamilton, New Zealand
 */

package weka_predictions.classifiers.pmml.consumer;

import weka_predictions.core.FastVector;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the pmml SupportVectorMachineModel classifier.
 *
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 * @version $Revision: 8034 $
 */
public class SupportVectorMachineModelTest extends AbstractPMMLClassifierTest {

  public SupportVectorMachineModelTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    m_modelNames = new FastVector();
    m_dataSetNames = new FastVector();
    m_modelNames.addElement("Audit_SVM.xml");
    m_dataSetNames.addElement("Audit_SVM_small.arff");
  }

  public static Test suite() {
    return new TestSuite(weka_predictions.classifiers.pmml.consumer.SupportVectorMachineModelTest.class);
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}