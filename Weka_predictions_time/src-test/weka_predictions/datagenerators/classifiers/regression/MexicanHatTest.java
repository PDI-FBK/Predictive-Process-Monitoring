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
 * Copyright (C) 2005 University of Waikato, Hamilton, New Zealand
 */

package weka_predictions.datagenerators.classifiers.regression;

import weka_predictions.datagenerators.AbstractDataGeneratorTest;
import weka_predictions.datagenerators.DataGenerator;
import weka_predictions.datagenerators.classifiers.regression.MexicanHat;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests MexicanHat. Run from the command line with:<p/>
 * java weka.datagenerators.classifiers.regression.MexicanHatTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8034 $
 */
public class MexicanHatTest 
  extends AbstractDataGeneratorTest {

  public MexicanHatTest(String name) { 
    super(name);  
  }

  /** Creates a default MexicanHat */
  public DataGenerator getGenerator() {
    return new MexicanHat();
  }

  public static Test suite() {
    return new TestSuite(MexicanHatTest.class);
  }

  public static void main(String[] args){
    junit.textui.TestRunner.run(suite());
  }
}
