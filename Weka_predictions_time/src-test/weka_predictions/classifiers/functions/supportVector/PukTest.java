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

package weka_predictions.classifiers.functions.supportVector;

import weka_predictions.classifiers.functions.supportVector.AbstractKernelTest;
import weka_predictions.classifiers.functions.supportVector.Kernel;
import weka_predictions.classifiers.functions.supportVector.Puk;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests Puk. Run from the command line with:<p/>
 * java weka.classifiers.functions.supportVector.PukTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class PukTest 
  extends AbstractKernelTest {

  public PukTest(String name) { 
    super(name);  
  }

  /** Creates a default Puk */
  public Kernel getKernel() {
    return new Puk();
  }

  public static Test suite() {
    return new TestSuite(PukTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }
}
